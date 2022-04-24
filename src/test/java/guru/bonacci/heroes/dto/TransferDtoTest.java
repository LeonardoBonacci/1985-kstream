package guru.bonacci.heroes.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
 
public class TransferDtoTest {
 
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void creditCardNumberMustNotBeNull() {
    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(10.220022));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto);
    assertThat(violations).isNotEmpty();
  }
}