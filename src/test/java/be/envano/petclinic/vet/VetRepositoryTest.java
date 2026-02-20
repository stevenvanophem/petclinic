package be.envano.petclinic.vet;

import be.envano.petclinic.specialty.Specialty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
@Import(VetRepository.class)
class VetRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:18.2"))
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private VetRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    @DisplayName("Calling nextId should increment the sequence")
    void testIncrement() {
        Vet.Id first = repository.nextId();
        Vet.Id second = repository.nextId();

        assertThat(first.value()).isEqualTo(1L);
        assertThat(second.value()).isEqualTo(2L);
    }

    @Test
    @DisplayName("I can insert a new vet with specialties")
    void testInsert() {
        createSpecialty(1L, "radiology");
        createSpecialty(2L, "surgery");

        Vet result = repository.add(new Vet(new VetCommand.Rehydrate(
            VetTestFactory.JamesCarter.ID,
            VetTestFactory.JamesCarter.NAME,
            VetTestFactory.JamesCarter.SPECIALTIES,
            0
        )));

        assertThat(result.id()).isEqualTo(VetTestFactory.JamesCarter.ID);
        assertThat(result.name()).isEqualTo(VetTestFactory.JamesCarter.NAME);
        assertThat(result.specialties()).containsExactlyElementsOf(VetTestFactory.JamesCarter.SPECIALTIES);
        assertThat(result.version()).isEqualTo(0);
    }

    @Test
    @DisplayName("I can update an existing vet")
    void testUpdate() {
        createSpecialty(1L, "radiology");
        createSpecialty(2L, "surgery");
        createSpecialty(3L, "dentistry");

        Vet given = repository.add(new Vet(new VetCommand.Rehydrate(
            VetTestFactory.JamesCarter.ID,
            VetTestFactory.JamesCarter.NAME,
            VetTestFactory.JamesCarter.SPECIALTIES,
            0
        )));

        Vet.Name renamed = Vet.Name.fromStrings("Helen", "Leary");
        List<Specialty.Id> changedSpecialties = List.of(Specialty.Id.fromLong(3L));
        Vet result = repository.update(new Vet(new VetCommand.Rehydrate(
            given.id(),
            renamed,
            changedSpecialties,
            given.version()
        )));

        assertThat(result.name()).isEqualTo(renamed);
        assertThat(result.specialties()).containsExactlyElementsOf(changedSpecialties);
        assertThat(result.version()).isEqualTo(1);
    }

    @Test
    @DisplayName("I can delete an existing vet")
    void testDelete() {
        createSpecialty(1L, "radiology");
        Vet given = repository.add(new Vet(new VetCommand.Rehydrate(
            VetTestFactory.JamesCarter.ID,
            VetTestFactory.JamesCarter.NAME,
            List.of(Specialty.Id.fromLong(1L)),
            0
        )));

        repository.delete(given);

        int records = JdbcTestUtils.countRowsInTable(jdbcClient, "vet");
        assertThat(records).isEqualTo(0);
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

}
