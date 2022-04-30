package guru.bonacci.heroes.tippurger;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TIPCache extends CrudRepository<TransferInProgress, String> {}