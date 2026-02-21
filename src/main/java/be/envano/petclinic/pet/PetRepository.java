package be.envano.petclinic.pet;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
class PetRepository {

    private static final PetRowMapper ROW_MAPPER = new PetRowMapper();

    private final JdbcClient jdbcClient;

    PetRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    Pet.Id nextId() {
        String sql = """
            SELECT nextval('pet_sequence')
            """;

        long value = jdbcClient.sql(sql)
            .query(Number.class)
            .single()
            .longValue();

        return Pet.Id.fromLong(value);
    }

    Pet add(Pet pet) {
        Objects.requireNonNull(pet);

        if (existsByOwnerAndName(pet.ownerId(), pet.name()))
            throw new PetException.DuplicateName();

        String sql = """
            INSERT INTO pet (ID, NAME, BIRTH_DATE, TYPE, OWNER_ID, VERSION)
            VALUES (:id, :name, :birthDate, :type, :ownerId, :version)
            """;

        jdbcClient.sql(sql)
            .param("id", pet.id().value())
            .param("name", pet.name().toString())
            .param("birthDate", pet.birthDate().value())
            .param("type", pet.type().toString())
            .param("ownerId", pet.ownerId().toLong())
            .param("version", pet.version())
            .update();

        return pet;
    }

    Pet update(Pet pet) {
        Objects.requireNonNull(pet);

        if (existsByOwnerAndNameExceptId(pet.ownerId(), pet.name(), pet.id()))
            throw new PetException.DuplicateName();

        final int newVersion = pet.version() + 1;

        String sql = """
            UPDATE pet
            SET NAME = :name, VERSION = :newVersion
            WHERE ID = :id AND VERSION = :currentVersion
            """;

        int rows = jdbcClient.sql(sql)
            .param("name", pet.name().toString())
            .param("newVersion", newVersion)
            .param("id", pet.id().value())
            .param("currentVersion", pet.version())
            .update();

        if (rows == 0)
            throw new PetException.VersionConflict();

        return new Pet(new PetCommand.Rehydrate(
            pet.id(),
            pet.name(),
            pet.birthDate(),
            pet.type(),
            pet.ownerId(),
            newVersion
        ));
    }

    Optional<Pet> findById(Pet.Id id) {
        String sql = """
            SELECT ID, NAME, BIRTH_DATE, TYPE, OWNER_ID, VERSION
            FROM pet
            WHERE ID = :id
            """;

        return jdbcClient.sql(sql)
            .param("id", id.value())
            .query(ROW_MAPPER)
            .optional();
    }

    List<Pet> findAll() {
        String sql = """
            SELECT ID, NAME, BIRTH_DATE, TYPE, OWNER_ID, VERSION
            FROM pet
            ORDER BY ID
            """;

        return jdbcClient.sql(sql)
            .query(ROW_MAPPER)
            .list();
    }

    private boolean existsByOwnerAndName(Pet.OwnerId ownerId, Pet.Name name) {
        String sql = """
            SELECT EXISTS(
                SELECT 1
                FROM pet
                WHERE OWNER_ID = :ownerId
                  AND NAME = :name
            )
            """;

        return jdbcClient.sql(sql)
            .param("ownerId", ownerId.toLong())
            .param("name", name.toString())
            .query(Boolean.class)
            .single();
    }

    private boolean existsByOwnerAndNameExceptId(Pet.OwnerId ownerId, Pet.Name name, Pet.Id id) {
        String sql = """
            SELECT EXISTS(
                SELECT 1
                FROM pet
                WHERE OWNER_ID = :ownerId
                  AND NAME = :name
                  AND ID <> :id
            )
            """;

        return jdbcClient.sql(sql)
            .param("ownerId", ownerId.toLong())
            .param("name", name.toString())
            .param("id", id.value())
            .query(Boolean.class)
            .single();
    }

}
