import java.awt.Dimension;

import javax.swing.JFrame;

public class Driver {
  private static final int ROWS = 3;
  private static final int COLUMNS = 3;
  
  public static void main(final String[] argv) {
    final Frame frame;
    final Board board;
    final RandomAI randomAI;
    
    randomAI = new RandomAI();
    
    board = new Board(COLUMNS, ROWS, randomAI);
    board.init();
    
    frame = new Frame(board);
    frame.setSize(new Dimension(500, 500));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.init();
    frame.setVisible(true);
  }
}
