package be.envano.petclinic.specialty;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

class SpecialtyRowMapper implements RowMapper<Specialty> {

	@Override
	public Specialty mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
		final var id = new Specialty.Id(rs.getLong("id"));
		final var name = new Specialty.Name(rs.getString("name"));
		final int version = rs.getInt("version");

		var command = new SpecialtyCommand.Rehydrate(
			id, name, version
		);

		return new Specialty(command);
	}

}
