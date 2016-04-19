import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Board {
  private BoardSection[][] boardSections;
  private AIOpponent opponent;
  private AIOpponent randomOpponent;
  
  public Board(int width, int height) {
    boardSections = new BoardSection[width][height];
    randomOpponent = new RandomAI();
  }
  
  public void setOpponent(AIOpponent newOpponent) {
    this.opponent = newOpponent;
  }
  
  public void init() {
    for (int row = 0; row < boardSections.length; row++) {
      for (int col = 0; col < boardSections[0].length; col++) {
        boardSections[col][row] = new BoardSection(col, row, this);
        boardSections[col][row].init();
      }
    }
  }
  
  public void startGame() {
    while (true) {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      opponentTakeTurn();
      checkGameOver();
      
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      randomOpponent.takeTurn(boardSections);
      checkGameOver();
    }
  }
  
  public void opponentTakeTurn() {
    checkGameOver();
    opponent.takeTurn(getAllSections());
    checkGameOver();
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
        TimeUnit.MILLISECONDS.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      resetBoard();
      //((SmarterAI)opponent).randomize();
    }
  }
  
  private boolean isGameOver() {
    boolean gameWon;
    if ((gameWon = isGameWon())) {
      return true;
    }
    
    for (int col = 0; col < boardSections.length; col++) {
      for (int row = 0; row < boardSections[0].length; row++) {
        if (!boardSections[col][row].isUsed()) {
          return false;
        }
      }
    }
    
    return true;
  }
  
  private boolean isGameWon() {
    return (checkHorizontalWins() || checkVerticalWins());
  }
  
  private boolean checkHorizontalWins() {
    int horizontalX;
    int horizontalO;
    
    for (int row = 0; row < boardSections[0].length; row++) {
      horizontalX = 0;
      horizontalO = 0;
      for (int col = 0; col < boardSections.length; col++) {
        if (boardSections[col][row].getMarking() == BoardSection.Marking.X) {
          horizontalX++;
        }
        
        else if (boardSections[col][row].getMarking() == BoardSection.Marking.O) {
          horizontalO++;
        }
      }
      
      if (horizontalX == boardSections.length || horizontalO == boardSections.length) {
        return true;
      }
    }
    
    return false;
  }
  
  private boolean checkVerticalWins() {
    int verticalX;
    int verticalO;
    
    for (int col = 0; col < boardSections.length; col++) {
      verticalX = 0;
      verticalO = 0;
      for (int row = 0; row < boardSections[0].length; row++) {
        if (boardSections[col][row].getMarking() == BoardSection.Marking.X) {
          verticalX++;
        }
        
        else if (boardSections[col][row].getMarking() == BoardSection.Marking.O) {
          verticalO++;
        }
      }
      
      if (verticalX == boardSections[0].length || verticalO == boardSections[0].length) {
        return true;
      }
    }
    
    return false;
  }
  
  public void resetBoard() {
    for (int row = 0; row < boardSections.length; row++) {
      for (int col = 0; col < boardSections[0].length; col++) {
        boardSections[col][row].reset();
      }
    }
  }
}
