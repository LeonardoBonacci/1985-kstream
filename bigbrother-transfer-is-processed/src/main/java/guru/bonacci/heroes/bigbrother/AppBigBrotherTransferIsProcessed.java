package guru.bonacci.heroes.bigbrother;

import static guru.bonacci.heroes.kafka.Constants.MAX_TRANSFER_PROCESSING_TIME_SEC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_HOUSTON_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_PAIR_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_PROCESSED_TOPIC;
import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

import java.time.Duration;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serdes.WrapperSerde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Windowed;
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
public class AppBigBrotherTransferIsProcessed {

	public static void main(String[] args) {
		SpringApplication.run(AppBigBrotherTransferIsProcessed.class, args);
	}
	 
	
  @Bean
  public KStream<String, Transfer> topology(StreamsBuilder builder) {
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    
    KStream<String, Transfer> transferStream = // keyed on poolId.accountId
      builder // rekey to transferId
        .stream(TRANSFER_PAIR_TOPIC, Consumed.with(Serdes.String(), transferSerde))
        .selectKey((poolAccountId, transfer) -> transfer.getTransferId())
        .peek((transferId, transfer) -> log.info("transfer in {}<>{}", transferId, transfer));
    
    KStream<String, Transfer> processedStream = // keyed on poolId.accountId
        builder // rekey to transferId
          .stream(TRANSFER_PROCESSED_TOPIC, Consumed.with(Serdes.String(), transferSerde))
          .selectKey((poolAccountId, transfer) -> transfer.getTransferId())
          .peek((transferId, transfer) -> log.info("processed in {}<>{}", transferId, transfer));

    KStream<String, Boolean> joinedStream = 
      transferStream
        .leftJoin(processedStream, (t1, t2) -> t2 != null, // first false, then true
          JoinWindows.of(Duration.ofSeconds(MAX_TRANSFER_PROCESSING_TIME_SEC)).before(Duration.ofMillis(0)),
          StreamJoined.with(Serdes.String(), transferSerde, transferSerde))
        .peek((transferId, isJoined) -> log.info("joined {}<>{}", transferId, isJoined));

    
    KStream<Windowed<String>, Boolean> windowed = 
      joinedStream
        .groupByKey(Grouped.with(Serdes.String(), new BooleanSerde()))
        .windowedBy(
           SessionWindows.ofInactivityGapWithNoGrace(Duration.ofSeconds(MAX_TRANSFER_PROCESSING_TIME_SEC)))
        .reduce(
            (aggr, curr) -> aggr || curr, // first false, then true
            Materialized.with(Serdes.String(), new BooleanSerde())) 
        .suppress(Suppressed.untilWindowCloses(unbounded()))
        .toStream()
        .peek((transferId, isProcessed) -> log.info("windowed {}<>{}", transferId, isProcessed));

    
    windowed // unwindow
      .map((windowedKey, isProcessed) ->  KeyValue.pair(windowedKey.key(), isProcessed))
      .filter((transferId, isProcessed) -> !isProcessed) // not processed is alarm
      .mapValues((transferId, isProcessed) -> 0L)
      .peek((transferId, isProcessed) -> log.info("out {}<>{}", transferId, isProcessed))
      .to(TRANSFER_HOUSTON_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));

    return transferStream;
  }
  
  // Why is this Serde not present in the Kafka libraries?
  static public final class BooleanSerde extends WrapperSerde<Boolean> {
    public BooleanSerde() {
        super(new BooleanSerializer(), new BooleanDeserializer());
    }
  }

  public static class BooleanSerializer implements Serializer<Boolean> {
    public byte[] serialize(String topic, Boolean data) {
        if (data == null)
            return null;

        return new byte[] {
            (byte)(data?1:0)
        };
    }
  }
  
  public static class BooleanDeserializer implements Deserializer<Boolean> {
    public Boolean deserialize(String topic, byte[] data) {
        if (data == null)
            return null;
        if (data.length > 1) {
            throw new SerializationException("Size of data received by BooleanDeserializer is not larger than 1");
        }

        return data[0]!=0;
    }
  }
}
