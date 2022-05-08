package guru.bonacci.heroes.transferingress.tip;

import static guru.bonacci.heroes.domain.Account.identifier;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import guru.bonacci.heroes.domain.Transfer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TIPService {

  private final TIPRepository repo;

  public boolean proceed(Transfer transfer) {
    var fromTip = toFromTIP(transfer);
    if (repo.existsById(fromTip.getPoolAccountId())) {
      return false;
    }

    var toTip = toToTIP(transfer);
    if (repo.existsById(toTip.getPoolAccountId())) {
      return false;
    }

    repo.saveAll(ImmutableMap.of(
                    fromTip.getPoolAccountId(), 
                    fromTip, toTip.getPoolAccountId(), toTip));
    return true;
  }
  
  private TransferInProgress toFromTIP(Transfer transfer) {
    return new TransferInProgress(identifier(transfer.getPoolId(), transfer.getFrom()), transfer.getTransferId());  
  }
  
  private TransferInProgress toToTIP(Transfer transfer) {
    return new TransferInProgress(identifier(transfer.getPoolId(), transfer.getTo()), transfer.getTransferId());  
  }
}
