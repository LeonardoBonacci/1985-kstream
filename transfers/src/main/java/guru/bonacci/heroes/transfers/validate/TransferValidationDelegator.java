package guru.bonacci.heroes.transfers.validate;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

import guru.bonacci.heroes.domain.TransferDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransferValidationDelegator implements ConstraintValidator<TransferConstraint, Object> {

  private final AccountServiceClient client;
  
  private String poolField;
  private String fromField;
  private String toField;
  private String amountField;
  
  public void initialize(TransferConstraint constraintAnnotation) {
      this.poolField = constraintAnnotation.pool();
      this.fromField = constraintAnnotation.from();
      this.toField = constraintAnnotation.to();
      this.amountField = constraintAnnotation.amount();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
      var poolId = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(poolField));
      var from = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(fromField));
      var to = String.valueOf(new BeanWrapperImpl(value).getPropertyValue(toField));
      var amount = new BigDecimal(String.valueOf(new BeanWrapperImpl(value).getPropertyValue(amountField)));

      var info = client.validateTransfer(new TransferDto(poolId, from, to, amount));
      return info.isPoolIsValid() && info.isFromIsValid() && info.isToIsValid() && info.isHasSufficientFunds();
  }
}