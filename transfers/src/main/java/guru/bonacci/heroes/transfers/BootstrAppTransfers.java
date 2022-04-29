package guru.bonacci.heroes.transfers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BootstrAppTransfers {

	public static void main(String[] args) {
		SpringApplication.run(BootstrAppTransfers.class, args);
	}
}
