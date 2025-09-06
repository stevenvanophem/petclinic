package be.envano.petclinic.core.journal.jdbc;

import java.util.Objects;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;

import be.envano.petclinic.core.journal.Journal;
import be.envano.petclinic.core.json.JsonCodec;

public class JdbcJournal implements Journal {

	private final JdbcClient jdbcClient;
	private final JsonCodec jsonCodec;

	public JdbcJournal(JdbcClient jdbcClient, JsonCodec jsonCodec) {
		this.jsonCodec = jsonCodec;
		this.jdbcClient = jdbcClient;
	}

	@Override
	public void appendEvent(Object event) {
		Objects.requireNonNull(event);

		String sql = """
			INSERT INTO journal (
				type,
				payload_json
			) VALUES (
				:eventType,
				:payloadJson
			)
			""";

		SqlParameterSource namedParamSource = new MapSqlParameterSource()
			.addValue("eventType", event.getClass().getName())
			.addValue("payloadJson", jsonCodec.encode(event));

		jdbcClient.sql(sql)
			.paramSource(namedParamSource)
			.update();
	}

}
