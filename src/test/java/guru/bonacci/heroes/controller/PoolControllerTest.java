package guru.bonacci.heroes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.google.common.collect.ImmutableList;

import guru.bonacci.heroes.service.PoolService;
 
@WebMvcTest(value = PoolController.class)
public class PoolControllerTest {
 
    @MockBean PoolService service;

    @Autowired MockMvc mvc; 

    
    @Test
    public void statusCreated() throws Exception {
      when(service.searchMembers(any(), any())).thenReturn(ImmutableList.of("foo"));
        
      mvc.perform(get("/pools/{poolId}", "heroes").param("q", "foo"))
        .andExpect(status().isOk());
    }
    
    @Test
    public void shouldSupplyQueryString() throws Exception {
      mvc.perform(get("/pools/{poolId}", "heroes"))
        .andExpect(status().is4xxClientError());
    }
}