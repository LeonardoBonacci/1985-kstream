package guru.bonacci.heroes.account.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.account.service.TransferValidationService;
import guru.bonacci.heroes.domain.TransferDto;
import guru.bonacci.heroes.domain.TransferValidationRequest;
import guru.bonacci.heroes.domain.TransferValidationResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TransferValidationController {

  private final TransferValidationService transferService;


  @PostMapping(path = "/transfers/validate")
  public TransferValidationResult transfer(@Valid @RequestBody TransferDto dto) {
    return transferService.getTransferValidationInfo(toRequest(dto));
  }
  
  static TransferValidationRequest toRequest(TransferDto dto) {
    return new TransferValidationRequest(dto.getPoolId(), dto.getFrom(), dto.getTo(), dto.getAmount());
  }
}
