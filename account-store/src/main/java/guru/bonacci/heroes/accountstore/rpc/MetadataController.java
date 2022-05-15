package guru.bonacci.heroes.accountstore.rpc;

import static guru.bonacci.heroes.accountstore.BootstrAppAccountStore.STORE;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import guru.bonacci.heroes.accountstore.KafkaStreamsService;
import guru.bonacci.heroes.domain.Account;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("metadata")
@RequiredArgsConstructor
public class MetadataController {

  private final KafkaStreamsService streams;
  private final MetadataService metadataService;


  @GetMapping
  public List<HostStoreInfo> streamsMetadata() {
    return metadataService.streamsMetadata();
  }

  @GetMapping("/accounts")
  public List<HostStoreInfo> streamsMetadataForStore() {
    return metadataService.streamsMetadataForStore(STORE);
  }

  @GetMapping("/accounts/{accountId}")
  public HostStoreInfo streamsMetadataForKey(@PathVariable("accountId") final String accountId) {
    return metadataService.streamsMetadataForStoreAndKey(STORE, accountId, new StringSerializer());
  }

  // for testing - returns this instance's accounts
  @GetMapping("/all")
  public List<KeyValueBean> all() {

    final ReadOnlyKeyValueStore<String, Account> store = streams.waitForAccountStore();
    if (store == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // Get the values from the store
    var all = StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(store.all(), Spliterator.ORDERED),
        false);

    return all.map(kv -> new KeyValueBean(kv.key, kv.value)).collect(Collectors.toList());
  }

}

