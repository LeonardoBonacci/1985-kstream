package guru.bonacci.heroes.accountstore.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoStoreException extends RuntimeException {

  private static final long serialVersionUID = 1L;
}