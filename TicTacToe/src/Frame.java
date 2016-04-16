import java.awt.GridLayout;

import javax.swing.JFrame;

public class Frame extends JFrame {
  Board board;
  
  public Frame(Board board) {
    this.board = board;
  }
  
  public void init() {
    setLayout(new GridLayout(board.getWidth(), board.getHeight()));
    
    for (int row = 0; row < board.getHeight(); row++) {
      for (int col = 0; col < board.getWidth(); col++) {
        //board.getSectionAt(col, row).setSize(getSize().width / board.getWidth(), 
        //    getSize().height / board.getHeight());
        add(board.getSectionAt(col, row));
      }
    }
    
    repaint();
  }
}
