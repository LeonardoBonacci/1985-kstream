package guru.bonacci.heroes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.bonacci.heroes.dto.TransferDto;
import guru.bonacci.heroes.service.TfService;
 
@WebMvcTest(value = TfController.class)
public class TfControllerTest {
 
    @MockBean TfService service;

    @MockBean LocalValidatorFactoryBean validator; // disables @Valid

    @Autowired MockMvc mvc; 

    @Autowired ObjectMapper objectMapper;

    
    @Test
    public void statusCreated() throws Exception {
      when(service.transfer(any())).thenReturn(true);
        
      var dto = new TransferDto("heroes", "foo", "bar", BigDecimal.valueOf(10));
      
      mvc.perform(post("/transfers")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
              .andExpect(status().is2xxSuccessful());
    }
}