package be.envano.petclinic.vet;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

class VetRowMapper implements RowMapper<Vet> {

    @Override
    public Vet mapRow(ResultSet rs, int rowNum) throws SQLException {
        Vet.Id id = Vet.Id.fromLong(rs.getLong("id"));
        Vet.Name name = Vet.Name.fromStrings(
            rs.getString("first_name"),
            rs.getString("last_name")
        );
        int version = rs.getInt("version");

        return new Vet(new VetCommand.Rehydrate(
            id,
            name,
            List.of(),
            version
        ));
    }

}
