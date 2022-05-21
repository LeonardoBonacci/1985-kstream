package guru.bonacci.heroes.cdc;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import guru.bonacci.heroes.domain.AccountCDC;
import guru.bonacci.heroes.domain.PoolCDC;
import guru.bonacci.heroes.domain.PoolType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafka
@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class AppCDC {

	public static void main(String[] args) {
		SpringApplication.run(AppCDC.class, args);
	}

	private final CoroProperties coroProperties;
  private final CoroProperties coro2Properties;
	private final KafkaProducer kafkaProducer;

	
  @Scheduled(fixedRate = 2000)
  public void coro() {
    coroProperties.getAccounts()
      .stream()
      .map(accountId -> AccountCDC.builder()
                          .poolId("coro")
                          .accountId(accountId)
                          .startAmount(BigDecimal.ZERO)
                          .build())
      .peek(account -> log.info("upserting account {} ", account))
      .forEach(kafkaProducer::send);
    log.info("well done coro");
  }
  
  @Scheduled(fixedRate = 3000)
  public void coro2() {
    coro2Properties.getAccounts()
      .stream()
      .map(accountId -> AccountCDC.builder()
                          .poolId("coro2")
                          .accountId(accountId)
                          .startAmount(BigDecimal.ZERO)
                          .build())
      .peek(account -> log.info("upserting account {} ", account))
      .forEach(kafkaProducer::send);
    log.info("well done coro2");
  }

  @Scheduled(fixedRate = 1000)
  public void pool() {
    Arrays.asList("coro", "coro2")
      .stream()
      .map(poolId -> PoolCDC.builder()
                          .poolId(poolId)
                          .type(PoolType.SARDEX)
                          .build())
      .peek(pool -> log.info("upserting pool {} ", pool))
      .forEach(kafkaProducer::send);
    log.info("well done sardex");
  }

}
