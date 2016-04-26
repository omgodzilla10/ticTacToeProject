import java.util.ArrayList;

public interface AIOpponent {
  public void takeTurn(BoardSection[][] sections);
  public int getWins();
  public void incrementWins();
  public void resetWins();
  public void setMarking(BoardSection.Marking newMark);
}
