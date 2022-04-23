package guru.bonacci.heroes.transfer.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = SufficientFundsValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SufficientFundsConstraint {

  String message() default "Insufficient funds";
  Class<? extends Payload>[] payload() default {};
  Class<?>[] groups() default {};

  String pool();
  String account();
  String amount();
}