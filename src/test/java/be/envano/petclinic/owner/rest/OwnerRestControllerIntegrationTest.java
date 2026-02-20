package be.envano.petclinic.owner.rest;

import be.envano.petclinic.owner.Owner;
import be.envano.petclinic.owner.OwnerEvent;
import be.envano.petclinic.owner.OwnerService;
import be.envano.petclinic.owner.OwnerTestFactory;
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
class OwnerRestControllerIntegrationTest {

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
    OwnerRestControllerIntegrationTest(
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
        JdbcTestUtils.deleteFromTables(jdbcClient, "owner");
    }

    @Test
    @DisplayName("I can register a new owner")
    void testRegister() {
        final var request = new OwnerRestModel.PostRequest(
            OwnerTestFactory.JamesCarter.FIRST_NAME,
            OwnerTestFactory.JamesCarter.LAST_NAME,
            OwnerTestFactory.JamesCarter.ADDRESS_VALUE,
            OwnerTestFactory.JamesCarter.TELEPHONE_VALUE,
            OwnerTestFactory.JamesCarter.CITY_VALUE
        );

        ResponseEntity<OwnerRestModel.Response> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/owners")
            .body(request)
            .retrieve()
            .toEntity(OwnerRestModel.Response.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));

        OwnerRestModel.Response result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.id()).isGreaterThan(0L);
        assertThat(result.firstName()).isEqualTo(OwnerTestFactory.JamesCarter.FIRST_NAME);
        assertThat(result.lastName()).isEqualTo(OwnerTestFactory.JamesCarter.LAST_NAME);
        assertThat(result.address()).isEqualTo(OwnerTestFactory.JamesCarter.ADDRESS_VALUE);
        assertThat(result.telephone()).isEqualTo(OwnerTestFactory.JamesCarter.TELEPHONE_VALUE);
        assertThat(result.city()).isEqualTo(OwnerTestFactory.JamesCarter.CITY_VALUE);
        assertThat(result.version()).isEqualTo(0);

        List<Object> events = journal.events();
        assertThat(events.size()).isEqualTo(1);
        assertThat(events.getFirst()).isInstanceOf(OwnerEvent.Registered.class);

        OwnerTableRecord record = jdbcClient.sql("select * from owner")
            .query((rs, _) -> createTableRecord(rs))
            .optional()
            .orElseThrow();

        assertThat(record).isNotNull();
        assertThat(record.id()).isEqualTo(result.id());
        assertThat(record.firstName()).isEqualTo(OwnerTestFactory.JamesCarter.FIRST_NAME);
        assertThat(record.lastName()).isEqualTo(OwnerTestFactory.JamesCarter.LAST_NAME);
        assertThat(record.address()).isEqualTo(OwnerTestFactory.JamesCarter.ADDRESS_VALUE);
        assertThat(record.telephone()).isEqualTo(OwnerTestFactory.JamesCarter.TELEPHONE_VALUE);
        assertThat(record.city()).isEqualTo(OwnerTestFactory.JamesCarter.CITY_VALUE);
        assertThat(record.version()).isEqualTo(0);
    }

    @Test
    @DisplayName("I get validation details when owner first name is blank")
    void testRegisterBlankFirstNameValidation() {
        final var request = new OwnerRestModel.PostRequest(
            " ",
            OwnerTestFactory.JamesCarter.LAST_NAME,
            OwnerTestFactory.JamesCarter.ADDRESS_VALUE,
            OwnerTestFactory.JamesCarter.TELEPHONE_VALUE,
            OwnerTestFactory.JamesCarter.CITY_VALUE
        );

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/owners")
            .body(request)
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:owner:validation");
        assertThat(body.getDetail()).contains("Owner first name cannot be blank.");
    }

    @Test
    @DisplayName("I get not-found details when renaming a missing owner")
    void testRenameMissingOwner() {
        final var request = new OwnerRestModel.RenameRequest(
            OwnerTestFactory.SamBaker.FIRST_NAME,
            OwnerTestFactory.SamBaker.LAST_NAME,
            0
        );

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .put()
            .uri("/owners/{id}/name", 42L)
            .body(request)
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:owner:not-found");
        assertThat(body.getDetail()).isEqualTo("Owner was not found");
    }

    @Test
    @DisplayName("I get version-conflict details when using a stale version")
    void testRenameVersionConflict() {
        Owner owner = ownerService.register(OwnerTestFactory.JamesCarter.createRegisterCommand());

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .put()
            .uri("/owners/{id}/name", owner.id().toLong())
            .body(new OwnerRestModel.RenameRequest(
                OwnerTestFactory.SamBaker.FIRST_NAME,
                OwnerTestFactory.SamBaker.LAST_NAME,
                owner.version() + 1
            ))
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(409));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:owner:version-conflict");
    }

    @Test
    @DisplayName("I get duplicate-name conflict when creating same owner twice")
    void testRegisterDuplicateNameConflict() {
        final var request = new OwnerRestModel.PostRequest(
            OwnerTestFactory.JamesCarter.FIRST_NAME,
            OwnerTestFactory.JamesCarter.LAST_NAME,
            OwnerTestFactory.JamesCarter.ADDRESS_VALUE,
            OwnerTestFactory.JamesCarter.TELEPHONE_VALUE,
            OwnerTestFactory.JamesCarter.CITY_VALUE
        );

        RestClient.create("http://localhost:" + port)
            .post()
            .uri("/owners")
            .body(request)
            .retrieve()
            .toEntity(OwnerRestModel.Response.class);

        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/owners")
            .body(request)
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(409));
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getType().toString()).isEqualTo("urn:petclinic:owner:duplicate-name");
    }

    @Test
    @DisplayName("I get bad request when request payload is explicit JSON null")
    void testRegisterJsonNullBody() {
        ResponseEntity<ProblemDetail> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/owners")
            .contentType(MediaType.APPLICATION_JSON)
            .body("null")
            .exchange((_, res) -> ResponseEntity.status(res.getStatusCode()).body(res.bodyTo(ProblemDetail.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }

    private static OwnerTableRecord createTableRecord(ResultSet rs) throws SQLException {
        return new OwnerTableRecord(
            rs.getLong("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("address"),
            rs.getString("telephone"),
            rs.getString("city"),
            rs.getInt("version")
        );
    }

    private record OwnerTableRecord(
        long id,
        String firstName,
        String lastName,
        String address,
        String telephone,
        String city,
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
