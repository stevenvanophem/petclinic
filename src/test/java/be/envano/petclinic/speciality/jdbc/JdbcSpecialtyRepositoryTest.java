package be.envano.petclinic.speciality.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyCommand;
import be.envano.petclinic.speciality.support.SpecialtyTestFactory;

@JdbcTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JdbcSpecialtyRepository.class)
class JdbcSpecialtyRepositoryTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.6")
		.withDatabaseName("testdb")
		.withUsername("test")
		.withPassword("test");

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private JdbcSpecialtyRepository repository;

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
		Specialty result = repository.add(SpecialtyTestFactory.Surgery.load());

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
		Specialty given = repository.add(SpecialtyTestFactory.Surgery.load());

		final var renamed = new Specialty.Name("Super Surgery");
		final var command = new SpecialtyCommand.Load(
			given.id(),
			renamed,
			given.version()
		);

		Specialty result = repository.update(Specialty.load(command));

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