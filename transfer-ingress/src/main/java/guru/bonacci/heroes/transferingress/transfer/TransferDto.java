package guru.bonacci.heroes.transferingress.transfer;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.validation.GroupSequence;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import guru.bonacci.heroes.transferingress.validate.FinalCheck;
import guru.bonacci.heroes.transferingress.validate.InitialCheck;
import guru.bonacci.heroes.transferingress.validate.TransferConstraint;
import lombok.Value;

@Value
@TransferConstraint(
    groups = FinalCheck.class,
    pool = "poolId", 
    from = "from", 
    to = "to", 
    amount = "amount"
  )
@GroupSequence({InitialCheck.class, FinalCheck.class, TransferDto.class})
public class TransferDto {

  @NotBlank(groups = InitialCheck.class, message = "poolId is mandatory")
  private String poolId;

  @NotBlank(groups = InitialCheck.class, message = "from is mandatory")
  private String from;

  @NotBlank(groups = InitialCheck.class, message = "to is mandatory")
  private String to;

  @Nonnull
  @DecimalMin(groups = InitialCheck.class, value = "0.0", inclusive = false)
  @Digits(groups = InitialCheck.class, integer = 4, fraction = 2)
  private BigDecimal amount;
}