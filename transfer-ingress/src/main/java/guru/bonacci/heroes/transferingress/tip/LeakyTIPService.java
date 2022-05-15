package guru.bonacci.heroes.transferingress.tip;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import guru.bonacci.heroes.domain.Transfer;

@Profile("default")
@Service
public class LeakyTIPService implements ITIPService {


  @Override
  public boolean proceed(Transfer transfer) {    
    return true;
  }
  
  public boolean isBlocked(Transfer transfer) {
    return false;
  }
}
