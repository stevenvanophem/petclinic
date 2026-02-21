package be.envano.petclinic.pet.rest;

import be.envano.petclinic.owner.Owner;
import be.envano.petclinic.owner.OwnerService;
import be.envano.petclinic.owner.OwnerTestFactory;
import be.envano.petclinic.pet.PetEvent;
import be.envano.petclinic.pet.PetTestFactory;
import be.envano.petclinic.platform.journal.support.TestJournal;
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
class PetRestControllerIntegrationTest {

    private @LocalServerPort int port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:18.2"))
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    private final JdbcClient jdbcClient;
    private final TestJournal journal;
    private final OwnerService ownerService;

    @Autowired
    PetRestControllerIntegrationTest(
        JdbcClient jdbcClient,
        TestJournal journal,
        OwnerService ownerService
    ) {
        this.jdbcClient = jdbcClient;
        this.journal = journal;
        this.ownerService = ownerService;
    }

    @AfterEach
    void tearDown(@Autowired JdbcClient jdbcClient) {
        journal.events().clear();
        JdbcTestUtils.deleteFromTables(jdbcClient, "pet", "owner");
    }

    @Test
    @DisplayName("I can register a new pet")
    void testRegister() {
        Owner owner = ownerService.register(OwnerTestFactory.GeorgeFranklin.createRegisterCommand());

        final var request = new PetRestModel.PostRequest(
            PetTestFactory.Leo.NAME_VALUE,
            PetTestFactory.Leo.BIRTH_DATE_VALUE,
            PetTestFactory.Leo.TYPE_VALUE,
            owner.id().toLong()
        );

        ResponseEntity<PetRestModel.Response> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/pets")
            .body(request)
            .retrieve()
            .toEntity(PetRestModel.Response.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));

        PetRestModel.Response result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.id()).isGreaterThan(0L);
        assertThat(result.name()).isEqualTo(PetTestFactory.Leo.NAME_VALUE);
        assertThat(result.birthDate()).isEqualTo(PetTestFactory.Leo.BIRTH_DATE_VALUE);
        assertThat(result.type()).isEqualTo(PetTestFactory.Leo.TYPE_VALUE);
        assertThat(result.ownerId()).isEqualTo(owner.id().toLong());
        assertThat(result.version()).isEqualTo(0);

        List<Object> events = journal.events();
        assertThat(events.size()).isEqualTo(2);
        assertThat(events.get(1)).isInstanceOf(PetEvent.Registered.class);

        PetTableRecord record = jdbcClient.sql("select * from pet")
            .query((rs, _) -> createTableRecord(rs))
            .optional()
            .orElseThrow();

        assertThat(record).isNotNull();
        assertThat(record.id()).isEqualTo(result.id());
        assertThat(record.name()).isEqualTo(PetTestFactory.Leo.NAME_VALUE);
        assertThat(record.birthDate()).isEqualTo(PetTestFactory.Leo.BIRTH_DATE_VALUE);
        assertThat(record.type()).isEqualTo(PetTestFactory.Leo.TYPE_VALUE);
        assertThat(record.ownerId()).isEqualTo(owner.id().toLong());
        assertThat(record.version()).isEqualTo(0);
    }

    @Test
    @DisplayName("I get validation details when pet name is blank")
    void testRegisterBlankNameValidation() {
        Owner owner = ownerService.register(OwnerTestFactory.GeorgeFranklin.createRegisterCommand());

        final var request = new PetRestModel.PostRequest(
            " ",
            PetTestFactory.Leo.BIRTH_DATE_VALUE,
            PetTestFactory.Leo.TYPE_VALUE,
            owner.id().toLong()
        );

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/pets")
            .body(request)
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:pet:validation");
        assertThat(body.getDetail()).contains("Pet name cannot be blank.");
    }

    @Test
    @DisplayName("I get not-found details when renaming a missing pet")
    void testRenameMissingPet() {
        final var request = new PetRestModel.RenameRequest(
            PetTestFactory.Rosy.NAME_VALUE,
            0
        );

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .put()
            .uri("/pets/{id}/name", 42L)
            .body(request)
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:pet:not-found");
        assertThat(body.getDetail()).isEqualTo("Pet was not found");
    }

    @Test
    @DisplayName("I get version-conflict details when renaming with stale version")
    void testRenameVersionConflict() {
        Owner owner = ownerService.register(OwnerTestFactory.GeorgeFranklin.createRegisterCommand());

        ResponseEntity<PetRestModel.Response> created = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/pets")
            .body(new PetRestModel.PostRequest(
                PetTestFactory.Leo.NAME_VALUE,
                PetTestFactory.Leo.BIRTH_DATE_VALUE,
                PetTestFactory.Leo.TYPE_VALUE,
                owner.id().toLong()
            ))
            .retrieve()
            .toEntity(PetRestModel.Response.class);

        PetRestModel.Response pet = created.getBody();
        assertThat(pet).isNotNull();

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .put()
            .uri("/pets/{id}/name", pet.id())
            .body(new PetRestModel.RenameRequest(PetTestFactory.Rosy.NAME_VALUE, pet.version() + 1))
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(409));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:pet:version-conflict");
    }

    @Test
    @DisplayName("I get duplicate-name conflict when creating same pet name for same owner")
    void testRegisterDuplicateNameConflict() {
        Owner owner = ownerService.register(OwnerTestFactory.GeorgeFranklin.createRegisterCommand());

        final var request = new PetRestModel.PostRequest(
            PetTestFactory.Leo.NAME_VALUE,
            PetTestFactory.Leo.BIRTH_DATE_VALUE,
            PetTestFactory.Leo.TYPE_VALUE,
            owner.id().toLong()
        );

        RestClient.create("http://localhost:" + port)
            .post()
            .uri("/pets")
            .body(request)
            .retrieve()
            .toEntity(PetRestModel.Response.class);

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/pets")
            .body(request)
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(409));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:pet:duplicate-name");
    }

    @Test
    @DisplayName("I get bad request when request payload is explicit JSON null")
    void testRegisterJsonNullBody() {
        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/pets")
            .contentType(MediaType.APPLICATION_JSON)
            .body("null")
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }

    private static PetTableRecord createTableRecord(ResultSet rs) throws SQLException {
        return new PetTableRecord(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getObject("birth_date", java.time.LocalDate.class),
            rs.getString("type"),
            rs.getLong("owner_id"),
            rs.getInt("version")
        );
    }

    private record PetTableRecord(
        long id,
        String name,
        java.time.LocalDate birthDate,
        String type,
        long ownerId,
        int version
    ) {}

    @TestConfiguration
    static class LocalConfiguration {

        @Bean
        @Primary
        TestJournal testJournal() {
            return new TestJournal();
        }

    }

}
