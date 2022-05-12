package guru.bonacci.heroes.transferingress.tip;

import guru.bonacci.heroes.domain.Transfer;

public interface ITIPService {

  boolean proceed(Transfer transfer);  
  
  boolean isBlocked(Transfer transfer);
}
