package guru.bonacci.heroes.transfers;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class TransferController {

  private final TransferService service;
  

  @PostMapping
  public ResponseEntity<Void> transfer(@Valid @RequestBody TransferDto dto) {
    service.transfer(toTf(dto));
    return ResponseEntity.noContent().<Void>build();
  }
  
  static Transfer toTf(TransferDto dto) {
    return new Transfer(dto.getPoolId(), dto.getFrom(), dto.getTo(), dto.getAmount(), System.currentTimeMillis());
  }
}
