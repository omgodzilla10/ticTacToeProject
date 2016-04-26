import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Driver {
  private static final int ROWS = 3;
  private static final int COLUMNS = 3;
  
  final Board board;
  
  public Driver() {
    board = new Board(COLUMNS, ROWS);
  }
  
  public static void main(final String[] argv) {
    final Frame frame;
    final Driver driver;
    final AIOpponent playerAi;
    final AIOpponent opponentAi;
    
    driver = new Driver();
    
    driver.board.init();
    
    playerAi = new SmarterAI(driver.board.getHeight(), driver.board.getWidth(), BoardSection.Marking.X);
    opponentAi = new SmarterAI(driver.board.getHeight(), driver.board.getWidth(), BoardSection.Marking.O);
    
    driver.board.setOpponentAi(opponentAi);
    driver.board.setPlayerAi(new RandomAI(BoardSection.Marking.X));
    
    frame = new Frame(driver.board);
    frame.setSize(new Dimension(500, 500));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.init();
    
    
    
    //TrainingThread playerThread = driver.new TrainingThread(true);
    TrainingThread aiThread = driver.new TrainingThread(false);
    
    //playerThread.start();
    aiThread.start();
    
    try {
     // playerThread.join();
      aiThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    /*
    SmarterAI smarterAi = driver.board.getSmartestAi();
    smarterAi.setMarking(BoardSection.Marking.O);
    driver.board.setOpponentAi(smarterAi);*/
    
    try{
      
      FileOutputStream fout = new FileOutputStream("smartai.ser");
      ObjectOutputStream oos = new ObjectOutputStream(fout);   
      oos.writeObject(opponentAi);
      oos.close();
         
     }catch(Exception ex){
         ex.printStackTrace();
     }
    
    frame.setVisible(true);
  }
  
  class TrainingThread extends Thread {
    private boolean trainPlayer;
    
    public TrainingThread(boolean trainPlayer) {
      this.trainPlayer = trainPlayer;
    }
    
    @Override
    public void run() {
      for (int i = 0; i < 5; i++) {
        board.startGames(100, 0);
        System.out.println((trainPlayer? board.getPlayerAi().getWins() 
            : board.getOpponentAi().getWins()) + " wins out of 100");
        
        board.getOpponentAi().resetWins();
        board.getPlayerAi().resetWins();
        
        System.out.println("Training " + (trainPlayer? "player" : "opponent"));
        board.trainSmartAi2(trainPlayer, 20, 10);
      }
    }
  }
}
