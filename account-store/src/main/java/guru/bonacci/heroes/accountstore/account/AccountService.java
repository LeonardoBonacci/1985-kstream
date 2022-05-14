package guru.bonacci.heroes.accountstore.account;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.kafka.streams.state.HostInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import guru.bonacci.heroes.accountstore.Bookkeeper;
import guru.bonacci.heroes.accountstore.KafkaStreamsService;
import guru.bonacci.heroes.accountstore.rpc.HostStoreInfo;
import guru.bonacci.heroes.accountstore.rpc.MetadataController;
import guru.bonacci.heroes.accountstore.validation.AccountToValidate;
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

  private final Validator validator;

  
  private void validateInput(String poolId, String accountId) {
    var violations = validator.validate(new AccountToValidate(poolId, accountId));

    if (!violations.isEmpty()) {
      var sb = new StringBuilder();
      for (ConstraintViolation<AccountToValidate> constraintViolation : violations) {
          sb.append(constraintViolation.getMessage());
      }
      throw new InvalidPoolAccountException(sb.toString());
    }
  }

  
  public Optional<Account> getAccount(String poolId, String accountId) {
    validateInput(poolId, accountId);
    
    var poolAccountId = Account.identifier(poolId, accountId);
  
    final var hostStoreInfo = metadata.streamsMetadataForKey(poolAccountId);
  
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

