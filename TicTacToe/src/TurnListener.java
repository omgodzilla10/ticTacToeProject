import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TurnListener extends MouseAdapter {
  private static int turnsMade = 0;
  private BoardSection section;
  
  public TurnListener(BoardSection section) {
    this.section = section;
  }
  
  @Override
  public void mousePressed(MouseEvent e) {
    takeTurn();
    section.getBoard().opponentTakeTurn();
  }
  
  public void takeTurn() {
    if (!section.isUsed()) {
      turnsMade++;
      section.takeTurn(BoardSection.Marking.X);
    }
    
    section.getBoard().checkGameOver();
  }
}
