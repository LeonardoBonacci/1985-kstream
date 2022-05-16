package guru.bonacci.heroes.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.service.AccountService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accService;

  
  @GetMapping("/pools/{poolId}/accounts/{accountId}")
  public  ResponseEntity<Account>  showAccount(@PathVariable String poolId, @PathVariable String accountId) {
      var accOpt = accService.showAccount(accountId, poolId);
      return accOpt.map(acc -> ResponseEntity.ok().body(acc))
          .orElse(ResponseEntity.notFound().build());

  }
  
  @GetMapping("/pools/{poolId}/accounts/{accountId}/balance")
  public ResponseEntity<BigDecimal> getBalance(@PathVariable String poolId, @PathVariable String accountId) {
      var balanceOpt = accService.getBalance(accountId, poolId);
      return balanceOpt.map(bal -> ResponseEntity.ok().body(bal))
          .orElse(ResponseEntity.notFound().build());
  }
}
