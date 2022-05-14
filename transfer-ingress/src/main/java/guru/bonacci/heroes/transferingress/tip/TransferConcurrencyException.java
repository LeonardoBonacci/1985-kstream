package guru.bonacci.heroes.transferingress.tip;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "the reward of patience..")
public class TransferConcurrencyException extends RuntimeException {

  private static final long serialVersionUID = 1L;

}