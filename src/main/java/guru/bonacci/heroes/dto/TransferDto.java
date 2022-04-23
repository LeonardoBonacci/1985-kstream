package guru.bonacci.heroes.dto;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.validation.GroupSequence;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import guru.bonacci.heroes.dto.validate.AccountExistsConstraint;
import guru.bonacci.heroes.dto.validate.AdvancedCheck;
import guru.bonacci.heroes.dto.validate.BasicCheck;
import guru.bonacci.heroes.dto.validate.IntermediateCheck;
import guru.bonacci.heroes.dto.validate.PoolExistsConstraint;
import guru.bonacci.heroes.dto.validate.SufficientFundsConstraint;
import lombok.Value;

@Value
@SufficientFundsConstraint(
    groups = AdvancedCheck.class,
    pool = "poolId", 
    account = "from", 
    amount = "amount"
  )
@AccountExistsConstraint.List({ 
  @AccountExistsConstraint(
    groups = AdvancedCheck.class,
    pool = "poolId", 
    account = "from", 
    message = "'from' not in pool"
  ), 
  @AccountExistsConstraint(
    groups = AdvancedCheck.class,
    pool = "poolId", 
    account = "to", 
    message = "'to' not in pool"
  )
})
@GroupSequence({BasicCheck.class, IntermediateCheck.class, AdvancedCheck.class, TransferDto.class})
public class TransferDto {

  @NotBlank(groups = BasicCheck.class, message = "poolId is mandatory")
  @PoolExistsConstraint(groups = IntermediateCheck.class)
  private String poolId;

  @NotBlank(groups = BasicCheck.class, message = "from is mandatory")
  private String from;

  @NotBlank(groups = BasicCheck.class, message = "to is mandatory")
  private String to;

  @Nonnull
  @DecimalMin(groups = BasicCheck.class, value = "0.0", inclusive = false)
  @Digits(groups = BasicCheck.class, integer = 4, fraction = 2)
  private BigDecimal amount;
}