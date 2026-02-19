package be.envano.petclinic.specialty.internal.rest;

import be.envano.petclinic.platform.journal.support.TestJournal;
import be.envano.petclinic.specialty.SpecialtyEvent;
import be.envano.petclinic.specialty.support.SpecialtyTestFactory;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SpecialtyControllerIntegrationTest {

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
    SpecialtyControllerIntegrationTest(
        JdbcClient jdbcClient,
        TestJournal journal
    ) {
        this.journal = journal;
        this.jdbcClient = jdbcClient;
    }

    @AfterEach
    void tearDown(@Autowired JdbcClient jdbcClient) {
        journal.events().clear();
        JdbcTestUtils.deleteFromTables(jdbcClient, "specialty");
    }

    @Test
    @DisplayName("I can register a new specialty")
    void testRegister() {
        final var request = new RestModel.PostRequest(
            SpecialtyTestFactory.Radiology.NAME.toString()
        );

        ResponseEntity<RestModel.Response> response = RestClient.create("http://localhost:" + port)
            .post()
            .uri("/specialties")
            .body(request)
            .retrieve()
            .toEntity(RestModel.Response.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));

        RestModel.Response result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo(SpecialtyTestFactory.Radiology.NAME.toString());
        assertThat(result.version()).isEqualTo(0);

        List<Object> events = journal.events();
        assertThat(events.size()).isEqualTo(1);
        assertThat(events.getFirst()).isInstanceOf(SpecialtyEvent.Registered.class);

        SpecialtyTableRecord record = jdbcClient.sql("select * from specialty")
            .query((rs, rowNum) -> createTableRecord(rs))
            .optional()
            .orElseThrow();

        assertThat(record).isNotNull();
        assertThat(record.id()).isEqualTo(1L);
        assertThat(record.name()).isEqualTo(SpecialtyTestFactory.Radiology.NAME.toString());
        assertThat(record.version()).isEqualTo(0);
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
