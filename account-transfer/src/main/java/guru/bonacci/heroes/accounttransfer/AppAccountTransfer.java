package guru.bonacci.heroes.accounttransfer;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNT_TRANSFER_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_PAIR_TOPIC;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
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
public class AppAccountTransfer {

	public static void main(String[] args) {
		SpringApplication.run(AppAccountTransfer.class, args);
	}

	
  @Bean
  public KStream<String, Transfer> topology(StreamsBuilder builder) {
    final var accountSerde = new JsonSerde<Account>(Account.class);
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    
    KStream<String, Transfer> transferStream = // key: poolId.from or poolId.to
      builder
       .stream(TRANSFER_PAIR_TOPIC, Consumed.with(Serdes.String(), transferSerde))
       .peek((poolAccountId, transfer) -> log.info("in transfer {}<>{}", poolAccountId, transfer));

    KTable<String, Account> accountTable = // key: poolId.accountId
      builder
       .table(ACCOUNT_TRANSFER_TOPIC, Consumed.with(Serdes.String(), accountSerde));

    transferStream 
      .join(accountTable, (transfer, account) -> account.addTransfer(transfer))
      .peek((poolAccountId, account) -> 
              log.info("out account {}<>{} with last transfer {} ", poolAccountId, account, account.latestTransfer()))
      .to(ACCOUNT_TRANSFER_TOPIC, Produced.with(Serdes.String(), accountSerde));
   
    return transferStream;
  }
}
