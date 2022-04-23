package guru.bonacci.heroes;

import static java.math.BigDecimal.valueOf;

import java.util.Arrays;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import guru.bonacci.heroes.controller.TfController;
import guru.bonacci.heroes.dto.TransferDto;
import guru.bonacci.heroes.dto.validate.AdvancedCheck;
import guru.bonacci.heroes.dto.validate.BasicCheck;
import guru.bonacci.heroes.dto.validate.IntermediateCheck;
import guru.bonacci.heroes.service.TfService;

@SpringBootApplication
public class BootstrApp {

	public static void main(String[] args) {
		SpringApplication.run(BootstrApp.class, args);
	}

  @Bean
  CommandLineRunner demo(TfService tfService) {
	  return args -> {
	    var tfs = Arrays.asList(
	        new TransferDto("heroes", "a", "b", valueOf(100))
	        );
	    
	    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	    
	    for (TransferDto dto : tfs) {
	      Set<ConstraintViolation<TransferDto>> violations = 
	          validator.validate(dto, BasicCheck.class, IntermediateCheck.class, AdvancedCheck.class);
	      if (violations.isEmpty()) {
	        tfService.transfer(TfController.toTf(dto));
	      }
	      
	    }
    };
  }
}
