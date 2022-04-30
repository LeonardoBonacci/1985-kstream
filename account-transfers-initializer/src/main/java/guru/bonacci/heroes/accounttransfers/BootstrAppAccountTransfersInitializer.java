package guru.bonacci.heroes.accounttransfers;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.kafka.KafkaTopicNames;
import guru.bonacci.kafka.serialization.JacksonSerde;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BootstrAppAccountTransfersInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppAccountTransfersInitializer.class, args);
	}

	@Bean
	public KStream<String, Account> topology(StreamsBuilder builder) {
	  KStream<String, Account> accountStream = 
	      builder.stream(KafkaTopicNames.ACCOUNTS_TOPIC, Consumed.with(Serdes.String(), JacksonSerde.of(Account.class)));

	  final String storeName = "accountStore";
	  StoreBuilder<KeyValueStore<String, Account>> storeBuilder = Stores.keyValueStoreBuilder(
        Stores.persistentKeyValueStore(storeName),
        Serdes.String(),
        JacksonSerde.of(Account.class));

	  builder.addStateStore(storeBuilder);

	  KStream<String, Account> accountTransferStream = 
	      builder.stream(KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC, Consumed.with(Serdes.String(), JacksonSerde.of(Account.class)));
	  accountTransferStream.process(() -> new AccountStoreFillingProcessor(storeName), storeName); //FIXME this solution takes way too long and does not work during rebalance
	  
    accountStream
      .transformValues(() -> new AccountFilteringTransformer(storeName), storeName)
      .selectKey((none, account) -> account.identifier())
      .to(KafkaTopicNames.ACCOUNT_TRANSFERS_TOPIC, Produced.with(Serdes.String(), JacksonSerde.of(Account.class)));
  	return accountStream;
	}
	
	
	static class AccountStoreFillingProcessor implements org.apache.kafka.streams.processor.api.Processor<String, Account, Void, Void> {
    
	  private String storeName;
	  private KeyValueStore<String, Account> store;

	  public AccountStoreFillingProcessor(String storeName) {
      this.storeName = storeName;
	  }
	  
    @Override
    public void init(final org.apache.kafka.streams.processor.api.ProcessorContext<Void, Void> context) {
      store = context.getStateStore(storeName);
    }

    @Override
    public void process(final Record<String, Account> record) {
      var account = store.get(record.key());
      if (account == null) {
        store.put(record.value().identifier(), record.value());
      } 
    }

    @Override
    public void close() {
        // close any resources managed by this processor
        // Note: Do not close any StateStores as these are managed by the library
    }
	}
	
	
	static class AccountFilteringTransformer implements ValueTransformerWithKey<String, Account, Account> {

     private String storeName;
     private KeyValueStore<String, Account> store;

     public AccountFilteringTransformer(String storeName) {
         this.storeName = storeName;
     }

     @Override
     public void init(final org.apache.kafka.streams.processor.ProcessorContext context) {
          store = (KeyValueStore) context.getStateStore(storeName);
     }

     @Override
     public Account transform(final String unused, final Account account) {
         var persistedValue = store.get(account.identifier());

         if (persistedValue == null) {
           store.put(account.identifier(), account);
         }

         return persistedValue;
     }

     @Override
     public void close() {
     }
 }
	
	
}
