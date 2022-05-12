package guru.bonacci.heroes.accountcache.controller;

import java.util.Arrays;
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

import guru.bonacci.heroes.accountcache.KafkaStreamsService;
import guru.bonacci.heroes.domain.Account;

@RequestMapping("state")
@RestController
public class InteractiveQueriesRestService {

  private final KafkaStreamsService streams;
  private final MetadataService metadataService;
  private final HostInfo hostInfo;

  InteractiveQueriesRestService(final KafkaStreamsService streams, final MetadataService metadataService) {
    this.streams = streams;
    this.metadataService = metadataService;
    this.hostInfo = new HostInfo("localhost", 8080);
  }

  @GetMapping("/keyvalue/{storeName}/{key}")
  public KeyValueBean byKey(@PathVariable("storeName") final String storeName,
                            @PathVariable("key") final String key) {

    final HostStoreInfo hostStoreInfo = streamsMetadataForStoreAndKey(storeName, key);
    if (!thisHost(hostStoreInfo)){
       return fetchByKey(hostStoreInfo, "/state/keyvalue/"+storeName+"/"+key);
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
  @GetMapping("/keyvalue/{storeName}/all")
  public List<KeyValueBean> all(@PathVariable("storeName") final String storeName) {

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
    System.out.println(url);
    var kvBean = restTemplate.getForObject(url, KeyValueBean.class);
    return kvBean;
  }

  /**
   * Get the metadata for all of the instances of this Kafka Streams application
   * @return List of {@link HostStoreInfo}
   */
  @GetMapping("/instances")
  public List<HostStoreInfo> streamsMetadata() {
    return metadataService.streamsMetadata();
  }

  /**
   * Get the metadata for all instances of this Kafka Streams application that currently
   * has the provided store.
   * @param store   The store to locate
   * @return  List of {@link HostStoreInfo}
   */
  @GetMapping("/instances/{storeName}")
  public List<HostStoreInfo> streamsMetadataForStore(@PathVariable("storeName") final String store) {
    return metadataService.streamsMetadataForStore(store);
  }

  /**
   * Find the metadata for the instance of this Kafka Streams Application that has the given
   * store and would have the given key if it exists.
   * @param store   Store to find
   * @param key     The key to find
   * @return {@link HostStoreInfo}
   */
  @GetMapping("/instance/{storeName}/{key}")
  public HostStoreInfo streamsMetadataForStoreAndKey(@PathVariable("storeName") final String store,
                                                     @PathVariable("key") final String key) {
    return metadataService.streamsMetadataForStoreAndKey(store, key, new StringSerializer());
  }

  private boolean thisHost(final HostStoreInfo host) {
    return host.getHost().equals(hostInfo.host()) &&
           host.getPort() == hostInfo.port();
  }
}

