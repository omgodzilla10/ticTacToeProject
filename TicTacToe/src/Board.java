import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Board {
  private BoardSection[][] boardSections;
  private AIOpponent aiOpponent;
  private AIOpponent aiPlayer;
  
  public Board(int width, int height) {
    boardSections = new BoardSection[width][height];
  }
  
  public void setOpponentAI(AIOpponent newOpponent) {
    this.aiOpponent = newOpponent;
  }
  
  public void setPlayerAI(AIOpponent newPlayer) {
    this.aiPlayer = newPlayer;
  }
  
  public void init() {
    for (int row = 0; row < boardSections.length; row++) {
      for (int col = 0; col < boardSections[0].length; col++) {
        boardSections[col][row] = new BoardSection(col, row, this);
        boardSections[col][row].init();
      }
    }
  }
  
  public void startGames(int numToPlay, long delay) {
    if (aiPlayer != null && aiOpponent != null) {
      for (int i = 0; i < numToPlay; i++) {
        playAiGame(delay);
      }
    }
  }
  
  public void playAiGame(long delay) {
    boolean gameFinished = false;
    
    while (!gameFinished) {
      try {
        TimeUnit.MILLISECONDS.sleep(delay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      aiPlayer.takeTurn(boardSections);
      
      gameFinished = isGameOver();
      if (!gameFinished) {
        try {
          TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        
        aiOpponent.takeTurn(boardSections);
        
        gameFinished = isGameOver();
      }
    }
    
    try {
      TimeUnit.MILLISECONDS.sleep(delay);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    resetBoard();
  }
  
  public void opponentTakeTurn() {
    aiOpponent.takeTurn(boardSections);
  }
  
  public AIOpponent getPlayerAi() {
    return aiPlayer;
  }
  
  public AIOpponent getOpponentAi() {
    return aiOpponent;
  }
  
  public void trainSmartAi(boolean trainPlayer, int iterationsPerTrain, int timesToTrain) {
    SmarterAI aiToTrain;
    float[][][] weightVector = ((SmarterAI)aiPlayer).getWeightVector();
    int totalGames;
    float bestPercWins = 0;
    float percWins;
    
    if (trainPlayer) {
      aiToTrain = (SmarterAI)aiPlayer;
    } else {
      aiToTrain = (SmarterAI)aiOpponent;
    }
    
    for (int i = 0; i < timesToTrain; i++) {
      aiToTrain.randomizeAllWeights(-0.8f, 0.8f);
      startGames(iterationsPerTrain, 0);
      
      totalGames = aiPlayer.getWins() + aiOpponent.getWins();
      percWins = (float)aiPlayer.getWins() / (float)totalGames * 100;
      
      // If the AI beats it's best score.
      if (percWins > bestPercWins) {
        bestPercWins = percWins;
        weightVector = ((SmarterAI)aiPlayer).getWeightVector();
        System.out.println("New best! (" + percWins + ", " + i + ")");
        
        if ((int)percWins == 100) {
          break;
        }
      } else {
        // If the AI did not improve upon it's high score, roll back to best vectors.
        ((SmarterAI)aiPlayer).setWeightVector(weightVector);
      }
      
      aiPlayer.resetWins();
      aiOpponent.resetWins();
    }
    
    if (trainPlayer) {
      aiPlayer = aiToTrain;
    } else {
      aiOpponent = aiToTrain;
    }
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
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      resetBoard();
    }
  }
  
  private boolean isGameOver() {
    boolean gameWon;
    if ((gameWon = isGameWon())) {
      return true;
    }
    
    // Check for a stalemate.
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
      
      if (horizontalX == boardSections.length) {
        if (aiPlayer != null) {
          aiPlayer.incrementWins();
        }
        
        return true;
      }
          
      else if (horizontalO == boardSections.length) {
        if (aiOpponent != null) {
          aiOpponent.incrementWins();
        }
        
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
      
      if (verticalX == boardSections[0].length) {
        if (aiPlayer != null) {
          aiPlayer.incrementWins();
        }
        
        return true;
      }
          
      else if (verticalO == boardSections[0].length) {
        if (aiOpponent != null) {
          aiOpponent.incrementWins();
        }
        
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
