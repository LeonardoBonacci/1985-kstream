package guru.bonacci.heroes.transferingress.tip;

import static guru.bonacci.heroes.domain.Account.identifier;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.domain.TransferInProgress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TIPService {

  private final TIPCache cache;

  
  public boolean proceed(Transfer transfer) {
    var fromTip = toFromTIP(transfer);
    if (cache.existsById(fromTip.getPoolAccountId())) {
      return false;
    }

    var toTip = toToTIP(transfer);
    if (cache.existsById(toTip.getPoolAccountId())) {
      return false;
    }

    cache.saveAll(ImmutableMap.of(
                    fromTip.getPoolAccountId(), 
                    fromTip, toTip.getPoolAccountId(), toTip));
    return true;
  }
  
  
  public Boolean isBlocked(Transfer transfer) {
    return isBlocked(identifier(transfer.getPoolId(), transfer.getFrom())) || 
           isBlocked(identifier(transfer.getPoolId(), transfer.getTo()));
  }

  private Boolean isBlocked(String identifier) {
    if (!cache.lock(identifier)) {
      log.warn("attempt to override lock {}", identifier);
      return true;
    }
    return false;
  }
  
  
  private TransferInProgress toFromTIP(Transfer transfer) {
    return new TransferInProgress(identifier(transfer.getPoolId(), transfer.getFrom()), transfer.getTransferId());  
  }
  
  private TransferInProgress toToTIP(Transfer transfer) {
    return new TransferInProgress(identifier(transfer.getPoolId(), transfer.getTo()), transfer.getTransferId());  
  }
}
