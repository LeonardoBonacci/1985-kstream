package guru.bonacci.heroes.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import guru.bonacci.heroes.transfers.TransferDto;
import guru.bonacci.heroes.transfers.validate.InitialCheck;
import guru.bonacci.heroes.transfers.validate.FinalCheck;
 
@SpringBootTest
public class TransferDtoTest {
 
  @Autowired Validator validator;

  @Test
  void passesBasic() {
    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(10.22));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, InitialCheck.class);
    assertThat(violations).isEmpty();
  }

  @Test
  void amountFormatInvalid() {
    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(10.220022));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, InitialCheck.class);
    assertThat(violations).isNotEmpty();
  }
  
  @Test
  void passesAdvanced() {
    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(9.99));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, FinalCheck.class);
    assertThat(violations).isEmpty();
  }

  @Test
  void advancedInsufficientFunds() {
    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(10.01));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, FinalCheck.class);
    assertThat(violations).isNotEmpty();
  }
}