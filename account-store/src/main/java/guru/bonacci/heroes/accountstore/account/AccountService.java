package guru.bonacci.heroes.accountstore.account;

import static guru.bonacci.heroes.accountstore.BootstrAppAccountStore.STORE;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import guru.bonacci.heroes.accountstore.KafkaStreamsService;
import guru.bonacci.heroes.accountstore.rpc.HostStoreInfo;
import guru.bonacci.heroes.accountstore.rpc.MetadataService;
import guru.bonacci.heroes.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

  private final KafkaStreamsService streams;
  private final HostInfo hostInfo;

  private final MetadataService metadata;
  private final RestTemplate restTemplate;

  
  //https://github.com/quarkusio/quarkus-quickstarts/blob/main/kafka-streams-quickstart/aggregator/src/main/java/org/acme/kafka/streams/aggregator/streams/InteractiveQueries.java
  public Optional<Account> getAccount(final String poolId, final String accountId) {
    var poolAccountId = Account.identifier(poolId, accountId);
  
    var hostStoreInfoOpt = metadata.streamsMetadataForStoreAndKey(STORE, poolAccountId, new StringSerializer());
    return hostStoreInfoOpt
              .map(hostStoreInfo -> doGetAccount(poolId, accountId, hostStoreInfo))
              .orElse(Optional.empty());
  }

  private Optional<Account> doGetAccount(String poolId, String accountId, HostStoreInfo hostStoreInfo) {
    var poolAccountId = Account.identifier(poolId, accountId);
    
    log.info("there {}", hostStoreInfo);
    log.info("here {}", hostInfo);
    
    if (!thisHost(hostStoreInfo)){
       return fetchRemote(hostStoreInfo, poolId, accountId);
    }
  
    final var store = streams.waitForAccountStore();
    if (store == null) {
      throw new NoStoreException();
    }
  
    log.debug("Get account {} from the store", poolAccountId); 
    return Optional.ofNullable(store.get(poolAccountId));
  }

  public Optional<BigDecimal> getBalance(String poolId, String accountId) {
    var accountOpt = getAccount(poolId, accountId);
    return accountOpt.map(Account::getBalance);
  }


  private boolean thisHost(final HostStoreInfo host) {
    return host.getHost().equals(hostInfo.host()) &&
           host.getPort() == hostInfo.port();
  }
  
  private Optional<Account> fetchRemote(HostStoreInfo host, String poolId, String accountId) {
    var url = getOtherUri(host.getHost(), host.getPort(), poolId, accountId);
    log.info("attempting to reach {}", url);

    try {
      return Optional.ofNullable(restTemplate.getForObject(url, Account.class));
    } catch (HttpClientErrorException ex)   {
      log.warn("remote call '{}' returned not found", url);
      return Optional.empty();
    }
  }

  // TODO support https
  // https://github.com/quarkusio/quarkus-quickstarts/blob/main/kafka-streams-quickstart/aggregator/src/main/java/org/acme/kafka/streams/aggregator/rest/WeatherStationEndpoint.java
  private URI getOtherUri(String host, int port, String poolId, String accountId) {
    try {
      var path = String.format("pools/%s/accounts/%s", poolId, accountId);
      return new URI(String.format("http://%s:%d/%s", host, port, path));
    }
    catch (URISyntaxException e) {
        throw new RuntimeException(e);
    }
  }
}

