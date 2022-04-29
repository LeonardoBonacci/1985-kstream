package guru.bonacci.kafka.serialization;

import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer<T> implements Serializer<T> {

	private final ObjectMapper mapper;

	public JacksonSerializer() {
		this(ObjectMapperSupplier.get());
	}

	public JacksonSerializer(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {}

	@Override
	public byte[] serialize(String topic, T data) {
		if (data == null)
			return null;

		try {
			return mapper.writeValueAsBytes(data);
		} catch (final Exception e) {
			throw new SerializationException("Error serializing JSON message", e);
		}
	}

	@Override
	public void close() {
	}
}