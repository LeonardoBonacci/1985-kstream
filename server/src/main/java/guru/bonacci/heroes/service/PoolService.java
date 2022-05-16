package guru.bonacci.heroes.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import guru.bonacci.heroes.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PoolService {

  private final AccountRepository accountRepo;
  
  // for now 
  // key is poolId
  // value is list of accountId -> represents members in a pool
  private Map<String, List<String>> pools = new HashMap<>(); 

  
  @PostConstruct
  void init() {
    pools.put("heroes", accountRepo.getAccounts());
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
