import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class BoardSection extends JPanel {
  private static final long serialVersionUID = 1L;

  enum Marking {
    Empty,
    X,
    O;
  }
  
  private Marking currentMarking;
  private Board board;
  private TurnListener turnListener;
  
  public BoardSection(int col, int row, Board board) {
    this.board = board;
    currentMarking = Marking.Empty;
    turnListener = new TurnListener(this);
  }
  
  public void init() {
    setColor(Color.WHITE);
    setBorder(BorderFactory.createLineBorder(Color.BLACK));
    
    addMouseListener(turnListener);
  }
  
  public void takeTurn(boolean playerTurn) {
    setMarking(playerTurn? Marking.X : Marking.O);
  }
  
  public TurnListener getTurnListener() {
    return turnListener;
  }
  
  public Marking getMarking() {
    return currentMarking;
  }
  
  public void setMarking(Marking newMarking) {
    currentMarking = newMarking;
    
    switch (currentMarking) {
      case Empty: setColor(Color.WHITE);
        break;
      case X: setColor(Color.BLUE);
        break;
      case O: setColor(Color.RED);
        break;
      default: break;
    }
  }
  
  public void reset() {
    setMarking(Marking.Empty);
    setColor(Color.WHITE);
  }
  
  public boolean isUsed() {
    return (getMarking() == Marking.X || getMarking() == Marking.O);
  }
  
  public void setColor(Color newColor) {
    setBackground(newColor);
    repaint();
  }
  
  public Board getBoard() {
    return board;
  }
}
