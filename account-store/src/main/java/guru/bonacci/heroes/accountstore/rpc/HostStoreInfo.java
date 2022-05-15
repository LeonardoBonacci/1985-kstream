package guru.bonacci.heroes.accountstore.rpc;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostStoreInfo {

  private String host;
  private int port;
  private Set<String> storeNames;
}
