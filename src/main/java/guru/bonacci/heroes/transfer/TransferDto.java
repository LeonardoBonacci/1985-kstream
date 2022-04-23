package guru.bonacci.heroes.transfer;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.validation.GroupSequence;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import guru.bonacci.heroes.transfer.validate.AdvancedCheck;
import guru.bonacci.heroes.transfer.validate.BasicCheck;
import guru.bonacci.heroes.transfer.validate.IntermediateCheck;
import guru.bonacci.heroes.transfer.validate.PoolExistsConstraint;
import guru.bonacci.heroes.transfer.validate.SufficientFundsConstraint;
import lombok.Value;

@Value
@SufficientFundsConstraint(
    groups = AdvancedCheck.class,
    pool = "poolId", 
    account = "from", 
    amount = "amount"
  )
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
