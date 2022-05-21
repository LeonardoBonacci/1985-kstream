package guru.bonacci.heroes.accountcdc;

import static guru.bonacci.heroes.kafka.Constants.ONLY_POOL;

import java.math.BigDecimal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import guru.bonacci.heroes.domain.AccountCDC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class AppAccountCDC {

	public static void main(String[] args) {
		SpringApplication.run(AppAccountCDC.class, args);
	}

	private final AccountProperties accountProperties;
	private final AccountProducer accountProducer;

	
  @Scheduled(fixedRate = 120000)
  public void cdc() {
    accountProperties.getAccounts()
      .stream()
      .map(accountId -> AccountCDC.builder()
                          .poolId(ONLY_POOL)
                          .accountId(accountId)
                          .startAmount(BigDecimal.ZERO)
                          .build())
      .peek(account -> log.info("upserting account {} ", account))
      .forEach(accountProducer::send);
    log.info("well done");
  }
}
