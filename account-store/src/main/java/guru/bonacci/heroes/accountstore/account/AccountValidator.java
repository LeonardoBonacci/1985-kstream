package guru.bonacci.heroes.accountstore.account;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.accountstore.validation.AccountConstraint;
import guru.bonacci.heroes.accountstore.validation.PoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountValidator implements ConstraintValidator<AccountConstraint, Object> {

  private final PoolService poolService;
  
  
  private String poolField;
  private String accountField;
  
  public void initialize(AccountConstraint constraintAnnotation) {
      this.poolField = constraintAnnotation.pool();
      this.accountField = constraintAnnotation.account();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    var poolId = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(poolField));
    var accountId = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(accountField));

    log.info("validating {}.{}", poolId, accountId);
    return poolService.exists(poolId) && poolService.containsAccount(poolId, accountId);
  }
}