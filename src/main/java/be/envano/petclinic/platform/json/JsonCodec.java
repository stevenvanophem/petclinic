package be.envano.petclinic.platform.json;

public interface JsonCodec {

	String encode(Object object);

	<T> T decode(String json, Class<T> clazz);

}
