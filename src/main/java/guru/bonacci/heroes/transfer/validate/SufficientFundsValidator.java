package guru.bonacci.heroes.transfer.validate;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.account.AccService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SufficientFundsValidator implements ConstraintValidator<SufficientFundsConstraint, Object> {

  private final AccService accService;

  private String poolField;
  private String accountField;
  private String amountField;
  
  public void initialize(SufficientFundsConstraint constraintAnnotation) {
      this.poolField = constraintAnnotation.pool();
      this.accountField = constraintAnnotation.account();
      this.amountField = constraintAnnotation.amount();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
      var poolId = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(poolField));
      var accountId = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(accountField));
      var amount = new BigDecimal(String.valueOf(new BeanWrapperImpl(value).getPropertyValue(amountField)));

      var balance = accService.getBalance(accountId, poolId);
      return balance.get().compareTo(amount) > 0;
    }
}