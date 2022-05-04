package guru.bonacci.heroes.transferingress.tip;

import static guru.bonacci.heroes.domain.Account.identifier;

import org.springframework.stereotype.Service;

import guru.bonacci.heroes.domain.Transfer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TIPService {

  private final TIPCache repo;

  public boolean proceed(Transfer transfer) {
    var tip = toTIP(transfer);
    if (repo.existsById(tip.getAccount())) {
      return false;
    }

    repo.save(tip);
    return true;
  }
  
  private TransferInProgress toTIP(Transfer transfer) {
    return TransferInProgress.builder()
        .account(identifier(transfer.getPoolId(), transfer.getFrom()))
        .transferId(transfer.getTransferId())
        .build();
  }
}