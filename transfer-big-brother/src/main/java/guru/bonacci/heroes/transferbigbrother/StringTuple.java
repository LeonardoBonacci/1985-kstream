package guru.bonacci.heroes.transferbigbrother;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringTuple {

  private String tLeft;
  private String tRight;
  
  // take the right
  public StringTuple(StringTuple stLeft, StringTuple stRight) {
    this.tLeft = stRight.tLeft;
    this.tRight = stRight.tRight;
  }
}
