package guru.bonacci.heroes.transferingress.validation;

import static guru.bonacci.heroes.domain.Account.identifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import guru.bonacci.heroes.transferingress.tip.TIPRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LockValidator implements ConstraintValidator<LockConstraint, Object>  {

  private final TIPRepository cache;
  
  private String poolField;
  private String fromField;
  private String toField;
  
  public void initialize(LockConstraint constraintAnnotation) {
      this.poolField = constraintAnnotation.pool();
      this.fromField = constraintAnnotation.from();
      this.toField = constraintAnnotation.to();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    var poolId = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(poolField));
    var from = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(fromField));
    var to = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(toField));

    boolean blocked = 
        isBlocked(identifier(poolId, from)) || 
        isBlocked(identifier(poolId, to));
      
    if (blocked) {
      context.unwrap(HibernateConstraintValidatorContext.class)
             .addExpressionVariable("errorMessage", "Concurrent transfer attempt, try again in 10 seconds");
      return false;
    }
    
    if (proceed(poolId, from)) {
      context.unwrap(HibernateConstraintValidatorContext.class)
             .addExpressionVariable("errorMessage", from + " has transfer in progress");
      return false;
    }

    if (proceed(poolId, to)) {
      context.unwrap(HibernateConstraintValidatorContext.class)
             .addExpressionVariable("errorMessage", to + " has transfer in progress");
      return false;
    }

    return true;
  }  

  private boolean isBlocked(String identifier) {
    if (!cache.lock(identifier)) {
      log.warn("attempt to override lock {}", identifier);
      return true;
    }
    return false;
  }

  private boolean proceed(String poolId, String accountId) {
    return !cache.existsById(identifier(poolId, accountId));
  }
}