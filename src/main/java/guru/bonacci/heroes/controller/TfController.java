package guru.bonacci.heroes.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.dto.TransferDto;
import guru.bonacci.heroes.service.TfService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class TfController {

  private final TfService service;
  

  @PostMapping
  public ResponseEntity<Void> transfer(@Valid @RequestBody TransferDto dto) {
    service.transfer(toTf(dto));
    return ResponseEntity.noContent().<Void>build();
  }
  
  // public for demo ingestion
  public static Transfer toTf(TransferDto dto) {
    return new Transfer(dto.getPoolId(), dto.getFrom(), dto.getTo(), dto.getAmount(), System.currentTimeMillis());
  }
}
