package guru.bonacci.heroes.account.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import guru.bonacci.heroes.account.AccountBootstrApp;
import guru.bonacci.heroes.account.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

  private static final QueryableStoreType<ReadOnlyKeyValueStore<String, Account>> ACCOUNT_STORE_TYPE = QueryableStoreTypes.keyValueStore();

  private final StreamsBuilderFactoryBean streamsBuilder;

  
  private ReadOnlyKeyValueStore<String, Account> accountStore() {
    final var streams = streamsBuilder.getKafkaStreams();
    return streams.store(StoreQueryParameters.fromNameAndType(AccountBootstrApp.ACCOUNTS_STORE_NAME, ACCOUNT_STORE_TYPE).enableStaleStores());
  }
  
  public Optional<Account> getAccount(String accountId, String poolId) {
    var accountStoreKey = Account.identifier(poolId, accountId);
    log.info("retrieving Account with key {}", accountStoreKey);
    var account = accountStore().get(accountStoreKey);
    log.info("retrieved {}", account);
    return Optional.ofNullable(account);
  }
  
  public Optional<BigDecimal> getBalance(String accountId, String poolId) {
    var accOpt = getAccount(accountId, poolId);
    return accOpt.map(acc -> addUpTransfers(acc).reduce(BigDecimal.ZERO, BigDecimal::add));
  }
  
  private static Stream<BigDecimal> addUpTransfers(final Account account) {
    return account.getTransfers().stream()
            .map(tf -> tf.getTo().equals(account.getAccountId()) ? tf.getAmount() : tf.getAmount().multiply(BigDecimal.valueOf(-1)));
  }
}
