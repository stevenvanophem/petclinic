package be.envano.petclinic.specialty;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
class SpecialtyRepository {

	private static final SpecialtyRowMapper ROW_MAPPER = new SpecialtyRowMapper();

	private final JdbcClient jdbcClient;

	public SpecialtyRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public Specialty.Id nextId() {
		String sql = """
			SELECT nextval('specialty_sequence')
			""";

		long value = jdbcClient.sql(sql)
			.query(Number.class)
			.single()
			.longValue();

		return new Specialty.Id(value);
	}

	public Specialty add(Specialty specialty) {
		Objects.requireNonNull(specialty);

		String sql = """
			INSERT INTO specialty (ID, NAME, VERSION)
			VALUES (:id, :name, :version)
			""";

		jdbcClient.sql(sql)
			.param("id", specialty.id().toLong())
			.param("name", specialty.name().toString())
			.param("version", specialty.version())
			.update();

		return specialty;
	}

	public Specialty update(Specialty specialty) {
		Objects.requireNonNull(specialty);

		final var id = specialty.id();
		final var name = specialty.name();
		final int currentVersion = specialty.version();
		final int newVersion = currentVersion + 1;

		String sql = """
			UPDATE specialty
			SET NAME = :name, VERSION = :newVersion
			WHERE ID = :id AND VERSION = :currentVersion
			""";

		int rows = jdbcClient.sql(sql)
			.param("name", name.toString())
			.param("newVersion", newVersion)
			.param("id", id.toLong())
			.param("currentVersion", currentVersion)
			.update();

		if (rows == 0)
			throw new IllegalStateException("Specialty was modified concurrently or does not exist");

		var command = new SpecialtyCommand.Rehydrate(
			id,
			name,
			newVersion
		);

		return new Specialty(command);
	}

	public Optional<Specialty> findById(Specialty.Id id) {
		String sql = """
			SELECT ID, NAME, VERSION
			FROM specialty
			WHERE ID = :id
			""";
		return jdbcClient.sql(sql)
			.param("id", id.toLong())
			.query(ROW_MAPPER)
			.optional();
	}

	public List<Specialty> findAll() {
		String sql = """
			SELECT ID, NAME, VERSION
			FROM specialty
			ORDER BY ID
			""";

		return jdbcClient.sql(sql)
			.query(ROW_MAPPER)
			.list();
	}

}
