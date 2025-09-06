package be.envano.petclinic.core.journal.springbean;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import be.envano.petclinic.core.json.JsonCodec;
import be.envano.petclinic.core.journal.Journal;
import be.envano.petclinic.core.journal.jdbc.JdbcJournal;

@Configuration
public class JournalSpringConfiguration {

	@Bean
	public Journal journal(JdbcClient jdbcClient, JsonCodec jsonCodec) {
		Objects.requireNonNull(jdbcClient);
		Objects.requireNonNull(jsonCodec);
		return new JdbcJournal(jdbcClient, jsonCodec);
	}

}
