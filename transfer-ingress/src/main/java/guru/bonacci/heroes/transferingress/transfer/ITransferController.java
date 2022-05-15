package guru.bonacci.heroes.transferingress.transfer;


import java.util.UUID;
import java.util.concurrent.Callable;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.domain.dto.TransferDto;

public interface ITransferController {

  Callable<Transfer> transfer(TransferDto dto);

  public static Transfer toTf(TransferDto dto) {
    return Transfer.builder()
            .transferId(UUID.randomUUID().toString())
            .poolId(dto.getPoolId())
            .from(dto.getFrom())
            .to(dto.getTo())
            .amount(dto.getAmount())
            .build();
  }
}  