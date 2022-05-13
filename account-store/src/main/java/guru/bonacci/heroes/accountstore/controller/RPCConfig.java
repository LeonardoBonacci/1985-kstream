package guru.bonacci.heroes.accountstore.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.kafka.streams.state.HostInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RPCConfig {

  @Bean
  public HostInfo hostInfo(@Value("${server.port}") int port, 
                          @Value("${spring.cloud.client.ip-address}") String ip,
                          @Value("${spring.cloud.client.hostname}") String host) throws UnknownHostException {

    log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    log.warn("server.port {}" , port);
    log.warn("client.ip-address {}" , ip);
    log.warn("client.hostname {}" , host);
    log.warn("inet hostaddress {}", InetAddress.getLocalHost().getHostAddress());
    log.warn("inet hostname {}", InetAddress.getLocalHost().getHostName());
    log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
 
    return new HostInfo(host, port);
  }
}
