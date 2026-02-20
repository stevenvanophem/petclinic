package be.envano.petclinic.vet.rest;

import be.envano.petclinic.platform.journal.support.TestJournal;
import be.envano.petclinic.vet.VetEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VetRestControllerIntegrationTest {

    private @LocalServerPort int port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:18.2"))
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    private final JdbcClient jdbcClient;
    private final TestJournal journal;

    @Autowired
    VetRestControllerIntegrationTest(
        JdbcClient jdbcClient,
        TestJournal journal
    ) {
        this.jdbcClient = jdbcClient;
        this.journal = journal;
    }

    @AfterEach
    void tearDown(@Autowired JdbcClient jdbcClient) {
        journal.events().clear();
        JdbcTestUtils.deleteFromTables(jdbcClient, "vet_specialty", "vet", "specialty");
    }

    @Test
    @DisplayName("I can hire a new vet")
    void testHire() {
        createSpecialty(1L, "radiology");
        createSpecialty(2L, "surgery");

        ResponseEntity<VetRestModel.Response> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/vets")
            .body(new VetRestModel.PostRequest("James", "Carter", List.of(1L, 2L)))
            .retrieve()
            .toEntity(VetRestModel.Response.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        VetRestModel.Response result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.id()).isGreaterThan(0L);
        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.lastName()).isEqualTo("Carter");
        assertThat(result.specialtyIds().size()).isEqualTo(2);
        assertThat(result.version()).isEqualTo(0);
        assertThat(journal.events().getFirst()).isInstanceOf(VetEvent.Hired.class);
    }

    @Test
    @DisplayName("I get duplicate-name conflict when hiring same full name twice")
    void testDuplicateName() {
        createSpecialty(1L, "radiology");
        VetRestModel.PostRequest request = new VetRestModel.PostRequest("James", "Carter", List.of(1L));

        RestClient.create("http://localhost:" + port)
            .post()
            .uri("/vets")
            .body(request)
            .retrieve()
            .toEntity(VetRestModel.Response.class);

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/vets")
            .body(request)
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(409));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:vet:duplicate-name");
    }

    @Test
    @DisplayName("I get not-found when renaming missing vet")
    void testRenameMissingVet() {
        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .put()
            .uri("/vets/{id}/name", 99L)
            .body(new VetRestModel.RenameRequest("Sam", "Baker", 0))
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:vet:not-found");
    }

    @Test
    @DisplayName("I get version-conflict when specializing with stale version")
    void testSpecializeVersionConflict() {
        createSpecialty(1L, "radiology");
        createSpecialty(2L, "surgery");
        createSpecialty(3L, "dentistry");

        ResponseEntity<VetRestModel.Response> created = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/vets")
            .body(new VetRestModel.PostRequest("James", "Carter", List.of(1L)))
            .retrieve()
            .toEntity(VetRestModel.Response.class);

        VetRestModel.Response vet = created.getBody();
        assertThat(vet).isNotNull();

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .put()
            .uri("/vets/{id}/specialties", vet.id())
            .body(new VetRestModel.SpecializeRequest(List.of(2L, 3L), vet.version() + 1))
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(409));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:vet:version-conflict");
    }

    @Test
    @DisplayName("I can fire a vet and receive fired status")
    void testFire() {
        createSpecialty(1L, "radiology");
        ResponseEntity<VetRestModel.Response> created = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/vets")
            .body(new VetRestModel.PostRequest("James", "Carter", List.of(1L)))
            .retrieve()
            .toEntity(VetRestModel.Response.class);

        VetRestModel.Response vet = created.getBody();
        assertThat(vet).isNotNull();

        ResponseEntity<VetRestModel.FireResponse> response = RestClient.create("http://localhost:" + port)
            .method(HttpMethod.DELETE)
            .uri("/vets/{id}", vet.id())
            .body(new VetRestModel.FireRequest(vet.version()))
            .retrieve()
            .toEntity(VetRestModel.FireResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        VetRestModel.FireResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo("fired");

        int rows = JdbcTestUtils.countRowsInTable(jdbcClient, "vet");
        assertThat(rows).isEqualTo(0);
    }

    @Test
    @DisplayName("I can de-specialize an existing vet")
    void testDeSpecialize() {
        createSpecialty(1L, "radiology");
        createSpecialty(2L, "surgery");

        ResponseEntity<VetRestModel.Response> created = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/vets")
            .body(new VetRestModel.PostRequest("James", "Carter", List.of(1L, 2L)))
            .retrieve()
            .toEntity(VetRestModel.Response.class);

        VetRestModel.Response vet = created.getBody();
        assertThat(vet).isNotNull();

        ResponseEntity<VetRestModel.Response> response = RestClient.create("http://localhost:" + port)
            .method(HttpMethod.DELETE)
            .uri("/vets/{id}/specialties/{specialtyId}", vet.id(), 1L)
            .body(new VetRestModel.DeSpecializeRequest(vet.version()))
            .retrieve()
            .toEntity(VetRestModel.Response.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        VetRestModel.Response body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.specialtyIds().size()).isEqualTo(1);
        assertThat(body.specialtyIds().contains(1L)).isFalse();
    }

    private void createSpecialty(long id, String name) {
        jdbcClient.sql("""
            INSERT INTO specialty (ID, NAME, VERSION)
            VALUES (:id, :name, 0)
            """)
            .param("id", id)
            .param("name", name)
            .update();
    }

    @TestConfiguration
    static class LocalConfiguration {

        @Bean
        @Primary
        TestJournal testJournal() {
            return new TestJournal();
        }

    }

}
