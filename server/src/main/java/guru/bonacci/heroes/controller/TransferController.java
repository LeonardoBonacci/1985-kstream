package guru.bonacci.heroes.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.dto.TransferDto;
import guru.bonacci.heroes.service.AccountService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class TransferController {

  private final AccountService service;
  

  @PostMapping
  public ResponseEntity<Void> transfer(@RequestBody TransferDto dto) {
    service.process(toTf(dto));
    return ResponseEntity.noContent().<Void>build();
  }
  
  static Transfer toTf(TransferDto dto) {
    return new Transfer(UUID.randomUUID().toString(), dto.getPoolId(), dto.getFrom(), dto.getTo(), dto.getAmount(), System.currentTimeMillis());
  }
}
