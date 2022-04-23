package guru.bonacci.heroes.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import guru.bonacci.heroes.Transferer;
import guru.bonacci.heroes.domain.Transfer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("!stream")
public class AccServiceDelegator implements Transferer {

  private final AccService service;
  
  @Override
  public boolean fer(Transfer tf) {
    return service.process(tf);
  }
}
