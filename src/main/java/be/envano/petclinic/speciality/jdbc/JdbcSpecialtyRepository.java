package be.envano.petclinic.speciality.jdbc;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyCommand;
import be.envano.petclinic.speciality.SpecialtyRepository;

public class JdbcSpecialtyRepository implements SpecialtyRepository {

	private static final JdbcSpecialtyRowMapper ROW_MAPPER = new JdbcSpecialtyRowMapper();

	private final JdbcClient jdbcClient;

	public JdbcSpecialtyRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	@Override
	public Specialty.Id nextId() {
		String sql = """
			SELECT SPECIALTY_SEQUENCE.NEXTVAL 
			FROM DUAL
			""";

		long value = jdbcClient.sql(sql)
			.query(Number.class)
			.single()
			.longValue();

		return new Specialty.Id(value);
	}

	@Override
	public Specialty add(Specialty specialty) {
		Objects.requireNonNull(specialty);

		String sql = """
			INSERT INTO SPECIALTY (ID, NAME, VERSION)
			VALUES (:id, :name, :version)
			""";

		jdbcClient.sql(sql)
			.param("id", specialty.id().toLong())
			.param("name", specialty.name().toString())
			.param("version", specialty.version())
			.update();

		return specialty;
	}

	@Override
	public Specialty update(Specialty specialty) {
		Objects.requireNonNull(specialty);

		final var id = specialty.id();
		final var name = specialty.name();
		final int currentVersion = specialty.version();
		final int newVersion = currentVersion + 1;

		String sql = """
			UPDATE SPECIALTY
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

		var command = new SpecialtyCommand.Load(
			id,
			name,
			newVersion
		);

		return Specialty.load(command);
	}

	@Override
	public Optional<Specialty> findById(Specialty.Id id) {
		String sql = """
			SELECT ID, NAME, VERSION
			FROM SPECIALTY
			WHERE ID = :id
			""";
		return jdbcClient.sql(sql)
			.param("id", id.toLong())
			.query(ROW_MAPPER)
			.optional();
	}

	@Override
	public List<Specialty> findAll() {
		String sql = """
			SELECT ID, NAME, VERSION
			FROM SPECIALTY
			ORDER BY ID
			""";

		return jdbcClient.sql(sql)
			.query(ROW_MAPPER)
			.list();
	}

}
