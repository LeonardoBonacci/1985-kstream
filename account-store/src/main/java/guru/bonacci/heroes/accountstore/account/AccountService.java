package guru.bonacci.heroes.accountstore.account;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import guru.bonacci.heroes.accountstore.Bookkeeper;
import guru.bonacci.heroes.accountstore.KafkaStreamsService;
import guru.bonacci.heroes.accountstore.rpc.HostStoreInfo;
import guru.bonacci.heroes.accountstore.rpc.MetadataController;
import guru.bonacci.heroes.accountstore.validation.PoolService;
import guru.bonacci.heroes.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

  private final KafkaStreamsService streams;
  private final HostInfo hostInfo;

  private final Bookkeeper bookkeeper;
  private final MetadataController metadata;
  private final RestTemplate restTemplate;

  private final PoolService poolService;

  
  private void validateInput(String poolId, String accountId) {
    if (!poolService.exists(poolId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "pool " + poolId + " does not exist");      
    }
  
    if (!poolService.containsAccount(poolId, accountId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account " + accountId + " not in pool " + poolId);      
    }
  }

  
  public Optional<Account> getAccount(String poolId, String accountId) {
    validateInput(poolId, accountId);
    
    var poolAccountId = Account.identifier(poolId, accountId);
  
    final HostStoreInfo hostStoreInfo = metadata.streamsMetadataForKey(poolAccountId);
  
    log.info("there {}", hostStoreInfo);
    log.info("here {}", hostInfo);
    
    if (!thisHost(hostStoreInfo)){
       return fetchRemote(hostStoreInfo, poolId, accountId);
    }
  
    final ReadOnlyKeyValueStore<String, Account> store = streams.waitForAccountStore();
    if (store == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  
    log.debug("Get account {} from the store", poolAccountId); 
    return Optional.ofNullable(store.get(poolAccountId));
  }
  
  public Optional<BigDecimal> getBalance(String poolId, String accountId) {
    var accOpt = getAccount(poolId, accountId);
    return accOpt.map(bookkeeper::determineBalance);
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

