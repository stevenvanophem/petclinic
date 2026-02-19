package be.envano.petclinic.specialty.internal;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCommand;
import be.envano.petclinic.specialty.support.SpecialtyTestFactory;
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
@Import(SpecialtyRepository.class)
class SpecialtyRepositoryTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:18.2"))
		.withDatabaseName("testdb")
		.withUsername("test")
		.withPassword("test");

	@Autowired
	private SpecialtyRepository repository;

	@Test
	@DisplayName("Calling nextId should increment the sequence")
	void testIncrement() {
		Specialty.Id first = repository.nextId();
		Specialty.Id second = repository.nextId();

		assertThat(first.toLong()).isEqualTo(1L);
		assertThat(second.toLong()).isEqualTo(2L);
	}

	@Test
	@DisplayName("I can insert a new specialty")
	void testInsert() {
		Specialty result = repository.add(SpecialtyAggregate.load(new SpecialtyCommand.Load(
			SpecialtyTestFactory.Surgery.ID,
			SpecialtyTestFactory.Surgery.NAME,
			0
		)));

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(SpecialtyTestFactory.Surgery.ID);
		assertThat(result.name()).isEqualTo(SpecialtyTestFactory.Surgery.NAME);
		assertThat(result.version()).isEqualTo(0);

		int records = repository.findAll().size();
		assertThat(records).isEqualTo(1);
	}

	@Test
	@DisplayName("I can update an existing specialty")
	void testUpdate() {
		Specialty given = repository.add(SpecialtyAggregate.load(new SpecialtyCommand.Load(
			SpecialtyTestFactory.Surgery.ID,
			SpecialtyTestFactory.Surgery.NAME,
			0
		)));

		final var renamed = new Specialty.Name("Super Surgery");
		final var command = new SpecialtyCommand.Load(
			given.id(),
			renamed,
			given.version()
		);

		Specialty result = repository.update(SpecialtyAggregate.load(command));

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(SpecialtyTestFactory.Surgery.ID);
		assertThat(result.name()).isEqualTo(renamed);
		assertThat(result.version()).isEqualTo(1);

		List<Specialty> items = repository.findAll();
		assertThat(items.size()).isEqualTo(1);
		assertThat(items.getFirst().name()).isEqualTo(renamed);
		assertThat(items.getFirst().version()).isEqualTo(1);
	}

}
