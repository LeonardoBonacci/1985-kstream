package guru.bonacci.kafka.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.google.common.collect.ImmutableMap;

public class JacksonDeserializerTest {


	@Test
	public void deserialize() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> input = ImmutableMap.of("foo", "bar");
		byte[] buffer = mapper.writeValueAsBytes(input);
		try (JacksonDeserializer<JsonNode> deserializer = new JacksonDeserializer<>(JsonNode.class, mapper)) {
			JsonNode actual = deserializer.deserialize("dummy", buffer);
			assertNotNull(actual);
			assertTrue(actual.isObject());
			JsonNode converted = mapper.convertValue(input, JsonNode.class);
			assertEquals(converted, actual);
		}
	}

	@Test
	public void deseriSmile() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper(new SmileFactory());

		Map<String, String> input = ImmutableMap.of("foo", "bar");
		byte[] buffer = mapper.writeValueAsBytes(input);
		try (JacksonDeserializer<JsonNode> deserializer = new JacksonDeserializer<>(JsonNode.class, mapper)) {
			JsonNode actual = deserializer.deserialize("dummy", buffer);
			assertNotNull(actual);
			assertTrue(actual.isObject());
			JsonNode converted = mapper.convertValue(input, JsonNode.class);
			assertEquals(converted, actual);
		}
	}

	@Test
	public void classAlreadyDefined() {
		try (JacksonDeserializer<String> deserializer = new JacksonDeserializer<>(String.class)) {
			assertEquals(String.class, deserializer.getType());
		}	
	}

	@Test
	public void deserializeNull() {
		try (JacksonDeserializer<JsonNode> deserializer = new JacksonDeserializer<>(JsonNode.class)) {
			assertNull(deserializer.deserialize("dummy", null));
		}
	}

	@Test
	public void deserializeBadJson() {
		try (JacksonDeserializer<JsonNode> deserializer = new JacksonDeserializer<>(JsonNode.class)) {
			assertThrows(SerializationException.class, () -> {
				deserializer.deserialize("dummy", "{".getBytes());
			});
		}	
	}
}