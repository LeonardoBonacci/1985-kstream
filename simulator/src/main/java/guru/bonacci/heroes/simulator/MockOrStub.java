package guru.bonacci.heroes.simulator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

@Component
public class MockOrStub {

  public static final String ONLY_BUT_NOT_LONELY_TEST_POOL = "coro";

  public List<String> accounts = ImmutableList
      .of("aaa", "bbb", "ccc", "ddd", "eee", "fff", "ggg", "hhh", "iii", "jjj", "kkk", "lll", "mmm", "nnn", "ooo", "ppp", "qqq", "rrr", "sss", "ttt", "uuu", "vvv", "www", "xxx", "yyy", "zzz");
  

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
