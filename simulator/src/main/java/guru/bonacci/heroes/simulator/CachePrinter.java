package guru.bonacci.heroes.simulator;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CachePrinter {

  private final TIPRepository tipRepo;

  
  @Scheduled(fixedRate = 10000)
  public void account() {
    log.info("--------- in cache --------");
    tipRepo.getAll().forEach(kv -> log.info("{}:{}", kv.getFirst(), kv.getSecond()));
    log.info("---------------------------");
  }    
}
