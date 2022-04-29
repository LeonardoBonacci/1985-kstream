package guru.bonacci.kafka.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Map;
import java.util.stream.Stream;

import org.apache.kafka.common.serialization.Serializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings({"unchecked", "rawtypes"})
public class JacksonSerializerTest {

	Serializer serializer;

	@BeforeEach
	public void before() {
		this.serializer = new JacksonSerializer();
	}

	@Test
	public void serializeNull() {
		assertNull(this.serializer.serialize("dummy", null));
	}

	@TestFactory
	public Stream<DynamicTest> serialize() {
		Map<String, Object> tests = ImmutableMap.of("{\"foo\":\"bar\"}", ImmutableMap.of("foo", "bar"));

		return tests.entrySet().stream().map(p -> dynamicTest(p.getKey(), () -> {
			byte[] buffer = this.serializer.serialize("topic", p.getValue());
			String actual = new String(buffer, Charsets.UTF_8);
			assertEquals(p.getKey(), actual);
		}));
	}
}