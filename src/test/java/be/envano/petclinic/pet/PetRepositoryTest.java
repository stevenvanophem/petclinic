package be.envano.petclinic.pet;

import be.envano.petclinic.owner.OwnerTestFactory;
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
@Import(PetRepository.class)
class PetRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:18.2"))
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private PetRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    @DisplayName("Calling nextId should increment the sequence")
    void testIncrement() {
        Pet.Id first = repository.nextId();
        Pet.Id second = repository.nextId();

        assertThat(first.toLong()).isEqualTo(1L);
        assertThat(second.toLong()).isEqualTo(2L);
    }

    @Test
    @DisplayName("I can insert a new pet")
    void testInsert() {
        insertOwner();

        Pet result = repository.add(new Pet(PetTestFactory.Leo.createRehydrateCommand()));

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(PetTestFactory.Leo.ID);
        assertThat(result.name()).isEqualTo(PetTestFactory.Leo.NAME);
        assertThat(result.version()).isEqualTo(0);

        int records = repository.findAll().size();
        assertThat(records).isEqualTo(1);
    }

    @Test
    @DisplayName("I can update an existing pet")
    void testUpdate() {
        insertOwner();

        Pet given = repository.add(new Pet(PetTestFactory.Leo.createRehydrateCommand()));

        PetCommand.Rehydrate command = new PetCommand.Rehydrate(
            given.id(),
            PetTestFactory.Rosy.NAME,
            given.birthDate(),
            given.type(),
            given.ownerId(),
            given.version()
        );

        Pet result = repository.update(new Pet(command));

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(PetTestFactory.Leo.ID);
        assertThat(result.name()).isEqualTo(PetTestFactory.Rosy.NAME);
        assertThat(result.version()).isEqualTo(1);

        List<Pet> items = repository.findAll();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.getFirst().name()).isEqualTo(PetTestFactory.Rosy.NAME);
        assertThat(items.getFirst().version()).isEqualTo(1);
    }

    private void insertOwner() {
        jdbcClient.sql("""
            INSERT INTO owner (ID, FIRST_NAME, LAST_NAME, ADDRESS, TELEPHONE, CITY, VERSION)
            VALUES (:id, :firstName, :lastName, :address, :telephone, :city, :version)
            """)
            .param("id", OwnerTestFactory.GeorgeFranklin.ID.toLong())
            .param("firstName", OwnerTestFactory.GeorgeFranklin.FIRST_NAME)
            .param("lastName", OwnerTestFactory.GeorgeFranklin.LAST_NAME)
            .param("address", OwnerTestFactory.GeorgeFranklin.ADDRESS_VALUE)
            .param("telephone", OwnerTestFactory.GeorgeFranklin.TELEPHONE_VALUE)
            .param("city", OwnerTestFactory.GeorgeFranklin.CITY_VALUE)
            .param("version", 0)
            .update();
    }

}
