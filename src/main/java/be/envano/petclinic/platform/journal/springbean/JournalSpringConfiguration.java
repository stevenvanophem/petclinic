package be.envano.petclinic.platform.journal.springbean;

import be.envano.petclinic.platform.journal.Journal;
import be.envano.petclinic.platform.journal.jdbc.JdbcJournal;
import be.envano.petclinic.platform.json.JsonCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Objects;

@Configuration
public class JournalSpringConfiguration {

	@Bean
	public Journal journal(JdbcClient jdbcClient, JsonCodec jsonCodec) {
		Objects.requireNonNull(jdbcClient);
		Objects.requireNonNull(jsonCodec);
		return new JdbcJournal(jdbcClient, jsonCodec);
	}

}
