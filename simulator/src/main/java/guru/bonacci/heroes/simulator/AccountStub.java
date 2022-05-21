package guru.bonacci.heroes.simulator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import guru.bonacci.heroes.domain.AccountCDC;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AccountStub {

  public static final String ONLY_BUT_NOT_LONELY_TEST_POOL = "coro";

  public List<String> accounts = Lists.newArrayList(
      "aaa", "bbb", "ccc", "ddd", "eee", "fff", "ggg", "hhh", "iii", "jjj", "kkk", "lll", "mmm", "nnn", "ooo", "ppp", "qqq", "rrr", "sss", "ttt", "uuu", "vvv", "www", "xxx", "yyy", "zzz");


  @KafkaListener(topics = "account", groupId = "simulator")
  public void listen(@Payload AccountCDC account) {
    log.info("receiving account {}", account);
    accounts.add(account.getAccountId());
  }
  
  

  String getRandomAccount() {
    var random = new Random();
    return accounts.get(random.nextInt(accounts.size()));
  }
  
  BigDecimal getRandomAmount(int range) {
    BigDecimal max = new BigDecimal(range);
    BigDecimal randFromDouble = new BigDecimal(Math.random());
    BigDecimal actualRandomDec = randFromDouble.multiply(max);
    actualRandomDec = actualRandomDec.setScale(2, RoundingMode.DOWN);
    return actualRandomDec;
  }
}
