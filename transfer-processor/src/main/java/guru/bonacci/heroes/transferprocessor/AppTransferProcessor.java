package guru.bonacci.heroes.transferprocessor;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNT_TRANSFER_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_PROCESSED_TOPIC;

import org.apache.kafka.common.serialization.Serdes;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafkaStreams
@SpringBootApplication
public class AppTransferProcessor {

	public static void main(String[] args) {
		SpringApplication.run(AppTransferProcessor.class, args);
	}

  @Bean
  public KStream<String, Account> topology(StreamsBuilder builder) {
    final var accountSerde = new JsonSerde<Account>(Account.class);
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    
    KStream<String, Account> accountStream = // key: poolId.accountId
     builder
      .stream(ACCOUNT_TRANSFER_TOPIC, Consumed.with(Serdes.String(), accountSerde))
      .peek((poolAccountId, account) -> 
              log.info("in {}<>{} with last transfer {} ", poolAccountId, account, account.latestTransfer()));

    accountStream
      .filter((poolAccountId, account) -> account.hasTransfer())
      .mapValues((poolAccountId, account) -> account.latestTransfer())
      .peek((poolAccountId, transfer) -> log.info("out {}<>{}", poolAccountId, transfer))
      .to(TRANSFER_PROCESSED_TOPIC, Produced.with(Serdes.String(), transferSerde));    

    return accountStream;
    
  }
}
