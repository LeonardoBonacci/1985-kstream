package guru.bonacci.heroes.accountstore.service;

import org.springframework.stereotype.Service;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.TransferValidationRequest;
import guru.bonacci.heroes.domain.TransferValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferValidationService {

  private final PoolService poolService;
  

  public TransferValidationResponse getTransferValidationInfo(TransferValidationRequest request, Account account) {
      if (account == null) { // we executed a left join
        log.warn("pool.from {}.{} not found", request.getPoolId(), request.getFrom());
        return new TransferValidationResponse(false, false, false, null, "'pool.from' combination does not exist");
      }
      
      log.warn("pool.from {}.{} found", request.getPoolId(), request.getFrom());

      // the join proves pool and from
      var poolExists = true; 
      var fromExists = true; 
      var toExists = poolService.containsAccount(request.getPoolId(), request.getTo());

      return new TransferValidationResponse(poolExists, fromExists, toExists, account, null);
  }
}

