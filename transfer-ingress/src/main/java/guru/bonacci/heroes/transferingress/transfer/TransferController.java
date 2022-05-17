package guru.bonacci.heroes.transferingress.transfer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.transferingress.validation.CheckLock;
import guru.bonacci.heroes.transferingress.validation.CheckTransfer;
import guru.bonacci.heroes.transferingress.validation.TransferToValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class TransferController {

  private final TransferService service;
  private final Validator validator;

  
  @PostMapping
  public Callable<Transfer> transfer(@RequestBody @Valid TransferDto dto) {
    return () -> {
      StopWatch watch = new StopWatch();
      watch.start();
  
      var transfer = TransferController.toTf(dto);
      // to avoid extra complexity we validate transfer here instead of in the service
      validateInput(transfer);
      var result = service.transfer(transfer);
  
      watch.stop();
      log.info("Processing Time : {}", watch.getTime()); 
    
      return result;
    };  
  }
  
  private void validateInput(Transfer transfer) {
    var transferToValidate = TransferToValidate.from(transfer);
    checkViolations(validator.validate(transferToValidate, CheckLock.class));
    checkViolations(validator.validate(transferToValidate, CheckTransfer.class));
  }
  
  private void checkViolations(Set<ConstraintViolation<TransferToValidate>> violations) {
    if (!violations.isEmpty()) {
      var sb = new StringBuilder();
      for (ConstraintViolation<TransferToValidate> constraintViolation : violations) {
          sb.append(constraintViolation.getMessage());
      }
      throw new ConstraintViolationException(sb.toString(), violations);
    }
  }
  
  static Transfer toTf(TransferDto dto) {
    return Transfer.builder()
            .transferId(UUID.randomUUID().toString())
            .poolId(dto.getPoolId())
            .from(dto.getFrom())
            .to(dto.getTo())
            .amount(dto.getAmount())
            .build();
  }
}
