package be.envano.petclinic.pet;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

class PetRowMapper implements RowMapper<Pet> {

    @Override
    public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
        Pet.Id id = Pet.Id.fromLong(rs.getLong("id"));
        Pet.Name name = Pet.Name.fromString(rs.getString("name"));
        Pet.BirthDate birthDate = Pet.BirthDate.fromLocalDate(rs.getObject("birth_date", LocalDate.class));
        Pet.Type type = Pet.Type.fromString(rs.getString("type"));
        Pet.OwnerId ownerId = Pet.OwnerId.fromLong(rs.getLong("owner_id"));
        int version = rs.getInt("version");

        return new Pet(new PetCommand.Rehydrate(
            id,
            name,
            birthDate,
            type,
            ownerId,
            version
        ));
    }

}
