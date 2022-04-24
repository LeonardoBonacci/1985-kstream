package guru.bonacci.heroes.dto.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = AccountExistsValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountExistsConstraint {
 
  String message() default "Invalid account used";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

  String pool();
  String account();

  @Target({ ElementType.TYPE })
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {
    AccountExistsConstraint[] value();
  }
}