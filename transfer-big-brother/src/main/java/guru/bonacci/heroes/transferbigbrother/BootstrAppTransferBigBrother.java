package guru.bonacci.heroes.transferbigbrother;

import java.time.Duration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafkaStreams
@SpringBootApplication
public class BootstrAppTransferBigBrother {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransferBigBrother.class, args);
	}

	@Bean
  public NewTopic f() {
    return TopicBuilder.name("foo2")
      .partitions(1)
      .build();
  }
	@Bean
  public NewTopic accounts() {
    return TopicBuilder.name("bar2")
      .partitions(1)
      .build();
  }

	
	 @Bean
	  public KStream<String, String> topology(StreamsBuilder builder) {
	   final var stringTupleSerde = new JsonSerde<StringTuple>(StringTuple.class);
	   
	    KStream<String, String> eventualStream = // keyed on transferId
	        builder.stream("foo2", Consumed.with(Serdes.String(), Serdes.String()))
	        .peek((k,v) -> log.info("foo {}", v));

	    KStream<String, String> consistentStream = // keyed on transferId
	          builder.stream("bar2", Consumed.with(Serdes.String(), Serdes.String()))
	         .peek((k,v) -> log.info("bar {}", v));
	     
	    KStream<String, StringTuple> joined = 
	      eventualStream.leftJoin(consistentStream,
	          (eventual, consistent) -> new StringTuple(eventual, consistent),
	          JoinWindows.of(Duration.ofMinutes(1))
	      )
	      .peek((k,v) -> log.info("joined {}", v));
	    
	    joined.groupByKey()
      .windowedBy(
          SessionWindows.ofInactivityGapWithNoGrace(Duration.ofSeconds(42)))
      .reduce((l, r) -> new StringTuple(l,r), Materialized.with(Serdes.String(), stringTupleSerde))
      .toStream()
      .peek((k,v) -> log.info("before filter {}", v))
      .filter((k,v) -> v.getTRight() == null)
      .mapValues((k,v) -> v.getTLeft())
	    .print(Printed.toSysOut());
	    return eventualStream;
	  }

//	@Bean
//	public KStream<String, Transfer> topology(StreamsBuilder builder, @Value("${max.time.difference.sec:42}") Long timeGap) {
//	  final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
//    final var transferTupleSerde = new JsonSerde<TransferTuple>(TransferTuple.class);
//	
//	  KStream<String, Transfer> eventualStream = // keyed on transferId
//	      builder.stream("foo", Consumed.with(Serdes.String(), transferSerde));
//
//	  KStream<String, Transfer> consistentStream = // keyed on transferId
//	        builder.stream("bar", Consumed.with(Serdes.String(), transferSerde));
//
////	  eventualStream.join(consistentStream, 
////	      (eventual, consistent) -> eventual,
////    	  JoinWindows.of(Duration.ofMinutes(5));//,
////        Joined.with(
////            Serdes.String(), 
////            transferSerde,   
////            transferSerde));  
//	   
//    KStream<String, TransferTuple> joined = 
//      eventualStream.leftJoin(consistentStream,
//          (eventual, consistent) -> new TransferTuple(eventual, consistent),
//          JoinWindows.of(Duration.ofMinutes(1))
//      );
//    
//    joined.print(Printed.toSysOut());
////      .to(TRANSFER_CONSISTENT_TOPIC, Produced.with(Serdes.String(), transferSerde));
//  	return eventualStream;
//	}
}
