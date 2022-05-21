package guru.bonacci.heroes.cdc;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Setter @Getter
@ConfigurationProperties(prefix = "coro2") 
public class Coro2Properties {

  private List<String> accounts;
}
