package guru.bonacci.heroes.account;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import guru.bonacci.kafka.serialization.JacksonSerde;

@SpringBootApplication
public class BootstrAppAccounts {

  public static final String ACCOUNTS_STORE_NAME = "AccountKeyValueStore";

  
	public static void main(String[] args) {
		SpringApplication.run(BootstrAppAccounts.class, args);
	}

	
  @Bean
  public KStream<String, Transfer> addTransfer(StreamsBuilder builder) {
    KStream<String, Transfer> transfers = builder.stream(KafkaTopicNames.TRANSFER_TUPLES_TOPIC);

    var materializedAccounts = Materialized.<String, Account, KeyValueStore<Bytes, byte[]>>as(ACCOUNTS_STORE_NAME)
          .withKeySerde(Serdes.String())
          .withValueSerde(JacksonSerde.of(Account.class));

    KTable<String, Account> accounts = builder.table(KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC, materializedAccounts);
    accounts
      .toStream()
      .map((key, account) -> KeyValue.pair(account.getLatestTransfer().getTransferId(), account.getLatestTransfer()))
      .to(KafkaTopicNames.TRANSFERS_EVENTUAL_TOPIC);
    
    transfers
      .join(accounts, (transfer, account) -> account.addTransfer(transfer))
      .to(KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC);
    return transfers;
  }
}
