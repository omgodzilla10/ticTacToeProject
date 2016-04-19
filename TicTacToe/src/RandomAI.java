import java.util.ArrayList;
import java.util.Collections;

public class RandomAI implements AIOpponent {

  @Override
  public void takeTurn(BoardSection[][] sections) {
    ArrayList<BoardSection> unusedSections = new ArrayList<BoardSection>();
    
    for (int row = 0; row < sections.length; row++) {
      for (int col = 0; col < sections[0].length; col++) {
        if (!sections[row][col].isUsed()) {
          unusedSections.add(sections[row][col]);
        }
      }
    }
    
    if (unusedSections.size() > 0) {
      Collections.shuffle(unusedSections);
      unusedSections.get(0).takeTurn(true);
    }
  }
}
