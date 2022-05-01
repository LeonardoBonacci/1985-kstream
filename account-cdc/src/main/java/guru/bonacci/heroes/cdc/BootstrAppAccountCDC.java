package guru.bonacci.heroes.cdc;

import static guru.bonacci.heroes.kafka.HeroesConstants.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import guru.bonacci.heroes.domain.AccountCDC;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BootstrAppAccountCDC {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppAccountCDC.class, args);
	}

	
	@Bean
  CommandLineRunner cdc(AccountProperties accountProperties, AccountProducer accountProducer) {
    return args -> {
      accountProperties.getAccounts()
        .stream()
        .map(accountId -> AccountCDC.builder().poolId(ONLY_POOL).accountId(accountId).accountName("unused").build())
        .peek(account -> log.info("upserting account {} ", account))
        .forEach(accountProducer::send);
    };
  }
}
