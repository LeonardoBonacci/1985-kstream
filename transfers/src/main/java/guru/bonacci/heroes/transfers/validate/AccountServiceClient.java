package guru.bonacci.heroes.transfers.validate;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import guru.bonacci.heroes.transfers.TransferDto;

@FeignClient(value = "placeholder", url = "localhost:8082")
public interface AccountServiceClient {

  @RequestMapping(method = RequestMethod.POST, value = "/transfers/validate", consumes = "application/json")
  TransferValidationResult validateTransfer(TransferDto dto);
}