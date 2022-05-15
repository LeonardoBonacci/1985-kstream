package guru.bonacci.heroes.accountstore.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import guru.bonacci.heroes.accountstore.account.AccountValidator;

@Constraint(validatedBy = AccountValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountConstraint {

  String message() default "Account details validation failed";
  Class<? extends Payload>[] payload() default {};
  Class<?>[] groups() default {};

  String pool();
  String account();
}