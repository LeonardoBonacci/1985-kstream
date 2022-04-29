package guru.bonacci.kafka.serialization;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerde<T> implements Serde<T> {

	private final JacksonSerializer<T> serializer;
	private final JacksonDeserializer<T> deserializer;

	
	public JacksonSerde(Class<T> type) {
		this(type, ObjectMapperSupplier.get());
	}

	public JacksonSerde(Class<T> type, boolean smile) {
		this(type, ObjectMapperSupplier.get(smile));
	}

	public JacksonSerde(Class<T> type, ObjectMapper objectMapper) {
		this.serializer = new JacksonSerializer<T>(objectMapper);
		this.deserializer = new JacksonDeserializer<T>(type, objectMapper);
	}
	
	public static final <T> JacksonSerde<T> of(Class<T> cls) {
		return new JacksonSerde<>(cls);
	}

	public static final <T> JacksonSerde<T> of(Class<T> cls, boolean smile) {
		return new JacksonSerde<>(cls, smile);
	}

	public static final <T> JacksonSerde<T> of(Class<T> cls, ObjectMapper objectMapper) {
		return new JacksonSerde<>(cls, objectMapper);
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		this.serializer.configure(configs, isKey);
		this.deserializer.configure(configs, isKey);
	}

	@Override
	public void close() {
		serializer.close();
		deserializer.close();
	}

	@Override
	public Serializer<T> serializer() {
		return serializer;
	}

	@Override
	public Deserializer<T> deserializer() {
		return deserializer;
	}
}