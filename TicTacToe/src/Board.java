import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Board {
  private BoardSection[][] boardSections;
  private AIOpponent aiOpponent;
  private AIOpponent aiPlayer;
  
  public Board(int width, int height) {
    boardSections = new BoardSection[width][height];
  }
  
  public void setOpponentAi(AIOpponent newOpponent) {
    aiOpponent = newOpponent;
  }
  
  public void setPlayerAi(AIOpponent newPlayer) {
    aiPlayer = newPlayer;
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
      for (int i = 0; i < 4; i++) {
        new Thread() {
          public void run() {
            for (int j = 0; j < numToPlay / 4; j++) {
              playAiGame(delay);
            }
          }
        }.run();
      }
    }
  }
  
  public synchronized void playAiGame(long delay) {
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
  
  public synchronized void trainSmartAi(boolean trainPlayer, int iterationsPerTrain, int timesToTrain) {
    SmarterAI aiToTrain;
    float[][][] weightVector;
    int bestScore = 0;
    int score;
    float randExpansion = 1f;
    
    if (trainPlayer) {
      aiToTrain = (SmarterAI)aiPlayer;
    } else {
      aiToTrain = (SmarterAI)aiOpponent;
    }
    
    weightVector = aiToTrain.getWeightVector();
    
    for (int i = 0; i < timesToTrain; i++) {
      startGames(iterationsPerTrain, 0);
      
      score = aiToTrain.getWins() + (iterationsPerTrain - aiOpponent.getWins() 
          - aiPlayer.getWins());
      
      // If the AI beats it's best score.
      if (score > bestScore) {
        bestScore = score;
        weightVector = aiToTrain.getWeightVector();
        randExpansion = 1f;
        
        System.out.println("New best! (" + score + ", " + i + ")");
      } else {
        aiToTrain.setWeightVector(weightVector);
        randExpansion *= 1.05f;
      }
      
      aiPlayer.resetWins();
      aiOpponent.resetWins();
      
      aiToTrain.randomizeAllWeights(-randExpansion, randExpansion);
    }
  }
  
  public synchronized void trainSmartAi2(boolean trainPlayer, int iterationsPerTrain, int timesToTrain) {
    int score;
    int bestScore = 0;
    float minRand = -2f;
    float maxRand = 2f;
    SmarterAI aiToTrain;
    float[][][] tempWeightVector;
    float[][][] weightVector;
    
    if (trainPlayer) {
      aiToTrain = (SmarterAI)aiPlayer;
    } else {
      aiToTrain = (SmarterAI)aiOpponent;
    }
    
    weightVector = aiToTrain.getWeightVector();
    
    for (int dim1 = 0; dim1 < weightVector.length; dim1++) {
      for (int dim2 = 0; dim2 < weightVector[dim1].length; dim2++) {
        for (int dim3 = 0; dim3 < weightVector[dim1][dim2].length; dim3++) {
          System.out.println("Training " + dim1 + ", " + dim2 + ", " + dim3);
          for (int i = 0; i < timesToTrain; i++) {
            tempWeightVector = weightVector;
            tempWeightVector[dim1][dim2][dim3] += ((Math.random() % (maxRand - minRand)) + minRand);
            
            aiToTrain.setWeightVector(tempWeightVector);
            startGames(iterationsPerTrain, 0);
            score = aiToTrain.getWins();
            
            if (score > bestScore) {
              bestScore = score;
              weightVector = tempWeightVector;
            }
            
            aiPlayer.resetWins();
            aiOpponent.resetWins();
          }
          
          aiToTrain.setWeightVector(weightVector);
          bestScore = 0;
        }
      }
    }
  }
  
  public SmarterAI getSmartestAi() {
    aiPlayer.resetWins();
    aiOpponent.resetWins();
    
    playAiGame(100);
    
    if (aiPlayer.getWins() > aiOpponent.getWins()) {
      return (SmarterAI)aiPlayer;
    } else {
      return (SmarterAI)aiOpponent;
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
    if (isGameWon()) {
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
    return (checkHorizontalWins() || checkVerticalWins() || checkRightDiagonalWins()
        || checkLeftDiagonalWins());
  }
  
  private boolean checkRightDiagonalWins() {
    int diagonalO = 0;
    int diagonalX = 0;
    
    for (int col = 0, row = 0; col < getWidth() && row < getHeight(); row++, col++) {
      if (boardSections[col][row].getMarking() == BoardSection.Marking.X) {
        diagonalX++;
      } else if (boardSections[col][row].getMarking() == BoardSection.Marking.O) {
        diagonalO++;
      }
    }
    
    if (diagonalX == getHeight()) {
      if (aiPlayer != null) {
        aiPlayer.incrementWins();
      }
      
      return true;
    }
    
    if (diagonalO == getHeight()) {
      if (aiOpponent != null) {
        aiOpponent.incrementWins();
      }
      
      return true;
    }
    
    return false;
  }
  
  private boolean checkLeftDiagonalWins() {
    int diagonalO = 0;
    int diagonalX = 0;
    
    for (int col = getWidth() - 1, row = 0; col >= 0 && row < getHeight(); row++, col--) {
      if (boardSections[col][row].getMarking() == BoardSection.Marking.X) {
        diagonalX++;
      } else if (boardSections[col][row].getMarking() == BoardSection.Marking.O) {
        diagonalO++;
      }
    }
    
    if (diagonalX == getHeight()) {
      if (aiPlayer != null) {
        aiPlayer.incrementWins();
      }
      
      return true;
    }
    
    if (diagonalO == getHeight()) {
      if (aiOpponent != null) {
        aiOpponent.incrementWins();
      }
      
      return true;
    }
    
    return false;
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
