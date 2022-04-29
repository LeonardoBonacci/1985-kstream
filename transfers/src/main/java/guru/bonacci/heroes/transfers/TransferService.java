package guru.bonacci.heroes.transfers;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.transfers.tip.TIPService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService {

  private final TIPService tipService;
  private final TransferProducer transferProducer;

//  @Transactional
  public Transfer transfer(Transfer transfer) {
    Objects.requireNonNull(transfer.getTransferId(), "cheating..");

    if (!tipService.proceed(transfer)) {
      throw new TooManyRequestsException("the reward of patience..");
    }
    transferProducer.send(transfer);
    return transfer;
  }
  
  @ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
  public static class TooManyRequestsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TooManyRequestsException() {
        super();
    }
    
    public TooManyRequestsException(String message) {
        super(message);
    }
  }
}
