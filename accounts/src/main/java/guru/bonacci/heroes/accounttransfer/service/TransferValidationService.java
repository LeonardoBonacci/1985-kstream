package guru.bonacci.heroes.accounttransfer.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import guru.bonacci.heroes.domain.TransferValidationRequest;
import guru.bonacci.heroes.domain.TransferValidationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferValidationService {

  private final PoolService poolService;
  private final AccountService accountService;
  

  public TransferValidationResponse getTransferValidationInfo(TransferValidationRequest transfer) {
    var poolExists = poolService.exists(transfer.getPoolId());
    var fromExists = poolService.containsAccount(transfer.getPoolId(), transfer.getFrom());
    var toExists = poolService.containsAccount(transfer.getPoolId(), transfer.getTo());

    var balance = BigDecimal.ZERO;
    if (poolExists && fromExists && toExists) {
      var balanceOpt = accountService.getBalance(transfer.getFrom(), transfer.getPoolId());
      if (balanceOpt.isEmpty()) {
        throw new IllegalStateException("where is the balance...");
      } 
      balance = balanceOpt.get();
    }

    var sufficientBalance = balance.compareTo(transfer.getAmount()) > 0;
    return new TransferValidationResponse(poolExists, fromExists, toExists, sufficientBalance);
  }
}
