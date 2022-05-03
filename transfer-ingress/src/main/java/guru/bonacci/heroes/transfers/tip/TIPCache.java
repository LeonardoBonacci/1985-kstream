package guru.bonacci.heroes.transfers.tip;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TIPCache extends CrudRepository<TransferInProgress, String> {}