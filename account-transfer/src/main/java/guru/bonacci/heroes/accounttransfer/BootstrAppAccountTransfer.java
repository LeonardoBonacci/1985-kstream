package guru.bonacci.heroes.accounttransfer;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNT_STORAGE_SINK_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_EVENTUAL_TOPIC;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_TUPLES_TOPIC;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
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
public class BootstrAppAccountTransfer {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppAccountTransfer.class, args);
	}

	
  @Bean
  public KStream<String, Transfer> topology(StreamsBuilder builder) {
    final var accountSerde = new JsonSerde<Account>(Account.class);
    final var transferSerde = new JsonSerde<Transfer>(Transfer.class);
    
    KStream<String, Transfer> transferStream = // key: poolId.from or poolId.to
      builder
       .stream(TRANSFER_TUPLES_TOPIC, Consumed.with(Serdes.String(), transferSerde))
       .peek((k,v) -> log.info("in transfer {}<>{}", k, v));

    KTable<String, Account> accountTable = // key: poolId.accountId
      builder
       .table(ACCOUNT_TRANSFERS_TOPIC, Consumed.with(Serdes.String(), accountSerde));

    KStream<String, Account> accountStream = 
      accountTable
        .toStream()
        .peek((k,v) -> log.info("out account {}<>{}", k, v));
     
    // stream the transfer to the eventual consistency mechanism..
      accountStream
        .peek((k,v) -> log.info("hastransfer {} filter {}<>{}", v.hasTransfer(), k, v))
        .filter((poolAccountId, account) -> account.hasTransfer())
        .map((poolAccountId, account) -> { // rekey to transferId
        
          var latestTransfer = account.latestTransfer();
          return KeyValue.pair(latestTransfer.getTransferId(), latestTransfer);
        })
        .peek((k,v) -> log.info("out eventual {}<>{}", k, v))
        .to(TRANSFER_EVENTUAL_TOPIC, Produced.with(Serdes.String(), transferSerde));

    //.. and also to account-storage   
    accountStream
      .to(ACCOUNT_STORAGE_SINK_TOPIC, Produced.with(Serdes.String(), accountSerde));
    
    transferStream // raison d'être: add transfer to account
      .join(accountTable, (transfer, account) -> account.addTransfer(transfer))
      .peek((k,v) -> log.info("out transfer account {}<>{}", k, v))
      .to(ACCOUNT_TRANSFERS_TOPIC, Produced.with(Serdes.String(), accountSerde));
   
    return transferStream;
  }
}
