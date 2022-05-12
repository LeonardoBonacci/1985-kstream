package guru.bonacci.heroes.accountcache;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import guru.bonacci.heroes.domain.Account;
import lombok.RequiredArgsConstructor;

/** hack to avoid nullpointers due to late instantiation of KafkaStreams object */
@Service
@RequiredArgsConstructor
public class KafkaStreamsService {

  private static final QueryableStoreType<ReadOnlyKeyValueStore<String, Account>> ACCOUNT_STORE_TYPE = QueryableStoreTypes.keyValueStore();

  private final StreamsBuilderFactoryBean streamsBuilder;

  
  // As the docs warn, may be null when not yet started
  public KafkaStreams getKafkaStreams() {
    return streamsBuilder.getKafkaStreams();
  }

  public ReadOnlyKeyValueStore<String, Account> accountStore() {
    return getKafkaStreams()
         .store(StoreQueryParameters.fromNameAndType("AccountStore", ACCOUNT_STORE_TYPE).enableStaleStores());
  }

}
