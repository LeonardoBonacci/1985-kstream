package guru.bonacci.heroes.account.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.account.InteractiveQueries;
import guru.bonacci.heroes.account.PipelineMetadata;
import guru.bonacci.heroes.account.service.TransferValidationService;
import guru.bonacci.heroes.domain.TransferDto;
import guru.bonacci.heroes.domain.TransferValidationRequest;
import guru.bonacci.heroes.domain.TransferValidationResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TransferValidationController {

  private static final Integer SSL_PORT = 8443;

  private final TransferValidationService transferService;
  private final InteractiveQueries interactiveQueries;


  @PostMapping(path = "/transfers/validate")
  public TransferValidationResult transfer(@Valid @RequestBody TransferDto dto) {
    return transferService.getTransferValidationInfo(toRequest(dto));
  }
  
  static TransferValidationRequest toRequest(TransferDto dto) {
    return new TransferValidationRequest(dto.getPoolId(), dto.getFrom(), dto.getTo(), dto.getAmount());
  }
  
  
  @GetMapping("/transfers/validate/{accountId}")
  public  ResponseEntity<TransferValidationResult> showAccount(@PathVariable String accountId) throws UnknownHostException {
      var result = interactiveQueries.getTransferValidation(accountId);
      if (result.getResult().isPresent()) {
        return ResponseEntity.ok(result.getResult().get());
    } else if (result.getHost().isPresent()) {
        URI otherUri = getOtherUri(result.getHost().get(), result.getPort().getAsInt(), accountId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(otherUri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).build();
    }
  }
  
  @GetMapping("/meta-data")
  public List<PipelineMetadata> getMetaData() {
      return interactiveQueries.getMetaData();
  }

  private URI getOtherUri(String host, int port, String accountId) {
      try {
          String scheme = (port == SSL_PORT) ? "https" : "http";
          return new URI(scheme + "://" + host + ":" + port + "/accounts/" + accountId);
      } catch (URISyntaxException e) {
          throw new RuntimeException(e);
      }
  }
}
