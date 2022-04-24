package guru.bonacci.heroes.dto.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.service.PoolService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountExistsValidator implements ConstraintValidator<AccountExistsConstraint, Object> {

  private final PoolService poolService;

  private String poolField;
  private String accountField;

  
  public void initialize(AccountExistsConstraint constraintAnnotation) {
    this.poolField = constraintAnnotation.pool();
    this.accountField = constraintAnnotation.account();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    var poolId = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(poolField));
    var accId = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(accountField));

    return poolService.containsAccount(poolId, accId);
  }
}