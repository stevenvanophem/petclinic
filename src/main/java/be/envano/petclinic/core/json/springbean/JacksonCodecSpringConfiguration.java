package be.envano.petclinic.core.json.springbean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import be.envano.petclinic.core.json.JsonCodec;
import be.envano.petclinic.core.json.jackson.JacksonJsonCodec;

@Configuration
public class JacksonCodecSpringConfiguration {

	@Bean
	public JsonCodec jsonCodec() {
		return new JacksonJsonCodec();
	}

}
