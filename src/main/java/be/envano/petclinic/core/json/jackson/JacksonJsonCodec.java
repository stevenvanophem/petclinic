package be.envano.petclinic.core.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import be.envano.petclinic.core.json.JsonCodecException;
import be.envano.petclinic.core.json.JsonCodec;

public class JacksonJsonCodec implements JsonCodec {

	private final ObjectMapper objectMapper = new ObjectMapper();

	public String encode(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			throw new JsonCodecException(e);
		}
	}

	public <T> T decode(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			throw new JsonCodecException(e);
		}
	}

}
