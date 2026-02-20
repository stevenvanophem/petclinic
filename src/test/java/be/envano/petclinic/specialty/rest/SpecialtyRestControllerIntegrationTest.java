package be.envano.petclinic.specialty.rest;

import be.envano.petclinic.platform.journal.support.TestJournal;
import be.envano.petclinic.specialty.SpecialtyEvent;
import be.envano.petclinic.specialty.SpecialtyTestFactory;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpecialtyRestControllerIntegrationTest {

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
    SpecialtyRestControllerIntegrationTest(
        JdbcClient jdbcClient,
        TestJournal journal
    ) {
        this.jdbcClient = jdbcClient;
        this.journal = journal;
    }

    @AfterEach
    void tearDown(@Autowired JdbcClient jdbcClient) {
        journal.events().clear();
        JdbcTestUtils.deleteFromTables(jdbcClient, "specialty");
    }

    @Test
    @DisplayName("I can register a new specialty")
    void testRegister() {
        final var request = new SpecialtyRestModel.PostRequest(
            SpecialtyTestFactory.Radiology.NAME.toString()
        );

        ResponseEntity<SpecialtyRestModel.Response> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/specialties")
            .body(request)
            .retrieve()
            .toEntity(SpecialtyRestModel.Response.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));

        SpecialtyRestModel.Response result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.id()).isGreaterThan(0L);
        assertThat(result.name()).isEqualTo(SpecialtyTestFactory.Radiology.NAME.toString());
        assertThat(result.version()).isEqualTo(0);

        List<Object> events = journal.events();
        assertThat(events.size()).isEqualTo(1);
        assertThat(events.getFirst()).isInstanceOf(SpecialtyEvent.Registered.class);

        SpecialtyTableRecord record = jdbcClient.sql("select * from specialty")
            .query((rs, _) -> createTableRecord(rs))
            .optional()
            .orElseThrow();

        assertThat(record).isNotNull();
        assertThat(record.id()).isEqualTo(result.id());
        assertThat(record.name()).isEqualTo(SpecialtyTestFactory.Radiology.NAME.toString());
        assertThat(record.version()).isEqualTo(0);
    }

    @Test
    @DisplayName("I get validation details when specialty name is blank")
    void testRegisterBlankNameValidation() {
        final var request = new SpecialtyRestModel.PostRequest("  ");

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/specialties")
            .body(request)
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:specialty:validation");
        assertThat(body.getDetail()).contains("specialty name cannot be blank");
    }

    @Test
    @DisplayName("I get not-found details when renaming a missing specialty")
    void testRenameMissingSpecialty() {
        final var request = new SpecialtyRestModel.RenameRequest("Surgery Prime", 0);

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .put()
            .uri("/specialties/{id}/name", 42L)
            .body(request)
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:specialty:not-found");
        assertThat(body.getDetail()).isEqualTo("Specialty was not found");
    }

    @Test
    @DisplayName("I get version-conflict details when using a stale version")
    void testRenameVersionConflict() {
        ResponseEntity<SpecialtyRestModel.Response> created = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/specialties")
            .body(new SpecialtyRestModel.PostRequest("Radiology Prime"))
            .retrieve()
            .toEntity(SpecialtyRestModel.Response.class);

        SpecialtyRestModel.Response specialty = created.getBody();
        assertThat(specialty).isNotNull();

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .put()
            .uri("/specialties/{id}/name", specialty.id())
            .body(new SpecialtyRestModel.RenameRequest("Radiology Prime Plus", specialty.version() + 1))
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(409));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:specialty:version-conflict");
    }

    @Test
    @DisplayName("I get duplicate-name conflict when creating same specialty twice")
    void testRegisterDuplicateNameConflict() {
        final String duplicatedName = "Dentistry Prime";

        RestClient.create("http://localhost:" + port)
            .post()
            .uri("/specialties")
            .body(new SpecialtyRestModel.PostRequest(duplicatedName))
            .retrieve()
            .toEntity(SpecialtyRestModel.Response.class);

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/specialties")
            .body(new SpecialtyRestModel.PostRequest(duplicatedName))
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(409));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:specialty:duplicate-name");
    }

    @Test
    @DisplayName("I get bad request when request payload is explicit JSON null")
    void testRegisterJsonNullBody() {
        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/specialties")
            .contentType(MediaType.APPLICATION_JSON)
            .body("null")
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }

    private static SpecialtyTableRecord createTableRecord(ResultSet rs) throws SQLException {
        return new SpecialtyTableRecord(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getInt("version")
        );
    }

    private record SpecialtyTableRecord(long id, String name, int version) {}

    @TestConfiguration
    static class LocalConfiguration {

        @Bean
        @Primary
        TestJournal testJournal() {
            return new TestJournal();
        }

    }

}
