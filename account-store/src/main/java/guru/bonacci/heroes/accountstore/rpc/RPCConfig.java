package guru.bonacci.heroes.accountstore.rpc;

import org.apache.kafka.streams.state.HostInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RPCConfig {

  @Bean
  public HostInfo hostInfo( @Value("${server.port}") int port, 
                            @Value("${spring.cloud.client.ip-address}") String ip,
                            @Value("${spring.cloud.client.hostname}") String host) {

    log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    log.warn("server.port {}" , port);
    log.warn("client.ip-address {}" , ip);
    log.warn("client.hostname {}" , host);
    log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    return new HostInfo(ip, port);
  }
  
  @Bean
  public RestTemplate rest() {
    return new RestTemplate();
  }
}
