package guru.bonacci.heroes.accountstorage;

import java.math.BigDecimal;
import java.util.stream.Stream;

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
  
  private BigDecimal getBalance(Account acc) {
    return addUpTransfers(acc).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private Stream<BigDecimal> addUpTransfers(final Account account) {
    return account.getTransfers().stream()
            .map(tf -> tf.getTo().equals(account.getAccountId()) ? tf.getAmount() : tf.getAmount().multiply(BigDecimal.valueOf(-1)));
  }
  
//  @Scheduled(fixedRate = 5000)
  public void account() {
    log.info(">>>> account..");
    BigDecimal totalBalance = BigDecimal.ZERO;
    
    try {
      accountStore().all().forEachRemaining(kv -> {
        var balance = getBalance(kv.value);
        log.info("balance for {} is {}", kv.value.getAccountId(), balance);
        totalBalance.add(balance);
      });
    } catch(RuntimeException e) {
      e.printStackTrace();
    }
    log.warn("TOTAL BALANCE IS {}", totalBalance);
  }    

}
