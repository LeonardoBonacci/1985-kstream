package guru.bonacci.heroes.transferingress.pool.sardex;

import static guru.bonacci.heroes.transferingress.account.AccountUtils.*;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.TransferValidationResponse;
import guru.bonacci.heroes.transferingress.validate.PoolTypeBasedValidator;
import guru.bonacci.heroes.transferingress.validate.TransferValidationResult;

@Component("sardex")
public class SardexPoolTypeValidator implements PoolTypeBasedValidator {

  private static final BigDecimal MIN_BALANCE = BigDecimal.valueOf(-1000);

  
  @Override 
  public TransferValidationResult validate(TransferValidationResponse info, BigDecimal amount) {
    if (!info.getPoolIsValid() && !info.getFromIsValid() || !info.getToIsValid() || info.getFromAccount() == null) {
      return new TransferValidationResult(false, info.getErrorMessage());
    }

    if (hasSufficientFunds(info.getFromAccount())) {
      return new TransferValidationResult(true, null);
    }

    return new TransferValidationResult(false, "insufficient balance");
  }    
      
  @Override 
  public boolean hasSufficientFunds(Account account) {
    return getBalance(account).compareTo(MIN_BALANCE) > -1;
  }
}    

