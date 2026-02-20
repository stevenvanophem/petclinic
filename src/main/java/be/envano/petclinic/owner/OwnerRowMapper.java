package be.envano.petclinic.owner;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class OwnerRowMapper implements RowMapper<Owner> {

    @Override
    public Owner mapRow(ResultSet rs, int rowNum) throws SQLException {
        Owner.Id id = Owner.Id.fromLong(rs.getLong("id"));
        Owner.Name name = Owner.Name.fromStrings(
            rs.getString("first_name"),
            rs.getString("last_name")
        );
        Owner.Address address = Owner.Address.fromString(rs.getString("address"));
        Owner.Telephone telephone = Owner.Telephone.fromString(rs.getString("telephone"));
        Owner.City city = Owner.City.fromString(rs.getString("city"));
        int version = rs.getInt("version");

        return new Owner(new OwnerCommand.Rehydrate(
            id,
            name,
            address,
            telephone,
            city,
            version
        ));
    }

}
