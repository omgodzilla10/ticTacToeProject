import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Board {
  private BoardSection[][] boardSections;
  private AIOpponent opponent;
  
  public Board(int width, int height) {
    boardSections = new BoardSection[width][height];
  }
  
  public void setOpponent(AIOpponent opponent) {
    this.opponent = opponent;
  }
  
  public void init() {
    for (int row = 0; row < boardSections.length; row++) {
      for (int col = 0; col < boardSections[0].length; col++) {
        boardSections[col][row] = new BoardSection(col, row, this);
        boardSections[col][row].init();
      }
    }
  }
  
  public void opponentTakeTurn() {
    opponent.takeTurn(getAllSections());
  }
  
  public BoardSection[][] getAllSections() {
    return boardSections;
  }
  
  public BoardSection getSectionAt(int col, int row) {
    return boardSections[col][row];
  }
  
  public int getWidth() {
    return boardSections.length;
  }
  
  public int getHeight() {
    return boardSections[0].length;
  }
  
  public void checkGameOver() {
    if (isGameOver()) {
      try {
        TimeUnit.MILLISECONDS.sleep(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      resetBoard();
      ((SmarterAI)opponent).randomize();
    }
  }
  
  private boolean isGameOver() {
    for (int col = 0; col < boardSections.length; col++) {
      for (int row = 0; row < boardSections[0].length; row++) {
        if (!boardSections[col][row].isUsed()) {
          return false;
        }
      }
    }
    
    return true;
  }
  
  public void resetBoard() {
    for (int row = 0; row < boardSections.length; row++) {
      for (int col = 0; col < boardSections[0].length; col++) {
        boardSections[col][row].reset();
      }
    }
  }
}
