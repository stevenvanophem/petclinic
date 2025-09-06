package be.envano.petclinic.speciality.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import be.envano.petclinic.speciality.Specialty;
import be.envano.petclinic.speciality.SpecialtyCommand;

class JdbcSpecialtyRowMapper implements RowMapper<Specialty> {

	@Override
	public Specialty mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
		final var id = new Specialty.Id(rs.getLong("id"));
		final var name = new Specialty.Name(rs.getString("name"));
		final int version = rs.getInt("version");

		var command = new SpecialtyCommand.Load(
			id, name, version
		);

		return Specialty.load(command);
	}

}
