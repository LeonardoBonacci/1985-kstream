package guru.bonacci.heroes.account;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import guru.bonacci.heroes.kafka.Transfer;


@SpringBootTest
class AccServiceTest {

  @Autowired
  private AccService service;

  @BeforeEach
  void init() {
    service.cleanSheet();
  }
  
  @Test
  void shouldPerformBasicAccounting() throws Exception {
    service.process(new Transfer("heroes", "a", "b", valueOf(100), System.currentTimeMillis()));
    service.process(new Transfer("heroes", "b", "c", valueOf(50), System.currentTimeMillis()));
    service.process(new Transfer("heroes", "c", "a", valueOf(20), System.currentTimeMillis()));

    var accountA = service.showMeTheAccount("a", "heroes").get();
    assertThat(accountA.getTransactions()).hasSize(2);
    assertThat(service.getBalance("a", "heroes").get()).isEqualTo(valueOf(-80));
    
    var accountB = service.showMeTheAccount("b", "heroes").get();
    assertThat(accountB.getTransactions()).hasSize(2);
    assertThat(service.getBalance("b", "heroes").get()).isEqualTo(valueOf(50));

    var accountC = service.showMeTheAccount("c", "heroes").get();
    assertThat(accountC.getTransactions()).hasSize(2);
    assertThat(service.getBalance("c", "heroes").get()).isEqualTo(valueOf(30));

    var accountD = service.showMeTheAccount("d", "heroes");
    assertThat(accountD.isEmpty()).isTrue();
  }
  
  @Test
  void shouldSortTxs() throws Exception {
    service.process(new Transfer("heroes", "a", "b", valueOf(100), System.currentTimeMillis()));
    Thread.sleep(10);
    service.process(new Transfer("heroes", "b", "c", valueOf(50), System.currentTimeMillis()));
    Thread.sleep(10);
    service.process(new Transfer("heroes", "c", "a", valueOf(20), System.currentTimeMillis()));

    var accountA = service.showMeTheAccount("a", "heroes").get();
    assertThat(accountA.getTransactions()).hasSize(2);
    assertThat(accountA.getTransactions().get(0).getWhen()).isLessThan(accountA.getTransactions().get(1).getWhen());
  }

}
