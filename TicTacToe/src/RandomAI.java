import java.util.ArrayList;
import java.util.Collections;

public class RandomAI implements AIOpponent {

  @Override
  public void takeTurn(ArrayList<BoardSection> unusedSections) {
    if (unusedSections.size() > 0) {
      Collections.shuffle(unusedSections);
      unusedSections.get(0).getTurnListener().takeTurn(false);
    }
  }
}
