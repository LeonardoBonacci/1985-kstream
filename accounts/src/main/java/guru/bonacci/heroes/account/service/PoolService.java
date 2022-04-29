package guru.bonacci.heroes.account.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.bonacci.heroes.account.kafka.KafkaAccountsConfig;
import guru.bonacci.heroes.domain.Account;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PoolService {

  // for now 
  // key is poolId
  // value is list of accountId -> represents members in a pool
  private Map<String, List<String>> pools = new HashMap<>(); 

  
  @KafkaListener(topics = KafkaAccountsConfig.ACCOUNTS_TOPIC, groupId = "#{T(java.util.UUID).randomUUID().toString()}")
  public void listen(@Payload String data) throws IOException {
    //TODO use JsonDeserializer
    var account = new ObjectMapper().readValue(data, Account.class);
    log.info("Loading into pool: {}", account);
    addAccountToPool(account);
  }
  
  public void addAccountToPool(Account acc) {
    pools.putIfAbsent(acc.getPoolId(), new ArrayList<String>());
    pools.get(acc.getPoolId()).add(acc.getAccountId());
  }
  

  public List<String> searchMembers(String poolId, final String searchTerm) {
    if (!exists(poolId)) {
      throw new NonExistingPoolException("wrong guess..");
    }
  
    return pools.get(poolId).stream()
        .filter(accId -> searchPredicate(accId, searchTerm))
        .collect(Collectors.toList());
  }

  private boolean searchPredicate(String accId, String searchTerm) {
    return searchTerm.isBlank() ? true : accId.startsWith(searchTerm);
  }

  public boolean exists(String poolId) {
    return pools.containsKey(poolId);
  }

  public boolean containsAccount(String poolId, String accountId) {
    return exists(poolId) && pools.get(poolId).contains(accountId);
    
  }

  public boolean notContainsAccount(String poolId, String accountId) {
    return !containsAccount(poolId, accountId);
  }


  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public static class NonExistingPoolException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NonExistingPoolException() {
        super();
    }
    
    public NonExistingPoolException(String message) {
        super(message);
    }
  }
}
