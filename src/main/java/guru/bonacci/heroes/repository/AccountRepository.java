package guru.bonacci.heroes.repository;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Repository
@ConfigurationProperties(prefix = "account") 
public class AccountRepository {

  private List<String> accounts;
}
