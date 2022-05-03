package guru.bonacci.heroes.transfers.pool;

import java.math.BigDecimal;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.TransferValidationResponse;

public interface PoolTypeBasedValidator {

  TransferValidationResult validate(TransferValidationResponse info, BigDecimal amount);
  
  boolean hasSufficientFunds(Account account);
}
