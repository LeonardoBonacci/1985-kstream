package guru.bonacci.heroes.transfersplitter;

import static guru.bonacci.heroes.domain.Account.identifier;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_PAIR_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_TOPIC;
import static java.util.Arrays.asList;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import guru.bonacci.heroes.domain.Transfer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafkaStreams
@SpringBootApplication
public class AppTransferSplitter {

	public static void main(String[] args) {
		SpringApplication.run(AppTransferSplitter.class, args);
	}

	
	@Bean
	public KStream<String, Transfer> topology(StreamsBuilder builder) {
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);

	  KStream<String, Transfer> transferStream = // key: poolId.from
     builder 
      .stream(TRANSFER_TOPIC, Consumed.with(Serdes.String(), transferSerde))
      .peek((poolFrom, transfer) -> log.info("in {}<>{}", poolFrom, transfer));
	  
	  KStream<String, Transfer> rekeyed = 
      transferStream.flatMap((key, value) ->  // split in..
          asList(KeyValue.pair(identifier(value.getPoolId(), value.getFrom()), value.negativeClone()), // from
                 KeyValue.pair(identifier(value.getPoolId(), value.getTo()), value))); // to

	  rekeyed // inverse TimestampExtractor
	     .transformValues(() -> new ValueTransformer<Transfer, Transfer>() {
	       
        private ProcessorContext context;

        @Override
        public void init(ProcessorContext processorContext) {
            this.context = processorContext;
        }

        @Override
        public Transfer transform(Transfer transfer) {
            transfer.setWhen(context.timestamp()); // Java: lots of code for one line
            return transfer;
        }

        @Override
        public void close() {
        }
	    })
	    
      .peek((poolAccountId, transfer) -> log.info("out {}<>{}", poolAccountId, transfer))
	    .to(TRANSFER_PAIR_TOPIC, Produced.with(Serdes.String(), transferSerde));
  	return transferStream;
	}
}
