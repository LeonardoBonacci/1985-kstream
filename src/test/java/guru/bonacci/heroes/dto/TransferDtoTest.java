package guru.bonacci.heroes.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import guru.bonacci.heroes.dto.validate.AdvancedCheck;
import guru.bonacci.heroes.dto.validate.BasicCheck;
import guru.bonacci.heroes.dto.validate.IntermediateCheck;
import guru.bonacci.heroes.service.AccService;
import guru.bonacci.heroes.service.PoolService;
 
@SpringBootTest
public class TransferDtoTest {
 
  @Autowired Validator validator;

  @MockBean PoolService poolService;

  @MockBean AccService accService;


  @Test
  void passesBasic() {
    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(10.22));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, BasicCheck.class);
    assertThat(violations).isEmpty();
  }

  @Test
  void amountFormatInvalid() {
    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(10.220022));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, BasicCheck.class);
    assertThat(violations).isNotEmpty();
  }
  
  @Test
  void passesIntermediate() {
    when(poolService.exists(any())).thenReturn(true);

    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(10.22));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, IntermediateCheck.class);
    assertThat(violations).isEmpty();
  }

  @Test
  void intermediateNotInPool() {
    when(poolService.exists(any())).thenReturn(false);

    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(10.22));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, IntermediateCheck.class);
    assertThat(violations).isNotEmpty();
  }
  
  @Test
  void passesAdvanced() {
    when(poolService.containsAccount(any(), any())).thenReturn(true);
    when(accService.showMeTheBalance(any(), any())).thenReturn(Optional.of(BigDecimal.TEN));

    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(9.99));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, AdvancedCheck.class);
    assertThat(violations).isEmpty();
  }

  @Test
  void advancedInsufficientFunds() {
    when(poolService.containsAccount(any(), any())).thenReturn(true);
    when(accService.showMeTheBalance(any(), any())).thenReturn(Optional.of(BigDecimal.TEN));

    var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(10.01));
    
    Set<ConstraintViolation<TransferDto>> violations = validator.validate(dto, AdvancedCheck.class);
    assertThat(violations).isNotEmpty();
  }
}