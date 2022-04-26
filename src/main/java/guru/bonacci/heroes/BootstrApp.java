package guru.bonacci.heroes;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import guru.bonacci.heroes.domain.Account;
import guru.bonacci.heroes.kafka.AccountProducer;
import guru.bonacci.heroes.repository.AccountRepository;
import guru.bonacci.heroes.service.PoolService;
import guru.bonacci.heroes.service.TfService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BootstrApp {

	public static void main(String[] args) {
		SpringApplication.run(BootstrApp.class, args);
	}

  @Bean
  CommandLineRunner demo( TfService tfService, 
                          AccountRepository accRepo, 
                          AccountProducer accProducer, 
                    final PoolService poolService) {
	  return args -> {
	    // combine with k8s spec.containers.livenessProbe.initialDelaySeconds: x
	    Thread.sleep(1000);
	    accRepo.getAccounts().stream()
	        .peek(accName -> log.info("trying to add acc {} for fun", accName))
	        .map(accName -> Account.builder().accountId(accName).poolId(PoolService.ONLY_POOL_NAME).build())
	        .filter(acc -> poolService.notContainsAccount(acc.getPoolId(), acc.getAccountId()))
          .peek(acc -> log.info("adding acc {} for fun", acc.getAccountId()))
	        .forEach(accProducer::send);

//	    var tfs = Arrays.asList(
//	        new TransferDto("heroes", "a", "b", valueOf(100))
//	        );
//	    
//	    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
//	    
//	    for (TransferDto dto : tfs) {
//	      Set<ConstraintViolation<TransferDto>> violations = 
//	          validator.validate(dto, BasicCheck.class, IntermediateCheck.class, AdvancedCheck.class);
//	      if (violations.isEmpty()) {
//	        tfService.transfer(TfController.toTf(dto));
//	      }
//	      
//	    }
    };
  }
}
