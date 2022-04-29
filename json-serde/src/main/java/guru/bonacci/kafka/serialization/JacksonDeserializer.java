package guru.bonacci.kafka.serialization;

import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class JacksonDeserializer<T> implements Deserializer<T> {

	private final Class<T> type;
	private ObjectReader reader;

	public JacksonDeserializer(Class<T> type) {
		this(type, ObjectMapperSupplier.get());
	}

	public JacksonDeserializer(Class<T> type, ObjectMapper mapper) {
		this.type = type;
		this.reader = mapper.readerFor(type);
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {}

	@Override
	public T deserialize(String topic, byte[] data) {
		if (data == null)
			return null;

		try {
			return reader.readValue(data);
		} catch (final IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public void close() {
	}

	Class<T> getType() {
		return type;
	}
}