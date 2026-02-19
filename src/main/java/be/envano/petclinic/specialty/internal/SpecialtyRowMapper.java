package be.envano.petclinic.specialty.internal;

import be.envano.petclinic.specialty.Specialty;
import be.envano.petclinic.specialty.SpecialtyCommand;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

class SpecialtyRowMapper implements RowMapper<SpecialtyAggregate> {

	@Override
	public SpecialtyAggregate mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
		final var id = new Specialty.Id(rs.getLong("id"));
		final var name = new Specialty.Name(rs.getString("name"));
		final int version = rs.getInt("version");

		var command = new SpecialtyCommand.Load(
			id, name, version
		);

		return SpecialtyAggregate.load(command);
	}

}
