package guru.bonacci.heroes.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.service.AccService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
public class AccController {

  private final AccService accountService;

  
  @GetMapping("/{accountId}/{poolId}")
  public  ResponseEntity<Account>  showAccount(@PathVariable String accountId, @PathVariable String poolId) {
      var accOpt = accountService.showMeTheAccount(accountId, poolId);
      return accOpt.map(acc -> ResponseEntity.ok().body(acc))
          .orElse(ResponseEntity.notFound().build());

  }
  
  @GetMapping("/balance/{accountId}/{poolId}")
  public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountId, @PathVariable String poolId) {
      var balanceOpt = accountService.showMeTheBalance(accountId, poolId);
      return balanceOpt.map(bal -> ResponseEntity.ok().body(bal))
          .orElse(ResponseEntity.notFound().build());
  }
}
