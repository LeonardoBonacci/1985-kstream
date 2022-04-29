package guru.bonacci.kafka.serialization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NestedFoo {
	public Foo aFoo;
	public String aBar;
}
