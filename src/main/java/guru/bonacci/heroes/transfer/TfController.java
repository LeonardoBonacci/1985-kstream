package guru.bonacci.heroes.transfer;

import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.kafka.Transfer;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class TfController {

  private final TfService service;
  

  @PostMapping("/{poolId}")
  public ResponseEntity<Void> transfer(@PathVariable String poolId, @Valid @RequestBody TransferDto dto) throws ExecutionException {
    service.transfer(toTf(dto, poolId));
    return ResponseEntity.noContent().<Void>build();
  }
  
  private Transfer toTf(TransferDto dto, String poolId) {
    return new Transfer(poolId, dto.getFrom(), dto.getTo(), dto.getAmount(), System.currentTimeMillis());
  }
}
