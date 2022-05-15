package guru.bonacci.heroes.transferprocessor;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafkaStreams
@SpringBootApplication
public class BootstrAppTransferProcessor {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransferProcessor.class, args);
	}

  @Bean
  public KStream<String, Account> topology(StreamsBuilder builder) {
    final var accountSerde = new JsonSerde<Account>(Account.class);
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    
    KStream<String, Account> accountStream = 
     builder
      .stream(KafkaTopicNames.ACCOUNT_TRANSFER_TOPIC, Consumed.with(Serdes.String(), accountSerde))
      .peek((k,v) -> log.info("in {}<>{}", k, v));

    accountStream
      .filter((poolAccountId, account) -> account.hasTransfer())
      .map((poolAccountId, account) -> { // rekey to transferId
      
        var latestTransfer = account.latestTransfer();
        return KeyValue.pair(latestTransfer.getTransferId(), latestTransfer);
      })
      .peek((k,v) -> log.info("out {}<>{}", k, v))
      .to(TRANSFER_EVENTUAL_TOPIC, Produced.with(Serdes.String(), transferSerde));    

    return accountStream;
    
  }
}
