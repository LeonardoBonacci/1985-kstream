package guru.bonacci.heroes.transferingress.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import guru.bonacci.heroes.transferingress.validate.InitialCheck;

 
@SpringBootTest
public class TransferDtoTest {
 
  @Autowired Validator validator;

  @Test
  void passesInitial() {
    var dto = new TransferDto("coro", "foo", "bar", BigDecimal.valueOf(10.22));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, InitialCheck.class);
    assertThat(violations).isEmpty();
  }

  @Test
  void amountFormatInvalid() {
    var dto = new TransferDto("coro", "foo", "bar", BigDecimal.valueOf(10.220022));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, InitialCheck.class);
    assertThat(violations).isNotEmpty();
  }
}