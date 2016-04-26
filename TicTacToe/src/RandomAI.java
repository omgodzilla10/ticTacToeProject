import java.util.ArrayList;
import java.util.Collections;

public class RandomAI implements AIOpponent {
  private int wins;
  private BoardSection.Marking mark;
  
  public RandomAI(BoardSection.Marking mark) {
    this.mark = mark;
  }

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
      unusedSections.get(0).takeTurn(mark);
    }
  }

  @Override
  public int getWins() {
    return wins;
  }

  @Override
  public void incrementWins() {
    wins++;
  }

  @Override
  public void resetWins() {
    wins = 0;
  }

  @Override
  public void setMarking(BoardSection.Marking newMark) {
    mark = newMark;
  }
}
