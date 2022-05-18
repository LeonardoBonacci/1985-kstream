package guru.bonacci.heroes.transferingress.transfer;

import static guru.bonacci.heroes.domain.Account.identifier;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.transferingress.tip.TIPRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

  private final TIPRepository cache;
  private final TransferProducer transferProducer;
  

  @Transactional
  public Transfer transfer(Transfer transfer) {
    // tip is key:value of poolAccountId:transferId
    var fromTip = toFromTIP(transfer);
    var toTip = toToTIP(transfer);
    
    cache.saveAll(ImmutableMap.of(
      fromTip.getFirst(), fromTip, 
      toTip.getFirst(), toTip));

    var result = transferProducer.send(transfer);
    log.info("sent {}", result);
    return result;
  }
  
  private Pair<String, String> toFromTIP(Transfer transfer) {
    return Pair.of(identifier(transfer.getPoolId(), transfer.getFrom()), transfer.getTransferId());  
  }
  
  private Pair<String, String> toToTIP(Transfer transfer) {
    return Pair.of(identifier(transfer.getPoolId(), transfer.getTo()), transfer.getTransferId());  
  }
}
