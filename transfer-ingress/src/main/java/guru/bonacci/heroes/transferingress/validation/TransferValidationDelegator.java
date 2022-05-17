package guru.bonacci.heroes.transferingress.validation;

import static guru.bonacci.heroes.domain.Account.identifier;
import static guru.bonacci.heroes.kafka.KafkaTopicNames.TRANSFER_VALIDATION_REQUEST_TOPIC;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import guru.bonacci.heroes.domain.TransferValidationRequest;
import guru.bonacci.heroes.domain.TransferValidationResponse;
import guru.bonacci.heroes.transferingress.pool.PoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TransferValidationDelegator implements ConstraintValidator<TransferConstraint, Object>  {

  private final PoolRepository poolRepo;
  private final ApplicationContext appContext;
  private final ReplyingKafkaTemplate<String, TransferValidationRequest, TransferValidationResponse> kafkaTemplate;
  
  
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
    
    var validationRequest = new TransferValidationRequest(poolId, from, to);
    
    var record = new ProducerRecord<>(TRANSFER_VALIDATION_REQUEST_TOPIC, identifier(poolId, from), validationRequest);
    var replyFuture = kafkaTemplate.sendAndReceive(record);

    try {
      var sendResult = replyFuture.getSendFuture().get(3, TimeUnit.SECONDS);
      log.info("Sent ok: {}", sendResult.getRecordMetadata());
  
      var consumerRecord = replyFuture.get(3, TimeUnit.SECONDS);
      log.info("Return value: {} on partition {}", consumerRecord.value(), consumerRecord.partition());

      if (poolRepo.getType(poolId) == null) {
        context.unwrap(HibernateConstraintValidatorContext.class)
            .addExpressionVariable("errorMessage", "pool " + poolId + " does not exist");
        return false;
      }

      var poolType = poolRepo.getType(poolId).getName();
      var validator = appContext.getBean(poolType, PoolTypeBasedValidator.class);

      TransferValidationResult validationResult = validator.validate(consumerRecord.value(), amount);
      if (!validationResult.isValid()) {
        context.unwrap(HibernateConstraintValidatorContext.class)
               .addExpressionVariable("errorMessage", validationResult.getErrorMessage());
      }
    
      return validationResult.isValid();

    } catch (TimeoutException | InterruptedException | ExecutionException e) {
      context.unwrap(HibernateConstraintValidatorContext.class)
             .addExpressionVariable("errorMessage", "sorry, our fault, please try again");
      return false;
    }
  }
}