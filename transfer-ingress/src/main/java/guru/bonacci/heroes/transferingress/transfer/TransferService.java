package guru.bonacci.heroes.transferingress.transfer;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.transferingress.tip.TIPService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService {

  private final TIPService tipService;
  private final TransferProducer transferProducer;

  
  @Transactional
  public Transfer transfer(Transfer transfer) {
    Objects.requireNonNull(transfer.getTransferId(), "cheating..");
    
    if (tipService.isBlocked(transfer)) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "try again in a second..");
    }

    return doTransfer(transfer);
  }

  private Transfer doTransfer(Transfer transfer) {
    Objects.requireNonNull(transfer.getTransferId(), "cheating..");

    if (!tipService.proceed(transfer)) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "the reward of patience..");
    }
    
    return transferProducer.send(transfer);
  }
}
