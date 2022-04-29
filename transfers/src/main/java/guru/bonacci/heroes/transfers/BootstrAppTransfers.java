package guru.bonacci.heroes.transfers;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import guru.bonacci.heroes.domain.Transfer;

@EnableFeignClients
@SpringBootApplication
public class BootstrAppTransfers {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransfers.class, args);
	}
	
//  @Bean
  CommandLineRunner demo(TransferService serv) {
    return args -> {
      var t = Transfer.builder().amount(BigDecimal.ONE).from("aa1").to("b").poolId("c1").transferId("abc").build();
      System.out.println(serv.transfer(t));
      var t1 = Transfer.builder().amount(BigDecimal.ONE).from("aaa1").to("b").poolId("c1").transferId("abc").build();
      System.out.println(serv.transfer(t1));
    };
  }
}
