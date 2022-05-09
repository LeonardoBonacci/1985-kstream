package guru.bonacci.heroes.accountcache;

import java.math.BigDecimal;

import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class Bookkeeper {

  private static final QueryableStoreType<ReadOnlyKeyValueStore<String, Account>> ACCOUNT_STORE_TYPE = QueryableStoreTypes.keyValueStore();

  private final StreamsBuilderFactoryBean streamsBuilder;
  
  
  private ReadOnlyKeyValueStore<String, Account> accountStore() {
    final var streams = streamsBuilder.getKafkaStreams();
    return streams.store(StoreQueryParameters.fromNameAndType("AccountStore", ACCOUNT_STORE_TYPE).enableStaleStores());
  }
  
  private BigDecimal getBalance(Account account) {
    return account.getTransfers().stream()
                    .map(tf -> tf.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Scheduled(fixedRate = 10000)
  public void account() {
    try {
      BigDecimal totalBalance = BigDecimal.ZERO;
      
      var it = accountStore().all();
      while(it.hasNext()) {
        var kv = it.next();
        kv.value.getTransfers().forEach(t -> log.debug("{}", t.getAmount()));
        var balance = getBalance(kv.value);
        log.info("balance for {} is {}", kv.value.getAccountId(), balance);
        totalBalance = totalBalance.add(balance);
      };
      log.warn("TOTAL BALANCE IS {}", totalBalance);
       } catch(RuntimeException e) {
      e.printStackTrace();
    }
  }    
}
