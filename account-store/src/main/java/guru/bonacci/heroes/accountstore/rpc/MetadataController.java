package guru.bonacci.heroes.accountstore.rpc;

import static guru.bonacci.heroes.accountstore.AppAccountStore.STORE;
import static guru.bonacci.heroes.domain.Account.*;

import java.util.List;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("metadata")
@RequiredArgsConstructor
public class MetadataController {

  private final MetadataService metadataService;


  @GetMapping
  public List<HostStoreInfo> streamsMetadata() {
    return metadataService.streamsMetadata();
  }

  @GetMapping("/accounts")
  public List<HostStoreInfo> streamsMetadataForStore() {
    return metadataService.streamsMetadataForStore(STORE);
  }

  @GetMapping("/pools/{poolId}/accounts/{accountId}")
  public ResponseEntity<HostStoreInfo> streamsMetadataForKey(@PathVariable("poolId") String poolId, @PathVariable("accountId") String accountId) {
    var hostStoreInfoOpt = metadataService.streamsMetadataForStoreAndKey(STORE, identifier(poolId, accountId), new StringSerializer());
    return hostStoreInfoOpt.map(info -> ResponseEntity.ok().body(info))
        .orElse(ResponseEntity.notFound().build());
  }
}

