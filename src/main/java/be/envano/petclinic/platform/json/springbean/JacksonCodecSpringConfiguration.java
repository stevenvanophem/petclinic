package be.envano.petclinic.platform.json.springbean;

import be.envano.petclinic.platform.json.JsonCodec;
import be.envano.petclinic.platform.json.jackson.JacksonJsonCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonCodecSpringConfiguration {

	@Bean
	public JsonCodec jsonCodec() {
		return new JacksonJsonCodec();
	}

}
