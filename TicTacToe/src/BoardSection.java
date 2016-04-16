import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class BoardSection extends JPanel {
  private boolean isUsed;
  private Board board;
  private TurnListener turnListener;
  
  public BoardSection(int col, int row, Board board) {
    this.board = board;
    isUsed = false;
    turnListener = new TurnListener(this);
  }
  
  public void init() {
    setColor(Color.WHITE);
    setBorder(BorderFactory.createLineBorder(Color.BLACK));
    
    addMouseListener(turnListener);
  }
  
  public void takeTurn(boolean playerTurn) {
    setUsed(true);
    setColor(playerTurn? Color.BLUE : Color.RED);
  }
  
  public TurnListener getTurnListener() {
    return turnListener;
  }
  
  public boolean isUsed() {
    return isUsed;
  }
  
  public void setUsed(boolean isUsed) {
    this.isUsed = isUsed;
  }
  
  public void setColor(Color newColor) {
    setBackground(newColor);
  }
  
  public Board getBoard() {
    return board;
  }
}
