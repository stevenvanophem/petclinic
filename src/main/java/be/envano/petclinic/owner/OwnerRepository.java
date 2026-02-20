package be.envano.petclinic.owner;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
class OwnerRepository {

    private static final OwnerRowMapper ROW_MAPPER = new OwnerRowMapper();

    private final JdbcClient jdbcClient;

    OwnerRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    Owner.Id nextId() {
        String sql = """
            SELECT nextval('owner_sequence')
            """;

        long value = jdbcClient.sql(sql)
            .query(Number.class)
            .single()
            .longValue();

        return Owner.Id.fromLong(value);
    }

    Owner add(Owner owner) {
        Objects.requireNonNull(owner);

        if (existsByName(owner.name()))
            throw new OwnerException.DuplicateName();

        String sql = """
            INSERT INTO owner (ID, FIRST_NAME, LAST_NAME, ADDRESS, TELEPHONE, CITY, VERSION)
            VALUES (:id, :firstName, :lastName, :address, :telephone, :city, :version)
            """;

        jdbcClient.sql(sql)
            .param("id", owner.id().value())
            .param("firstName", owner.name().first().toString())
            .param("lastName", owner.name().last().toString())
            .param("address", owner.address().toString())
            .param("telephone", owner.telephone().toString())
            .param("city", owner.city().toString())
            .param("version", owner.version())
            .update();

        return owner;
    }

    Owner update(Owner owner) {
        Objects.requireNonNull(owner);

        if (existsByNameExceptId(owner.name(), owner.id()))
            throw new OwnerException.DuplicateName();

        final int newVersion = owner.version() + 1;

        String sql = """
            UPDATE owner
            SET FIRST_NAME = :firstName,
                LAST_NAME = :lastName,
                ADDRESS = :address,
                TELEPHONE = :telephone,
                CITY = :city,
                VERSION = :newVersion
            WHERE ID = :id AND VERSION = :currentVersion
            """;

        int rows = jdbcClient.sql(sql)
            .param("firstName", owner.name().first().toString())
            .param("lastName", owner.name().last().toString())
            .param("address", owner.address().toString())
            .param("telephone", owner.telephone().toString())
            .param("city", owner.city().toString())
            .param("newVersion", newVersion)
            .param("id", owner.id().value())
            .param("currentVersion", owner.version())
            .update();

        if (rows == 0)
            throw new OwnerException.VersionConflict();

        return new Owner(new OwnerCommand.Rehydrate(
            owner.id(),
            owner.name(),
            owner.address(),
            owner.telephone(),
            owner.city(),
            newVersion
        ));
    }

    Optional<Owner> findById(Owner.Id id) {
        String sql = """
            SELECT ID, FIRST_NAME, LAST_NAME, ADDRESS, TELEPHONE, CITY, VERSION
            FROM owner
            WHERE ID = :id
            """;

        return jdbcClient.sql(sql)
            .param("id", id.value())
            .query(ROW_MAPPER)
            .optional();
    }

    List<Owner> findAll() {
        String sql = """
            SELECT ID, FIRST_NAME, LAST_NAME, ADDRESS, TELEPHONE, CITY, VERSION
            FROM owner
            ORDER BY ID
            """;

        return jdbcClient.sql(sql)
            .query(ROW_MAPPER)
            .list();
    }

    private boolean existsByName(Owner.Name name) {
        String sql = """
            SELECT EXISTS(
                SELECT 1
                FROM owner
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

    private boolean existsByNameExceptId(Owner.Name name, Owner.Id id) {
        String sql = """
            SELECT EXISTS(
                SELECT 1
                FROM owner
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
