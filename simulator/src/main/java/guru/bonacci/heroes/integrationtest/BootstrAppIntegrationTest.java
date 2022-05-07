package guru.bonacci.heroes.integrationtest;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class BootstrAppIntegrationTest {

  private final MockOrStub mockOrStub;

  @Value("${client.hostAndPort}")
  private String clientHostAndPort;
  
  public static void main(String[] args) {
		SpringApplication.run(BootstrAppIntegrationTest.class, args);
	}
	

	@Scheduled(fixedRate = 1000)
	public void showMeTheMoney() {
	  WebClient client = WebClient.builder()
	      .baseUrl(clientHostAndPort)
	      .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	      .build();
	  
	  var pool = MockOrStub.ONLY_BUT_NOT_LONELY_TEST_POOL;
    var from = mockOrStub.getRandomAccount();
    var to = mockOrStub.getRandomAccount();
    var amount = mockOrStub.getRandomAmount(500);

    var transfer = new TransferDto(pool, from, to, amount);
    client.post()
      .uri("/transfers")
      .body(Mono.just(transfer), TransferDto.class)
      .retrieve()
      .toEntity(String.class)
      .subscribe(id -> log.info("transfer id {}", id));
	}    
}
