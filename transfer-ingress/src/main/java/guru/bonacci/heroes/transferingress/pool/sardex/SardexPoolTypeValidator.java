package guru.bonacci.heroes.transferingress.pool.sardex;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.domain.TransferValidationResponse;
import guru.bonacci.heroes.transferingress.account.AccountUtils;
import guru.bonacci.heroes.transferingress.pool.PoolTypeBasedValidator;
import guru.bonacci.heroes.transferingress.pool.TransferValidationResult;

@Component("sardex")
public class SardexPoolTypeValidator implements PoolTypeBasedValidator {

  
  @Override 
  public TransferValidationResult validate(TransferValidationResponse info, BigDecimal amount) {
    if (info.getPoolIsValid() || info.getFromIsValid() || info.getToIsValid() || info.getFromAccount() == null) {
      return new TransferValidationResult(false, info.getErrorMessage());
    }

    //TODO ERROR HANDLING no aacount
    if (hasSufficientFunds(info.getFromAccount())) {
      return new TransferValidationResult(true, null);
    }

    return new TransferValidationResult(false, "insufficient balance");
  }    
      
  @Override 
  public boolean hasSufficientFunds(Account account) {
    var minBalance = BigDecimal.valueOf(-1000);
    return AccountUtils.getBalance(account).compareTo(minBalance) > -1;
  }
}    

