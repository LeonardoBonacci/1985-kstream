package guru.bonacci.heroes.transferingress.tip;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "try again in a second")
public class TransferLockedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

}