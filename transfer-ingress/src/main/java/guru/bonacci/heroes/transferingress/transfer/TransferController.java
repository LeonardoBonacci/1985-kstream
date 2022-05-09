package guru.bonacci.heroes.transferingress.transfer;

import java.util.UUID;
import java.util.concurrent.Callable;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.transferingress.tip.TIPCache;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class TransferController {

  private final TransferService service;
  

  @PostMapping
  public Callable<Transfer> transfer(@Valid @RequestBody TransferDto dto) {
    var transfer = toTf(dto);
    return () -> service.transfer(transfer);
  }

  
  private static Transfer toTf(TransferDto dto) {
    return Transfer.builder()
            .transferId(generateUUID())
            .poolId(dto.getPoolId())
            .from(dto.getFrom())
            .to(dto.getTo())
            .amount(dto.getAmount())
            .build();
  }
  
  private static String generateUUID() {
    var id = UUID.randomUUID().toString();
    return id.startsWith(TIPCache.LOCK_PREFIX) ? generateUUID() : id;
  }
}
