package guru.bonacci.heroes.account;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import guru.bonacci.heroes.account.kafka.KafkaAccountsConfig;
import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.kafka.serialization.JacksonSerde;

@SpringBootApplication
public class BootstrAppAccounts {

  public static final String ACCOUNTS_STORE_NAME = "AccountKeyValueStore";

  
	public static void main(String[] args) {
		SpringApplication.run(BootstrAppAccounts.class, args);
	}

	
  @Bean
  public KStream<String, Transfer> addTransfer(StreamsBuilder builder) {
    KStream<String, Transfer> transfers = builder.stream(KafkaAccountsConfig.TRANSFER_TUPLES_TOPIC);

    var materializedAccounts = Materialized.<String, Account, KeyValueStore<Bytes, byte[]>>as(ACCOUNTS_STORE_NAME)
          .withKeySerde(Serdes.String())
          .withValueSerde(JacksonSerde.of(Account.class));

    KTable<String, Account> accounts = builder.table(KafkaAccountsConfig.ACCOUNTS_TOPIC, materializedAccounts);
//    accounts.toStream().to("processed-transfers"); //rekey?
        
//    transfers.join(accounts, (transfer, account) -> account.addTransfer(transfer)).to(KafkaAccountsConfig.ACCOUNTS_TOPIC);
    return transfers;
  }
}
