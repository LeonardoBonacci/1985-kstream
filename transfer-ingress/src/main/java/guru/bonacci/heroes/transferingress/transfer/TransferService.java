package guru.bonacci.heroes.transferingress.transfer;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.transferingress.tip.ITIPService;
import guru.bonacci.heroes.transferingress.tip.TransferConcurrencyException;
import guru.bonacci.heroes.transferingress.tip.TransferLockedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService {

  private final ITIPService tipService;
  private final TransferProducer transferProducer;
  
  @Transactional
  public Transfer transfer(Transfer transfer) {
    Objects.requireNonNull(transfer.getTransferId(), "cheating..");
    
    if (tipService.isBlocked(transfer)) {
      throw new TransferLockedException();
    }

    return doTransfer(transfer);
  }

  private Transfer doTransfer(Transfer transfer) {
    if (!tipService.proceed(transfer)) {
      throw new TransferConcurrencyException();
    }
    
    return transferProducer.send(transfer);
  }
}
