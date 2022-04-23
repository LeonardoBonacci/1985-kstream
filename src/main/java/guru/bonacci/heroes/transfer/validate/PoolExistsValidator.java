package guru.bonacci.heroes.transfer.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import guru.bonacci.heroes.pool.PoolService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PoolExistsValidator implements ConstraintValidator<PoolExistsConstraint, String> {

  private final PoolService poolService;
  
  
  @Override
  public void initialize(PoolExistsConstraint pool) {
  }

  @Override
  public boolean isValid(String poolId, ConstraintValidatorContext cxt) {
      return poolService.contains(poolId);
  }
}