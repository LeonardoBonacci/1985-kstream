package guru.bonacci.heroes.accountstore.controller;

import org.apache.kafka.streams.state.HostInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RPCConfig {

  @Bean
  public HostInfo hostInfo(@Value("${server.port}") int port) {
    return new HostInfo("localhost", port);
  }
}
