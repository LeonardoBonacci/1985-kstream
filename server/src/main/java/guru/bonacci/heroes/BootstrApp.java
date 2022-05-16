package guru.bonacci.heroes;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import guru.bonacci.heroes.domain.Transfer;
import guru.bonacci.heroes.service.AccountService;

@SpringBootApplication
public class BootstrApp {

  public static void main(String[] args) {
    SpringApplication.run(BootstrApp.class, args);
  }

  @Bean
  CommandLineRunner demo(AccountService service) {
    return args -> {
      var transfers = Arrays.asList(
        Transfer.builder().transferId(UUID.randomUUID().toString()).poolId("coro").from("aa").to("bb").amount(BigDecimal.valueOf(1.01)).when(System.currentTimeMillis()).build(),
        Transfer.builder().transferId(UUID.randomUUID().toString()).poolId("coro").from("aa").to("bb").amount(BigDecimal.valueOf(1.01)).when(System.currentTimeMillis()).build()
      );    
      transfers.forEach(tf -> service.process(tf));
    };
  }
}
