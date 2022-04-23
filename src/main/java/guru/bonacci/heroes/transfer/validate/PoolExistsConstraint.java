package guru.bonacci.heroes.transfer.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = PoolExistsValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PoolExistsConstraint {
 
    String message() default "Pool does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}