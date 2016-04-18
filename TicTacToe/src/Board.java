import java.util.ArrayList;

public class Board {
  private BoardSection[][] boardSections;
  private AIOpponent opponent;
  
  public Board(int width, int height, AIOpponent opponent) {
    boardSections = new BoardSection[width][height];
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
}
