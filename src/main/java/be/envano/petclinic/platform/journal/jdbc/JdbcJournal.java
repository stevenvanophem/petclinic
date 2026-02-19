package be.envano.petclinic.platform.journal.jdbc;

import be.envano.petclinic.platform.journal.Journal;
import be.envano.petclinic.platform.json.JsonCodec;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.time.Instant;
import java.util.Objects;

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
			    journal_type,
				payload_type,
				payload_json,
			    timestamp
			) VALUES (
			    'event',
				:payloadType,
				:payloadJson,
			    :timestamp
			)
			""";

		SqlParameterSource namedParamSource = new MapSqlParameterSource()
			.addValue("payloadType", event.getClass().getName())
			.addValue("payloadJson", jsonCodec.encode(event))
			.addValue("timestamp", Instant.now());

		jdbcClient.sql(sql)
			.paramSource(namedParamSource)
			.update();
	}

}
