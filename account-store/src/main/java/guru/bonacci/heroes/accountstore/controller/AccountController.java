package guru.bonacci.heroes.accountstore.controller;

import static guru.bonacci.heroes.accountstore.BootstrAppAccountStore.STORE;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import guru.bonacci.heroes.accountstore.KafkaStreamsService;
import guru.bonacci.heroes.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("state")
@RestController
@RequiredArgsConstructor
public class AccountController {

  private final KafkaStreamsService streams;
  private final MetadataService metadataService;
  private final HostInfo hostInfo;


  @GetMapping("/keyvalue/{key}")
  public KeyValueBean byKey(@PathVariable("key") final String key) {

    final HostStoreInfo hostStoreInfo = streamsMetadataForKey(key);
    log.warn(hostStoreInfo.toString());
    log.warn(hostInfo.toString());
    if (!thisHost(hostStoreInfo)){
       return fetchByKey(hostStoreInfo, "state/keyvalue/" + key);
    }

    // Lookup the KeyValueStore with the provided storeName
    final ReadOnlyKeyValueStore<String, Account> store = streams.accountStore();
    if (store == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // Get the value from the store
    final Account value = store.get(key);
    if (value == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return new KeyValueBean(key, value);
  }
  
  // for testing
  @GetMapping("/keyvalue/all")
  public List<KeyValueBean> all() {

    // Lookup the KeyValueStore with the provided storeName
    final ReadOnlyKeyValueStore<String, Account> store = streams.accountStore();
    if (store == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // Get the values from the store
    var all = StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(store.all(), Spliterator.ORDERED),
        false);

    return all.map(kv -> new KeyValueBean(kv.key, kv.value)).collect(Collectors.toList());
  }

  private KeyValueBean fetchByKey(final HostStoreInfo host, final String path) {
    RestTemplate restTemplate = new RestTemplate();
    var url = String.format("http://%s:%d/%s", host.getHost(), host.getPort(), path);
    log.info(url);
    var kvBean = restTemplate.getForObject(url, KeyValueBean.class);
    return kvBean;
  }

  
  @GetMapping("/instances")
  public List<HostStoreInfo> streamsMetadata() {
    return metadataService.streamsMetadata();
  }

  @GetMapping("/instances/accounts")
  public List<HostStoreInfo> streamsMetadataForStore() {
    return metadataService.streamsMetadataForStore(STORE);
  }

  @GetMapping("/instances/accounts/{key}")
  public HostStoreInfo streamsMetadataForKey(@PathVariable("key") final String key) {
    return metadataService.streamsMetadataForStoreAndKey(STORE, key, new StringSerializer());
  }

  private boolean thisHost(final HostStoreInfo host) {
    return host.getHost().equals(hostInfo.host()) &&
           host.getPort() == hostInfo.port();
  }
  
//  private URI getOtherUri(String host, int port, int id) {
//    try {
//        return new URI("http://" + host + ":" + port + "/weather-stations/data/" + id);
//    }
//    catch (URISyntaxException e) {
//        throw new RuntimeException(e);
//    }
//  }

}

