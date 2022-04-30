package guru.bonacci.heroes.initializer;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BootstrAppAccountInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppAccountInitializer.class, args);
	}

	
	@Bean
  CommandLineRunner demo(AccountProducer accountProducer) {
    return args -> {
      
      //TODO read accounts from file
    };
  }
}
