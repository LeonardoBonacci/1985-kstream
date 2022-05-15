package guru.bonacci.heroes.transferingress.transfer;

import static guru.bonacci.heroes.domain.Account.identifier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.domain.TransferInProgress;
import guru.bonacci.heroes.transferingress.tip.TIPRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService {

  private final TIPRepository cache;
  private final TransferProducer transferProducer;
  
  @Transactional
  public Transfer transfer(Transfer transfer) {
    var fromTip = toFromTIP(transfer);
    var toTip = toToTIP(transfer);
    
    cache.saveAll(ImmutableMap.of(
      fromTip.getPoolAccountId(), fromTip, 
      toTip.getPoolAccountId(), toTip));

    return transferProducer.send(transfer);
  }
  
  private TransferInProgress toFromTIP(Transfer transfer) {
    return new TransferInProgress(identifier(transfer.getPoolId(), transfer.getFrom()), transfer.getTransferId());  
  }
  
  private TransferInProgress toToTIP(Transfer transfer) {
    return new TransferInProgress(identifier(transfer.getPoolId(), transfer.getTo()), transfer.getTransferId());  
  }
}
