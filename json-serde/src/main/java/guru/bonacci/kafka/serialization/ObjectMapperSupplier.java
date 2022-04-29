package guru.bonacci.kafka.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

final class ObjectMapperSupplier {

    static ObjectMapper get() {
    	return get(false);
    }

    static ObjectMapper get(boolean smile) {
    	ObjectMapper mapper = smile ? new ObjectMapper(new SmileFactory()) : new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    	return mapper;
    }
}