package guru.bonacci.heroes.controller;

import java.util.List;

import javax.validation.constraints.Size;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import guru.bonacci.heroes.service.PoolService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("pools")
@RequiredArgsConstructor
public class PoolController {

  private final PoolService service;
  

  @GetMapping("/{poolId}/{q}")
  public  List<String> searchAccounts(@PathVariable String poolId, 
                                      @RequestParam @Size(max = 3) String q) {
      return service.searchMembers(poolId, q); 
  }
}
