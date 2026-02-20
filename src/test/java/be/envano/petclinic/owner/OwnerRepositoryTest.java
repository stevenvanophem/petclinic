package be.envano.petclinic.owner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
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
@Import(OwnerRepository.class)
class OwnerRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:18.2"))
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private OwnerRepository repository;

    @Test
    @DisplayName("Calling nextId should increment the sequence")
    void testIncrement() {
        Owner.Id first = repository.nextId();
        Owner.Id second = repository.nextId();

        assertThat(first.toLong()).isEqualTo(1L);
        assertThat(second.toLong()).isEqualTo(2L);
    }

    @Test
    @DisplayName("I can insert a new owner")
    void testInsert() {
        Owner result = repository.add(new Owner(OwnerTestFactory.JamesCarter.createRehydrateCommand()));

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(OwnerTestFactory.JamesCarter.ID);
        assertThat(result.name()).isEqualTo(OwnerTestFactory.JamesCarter.NAME);
        assertThat(result.version()).isEqualTo(0);

        int records = repository.findAll().size();
        assertThat(records).isEqualTo(1);
    }

    @Test
    @DisplayName("I can update an existing owner")
    void testUpdate() {
        Owner given = repository.add(new Owner(OwnerTestFactory.JamesCarter.createRehydrateCommand()));

        Owner.Name renamedName = OwnerTestFactory.SamBaker.NAME;
        Owner.Address updatedAddress = OwnerTestFactory.HelenLeary.ADDRESS;
        Owner.Telephone updatedTelephone = OwnerTestFactory.HelenLeary.TELEPHONE;
        Owner.City updatedCity = OwnerTestFactory.HelenLeary.CITY;
        OwnerCommand.Rehydrate command = new OwnerCommand.Rehydrate(
            given.id(),
            renamedName,
            updatedAddress,
            updatedTelephone,
            updatedCity,
            given.version()
        );

        Owner result = repository.update(new Owner(command));

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(OwnerTestFactory.JamesCarter.ID);
        assertThat(result.name()).isEqualTo(renamedName);
        assertThat(result.address()).isEqualTo(updatedAddress);
        assertThat(result.telephone()).isEqualTo(updatedTelephone);
        assertThat(result.city()).isEqualTo(updatedCity);
        assertThat(result.version()).isEqualTo(1);

        List<Owner> items = repository.findAll();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.getFirst().name()).isEqualTo(renamedName);
        assertThat(items.getFirst().version()).isEqualTo(1);
    }

}
