package be.envano.petclinic.core.json;

public interface JsonCodec {

	String encode(Object object);

	<T> T decode(String json, Class<T> clazz);

}
