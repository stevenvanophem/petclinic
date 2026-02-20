package be.envano.petclinic.vet;

import be.envano.petclinic.specialty.Specialty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
class VetRepository {

    private static final VetRowMapper ROW_MAPPER = new VetRowMapper();

    private final JdbcClient jdbcClient;

    VetRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    Vet.Id nextId() {
        String sql = """
            SELECT nextval('vet_sequence')
            """;

        long value = jdbcClient.sql(sql)
            .query(Number.class)
            .single()
            .longValue();

        return Vet.Id.fromLong(value);
    }

    Vet add(Vet vet) {
        Objects.requireNonNull(vet);

        if (existsByName(vet.name()))
            throw new VetException.DuplicateName();

        String sql = """
            INSERT INTO vet (ID, FIRST_NAME, LAST_NAME, VERSION)
            VALUES (:id, :firstName, :lastName, :version)
            """;

        jdbcClient.sql(sql)
            .param("id", vet.id().value())
            .param("firstName", vet.name().first().toString())
            .param("lastName", vet.name().last().toString())
            .param("version", vet.version())
            .update();

        replaceSpecialties(vet.id(), vet.specialties());
        return vet;
    }

    Vet update(Vet vet) {
        Objects.requireNonNull(vet);

        if (existsByNameExceptId(vet.name(), vet.id()))
            throw new VetException.DuplicateName();

        final int newVersion = vet.version() + 1;

        String sql = """
            UPDATE vet
            SET FIRST_NAME = :firstName, LAST_NAME = :lastName, VERSION = :newVersion
            WHERE ID = :id AND VERSION = :currentVersion
            """;

        int rows = jdbcClient.sql(sql)
            .param("firstName", vet.name().first().toString())
            .param("lastName", vet.name().last().toString())
            .param("newVersion", newVersion)
            .param("id", vet.id().value())
            .param("currentVersion", vet.version())
            .update();

        if (rows == 0)
            throw new VetException.VersionConflict();

        replaceSpecialties(vet.id(), vet.specialties());

        return new Vet(new VetCommand.Rehydrate(
            vet.id(),
            vet.name(),
            vet.specialties(),
            newVersion
        ));
    }

    Optional<Vet> findById(Vet.Id id) {
        String sql = """
            SELECT ID, FIRST_NAME, LAST_NAME, VERSION
            FROM vet
            WHERE ID = :id
            """;

        return jdbcClient.sql(sql)
            .param("id", id.value())
            .query(ROW_MAPPER)
            .optional()
            .map(this::withSpecialties);
    }

    List<Vet> findAll() {
        String sql = """
            SELECT ID, FIRST_NAME, LAST_NAME, VERSION
            FROM vet
            ORDER BY ID
            """;

        return jdbcClient.sql(sql)
            .query(ROW_MAPPER)
            .list()
            .stream()
            .map(this::withSpecialties)
            .toList();
    }

    void delete(Vet vet) {
        String sql = """
            DELETE FROM vet
            WHERE ID = :id AND VERSION = :version
            """;

        int rows = jdbcClient.sql(sql)
            .param("id", vet.id().value())
            .param("version", vet.version())
            .update();

        if (rows == 0)
            throw new VetException.VersionConflict();
    }

    private Vet withSpecialties(Vet vet) {
        List<Specialty.Id> specialties = findSpecialtiesByVetId(vet.id());
        return new Vet(new VetCommand.Rehydrate(
            vet.id(),
            vet.name(),
            specialties,
            vet.version()
        ));
    }

    private List<Specialty.Id> findSpecialtiesByVetId(Vet.Id vetId) {
        String sql = """
            SELECT SPECIALTY_ID
            FROM vet_specialty
            WHERE VET_ID = :vetId
            ORDER BY SPECIALTY_ID
            """;

        return jdbcClient.sql(sql)
            .param("vetId", vetId.value())
            .query(Long.class)
            .list()
            .stream()
            .map(Specialty.Id::fromLong)
            .toList();
    }

    private void replaceSpecialties(Vet.Id vetId, List<Specialty.Id> specialties) {
        String deleteSql = """
            DELETE FROM vet_specialty
            WHERE VET_ID = :vetId
            """;
        jdbcClient.sql(deleteSql)
            .param("vetId", vetId.value())
            .update();

        if (specialties.isEmpty())
            return;

        String insertSql = """
            INSERT INTO vet_specialty (VET_ID, SPECIALTY_ID)
            VALUES (:vetId, :specialtyId)
            """;

        for (Specialty.Id specialtyId : specialties) {
            jdbcClient.sql(insertSql)
                .param("vetId", vetId.value())
                .param("specialtyId", specialtyId.value())
                .update();
        }
    }

    private boolean existsByName(Vet.Name name) {
        String sql = """
            SELECT EXISTS(
                SELECT 1
                FROM vet
                WHERE FIRST_NAME = :firstName
                  AND LAST_NAME = :lastName
            )
            """;

        return jdbcClient.sql(sql)
            .param("firstName", name.first().toString())
            .param("lastName", name.last().toString())
            .query(Boolean.class)
            .single();
    }

    private boolean existsByNameExceptId(Vet.Name name, Vet.Id id) {
        String sql = """
            SELECT EXISTS(
                SELECT 1
                FROM vet
                WHERE FIRST_NAME = :firstName
                  AND LAST_NAME = :lastName
                  AND ID <> :id
            )
            """;

        return jdbcClient.sql(sql)
            .param("firstName", name.first().toString())
            .param("lastName", name.last().toString())
            .param("id", id.value())
            .query(Boolean.class)
            .single();
    }

}
