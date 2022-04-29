package guru.bonacci.kafka.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.kafka.common.serialization.Serde;
import org.junit.jupiter.api.Test;

import guru.bonacci.kafka.serialization.model.Foo;
import guru.bonacci.kafka.serialization.model.NestedFoo;

public class JacksonSerdeTest {

	@Test
	public void of() {
		Serde<Foo> serde = JacksonSerde.of(Foo.class);

		Foo expected = new Foo("first", "second");
		byte[] buffer = serde.serializer().serialize("topic", expected);
		Foo actual = serde.deserializer().deserialize("topic", buffer);

		assertNotNull(actual);
		assertEquals(expected, actual);
		serde.close();
	}
	
	@Test
	public void smile() {
		Serde<Foo> serde = JacksonSerde.of(Foo.class, true);

		Foo expected = new Foo("first", "second");
		byte[] buffer = serde.serializer().serialize("topic", expected);
		Foo actual = serde.deserializer().deserialize("topic", buffer);
		
		assertNotNull(actual);
		assertEquals(expected, actual);
		serde.close();
	}

	@Test
	public void nested() {
		Serde<NestedFoo> serde = JacksonSerde.of(NestedFoo.class);

		NestedFoo nexpected = new NestedFoo(new Foo("first", "second"), "third");
		byte[] buffer = serde.serializer().serialize("topic", nexpected);
		NestedFoo nactual = serde.deserializer().deserialize("topic", buffer);

		assertNotNull(nactual);
		assertEquals(nexpected, nactual);
		serde.close();
	}

	@Test
	public void nestedSmile() {
		Serde<NestedFoo> serde = JacksonSerde.of(NestedFoo.class, true);

		NestedFoo nexpected = new NestedFoo(new Foo("first", "second"), "third");
		byte[] buffer = serde.serializer().serialize("topic", nexpected);
		NestedFoo nactual = serde.deserializer().deserialize("topic", buffer);

		assertNotNull(nactual);
		assertEquals(nexpected, nactual);
		serde.close();
	}
}