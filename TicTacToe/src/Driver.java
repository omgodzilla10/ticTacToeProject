import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Driver {
  private static final int ROWS = 3;
  private static final int COLUMNS = 3;
  
  public static void main(final String[] argv) {
    final Frame frame;
    final Board board;
    final AIOpponent playerAi;
    final AIOpponent opponentAi;
    
    board = new Board(COLUMNS, ROWS);
    board.init();
    
    playerAi = new SmarterAI(board.getHeight(), board.getWidth(), BoardSection.Marking.X);
    opponentAi = new SmarterAI(board.getHeight(), board.getWidth(), BoardSection.Marking.O);
    
    board.setOpponentAI(opponentAi);
    board.setPlayerAI(playerAi);
    
    frame = new Frame(board);
    frame.setSize(new Dimension(500, 500));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.init();
    
    
    for (int i = 0; i < 10; i++) {
      System.out.println("Player AI");
      board.trainSmartAi(true, 100, 20);
      
      System.out.println("Opponent AI");
      board.trainSmartAi(false, 100, 20);
      
      if (i % 10 == 0) {
        System.out.println("\t\tIteration: " + i);
      }
    }
    
    frame.setVisible(true);
  }
}
