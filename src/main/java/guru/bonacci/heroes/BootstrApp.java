package guru.bonacci.heroes;

import static java.math.BigDecimal.valueOf;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import guru.bonacci.heroes.kafka.Transfer;
import guru.bonacci.heroes.transfer.TfProducer;

@SpringBootApplication
public class BootstrApp {

	public static void main(String[] args) {
		SpringApplication.run(BootstrApp.class, args);
	}

	@Bean
  CommandLineRunner demo(TfProducer tfProducer) {
	  return args -> {
	    tfProducer.transfer(new Transfer("heroes", "a", "b", valueOf(100), System.currentTimeMillis()));
      tfProducer.transfer(new Transfer("heroes", "b", "c", valueOf(50), System.currentTimeMillis()));
      tfProducer.transfer(new Transfer("heroes", "c", "a", valueOf(20), System.currentTimeMillis()));
    };
  }
}
