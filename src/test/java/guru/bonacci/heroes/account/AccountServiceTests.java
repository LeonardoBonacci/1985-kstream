package guru.bonacci.heroes.account;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountServiceTests {

  @Autowired
  private AccountService service;

  
  @Test
  void shouldPerformBasicAccounting() throws Exception {
    service.processTx(Transaction.builder().from("a").to("b").amount(100).build());
    service.processTx(Transaction.builder().from("b").to("c").amount(50).build());
    service.processTx(Transaction.builder().from("c").to("a").amount(20).build());

    var accountA = service.showMeTheAccount("a").get();
    assertThat(accountA.getTransactions()).hasSize(2);
    assertThat(service.getBalance("a")).isEqualTo(-80);
    
    var accountB = service.showMeTheAccount("b").get();
    assertThat(accountB.getTransactions()).hasSize(2);
    assertThat(service.getBalance("b")).isEqualTo(50);

    var accountC = service.showMeTheAccount("c").get();
    assertThat(accountC.getTransactions()).hasSize(2);
    assertThat(service.getBalance("c")).isEqualTo(30);

    var accountD = service.showMeTheAccount("d");
    assertThat(accountD.isEmpty()).isTrue();
  }
}
