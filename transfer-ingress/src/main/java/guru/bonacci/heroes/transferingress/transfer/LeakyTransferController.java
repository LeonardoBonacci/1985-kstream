package guru.bonacci.heroes.transferingress.transfer;

import static guru.bonacci.heroes.transferingress.transfer.ITransferController.toTf;

import java.util.concurrent.Callable;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.domain.dto.TransferDto;
import lombok.RequiredArgsConstructor;

@Profile("default")
@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class LeakyTransferController implements ITransferController {

  private final TransferService service;
  

  @PostMapping
  @Override
  public Callable<Transfer> transfer(@RequestBody TransferDto dto) {
    var transfer = toTf(dto);
    return () -> service.transfer(transfer);
  }
}