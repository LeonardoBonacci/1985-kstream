package guru.bonacci.heroes.accounttransfer;

import static guru.bonacci.heroes.kafka.KafkaTopicNames.*;

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
import org.springframework.kafka.support.serializer.JsonSerde;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;

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
        builder.stream(TRANSFER_TUPLES_TOPIC, Consumed.with(Serdes.String(), transferSerde));

    KTable<String, Account> accountTable = // key: poolId.accountId
        builder.table(ACCOUNT_TRANSFERS_TOPIC, Consumed.with(Serdes.String(), accountSerde));
    KStream<String, Account> accountStream = accountTable.toStream();
     
    // first, stream to the eventual consistency mechanism, and then to account-storage
    // order is important to guarantee that transfers 'have been processed' before they can be 'queried'
//    accountStream 
//      .map((key, account) -> KeyValue.pair(account.latestTransfer().getTransferId(), account.latestTransfer()));
//      .to(TRANSFERS_EVENTUAL_TOPIC);
    accountStream.to(ACCOUNT_STORAGE_SINK_TOPIC, Produced.with(Serdes.String(), accountSerde));
    
    transferStream // raison d'être: add transfer to account
      .join(accountTable, (transfer, account) -> account.addTransfer(transfer))
      .to(ACCOUNT_TRANSFERS_TOPIC, Produced.with(Serdes.String(), accountSerde));
    return transferStream;
  }
}
