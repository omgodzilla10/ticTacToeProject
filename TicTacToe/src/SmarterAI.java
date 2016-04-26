import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SmarterAI implements AIOpponent, Serializable {
  private int wins;
  BoardSection.Marking mark;
  Random rand;
  Node[] inputNodesX;
  Node[] inputNodesO;
  Node[] inputNodesEmpty;
  Node[] hiddenLayer;
  Node[] hiddenLayer2;
  Node[] outputNodes;
  
  public SmarterAI(int height, int width, BoardSection.Marking mark) {
    this.mark = mark;
    rand = new Random();
    
    inputNodesX = new Node[height * width];
    inputNodesO = new Node[height * width];
    inputNodesEmpty = new Node[height * width];
    hiddenLayer = new Node[height * width * 2];
    hiddenLayer2 = new Node[height * width];
    outputNodes = new Node[height * width];
    
    init(height, width);
  }
  
  private void init(int height, int width) {
    for (int i = 0; i < outputNodes.length; i++) {
      outputNodes[i] = new Node();
    }
    
    for (int i = 0; i < hiddenLayer2.length; i++) {
      hiddenLayer2[i] = new Node();
      
      for (int j = 0; j < outputNodes.length; j++) {
        Connection endConnect = new Connection(0f);
        endConnect.addNode(outputNodes[j]);
        
        hiddenLayer2[i].addConnection(endConnect);
      }
    }
    
    for (int i = 0; i < hiddenLayer.length; i++) {
      hiddenLayer[i] = new Node();
    }
    
    for (int i = 0; i < inputNodesX.length && i < inputNodesO.length; i++) {
      inputNodesX[i] = new Node();
      inputNodesO[i] = new Node();
      inputNodesEmpty[i] = new Node();
      
      for (int j = 0; j < hiddenLayer.length; j++) {
        Connection connectO = new Connection(0f);
        Connection connectX = new Connection(0f);
        Connection connectEmp = new Connection(0f);
        
        connectX.addNode(hiddenLayer[j]);
        connectO.addNode(hiddenLayer[j]);
        connectEmp.addNode(hiddenLayer[j]);
        
        inputNodesX[i].addConnection(connectX);
        inputNodesO[i].addConnection(connectO);
        inputNodesEmpty[i].addConnection(connectEmp);
      }
    }
    
    fillInnerConnections();
  }
  
  private void fillInnerConnections() {
    for (int i = 0; i < hiddenLayer.length; i++) {
      for (int j = 0; j < hiddenLayer2.length; j++) {
        Connection newConnect = new Connection(0f);
        newConnect.addNode(hiddenLayer2[j]);
        
        hiddenLayer[i].addConnection(newConnect);
      }
    }
  }
  
  public int getWins() {
    return wins;
  }
  
  public void incrementWins() {
    wins++;
  }
  
  public void resetWins() {
    wins = 0;
  }
  
  @Override
  public void takeTurn(BoardSection[][] sections) {
    int currentNodeIdx = 0;
    int section = 0;
    int endCol;
    int endRow;
    int tempIdx = outputNodes.length;
    
    resetAllNodes();
    
    if (getUnusedSections(sections).size() > 0) {
      for (int col = 0; col < sections.length; col++) {
        for (int row = 0; row < sections.length; row++) {
          if (sections[col][row].getMarking() == BoardSection.Marking.X) {
            inputNodesX[currentNodeIdx].activate(1f);
          } else if (sections[col][row].isUsed()) {
            inputNodesO[currentNodeIdx].activate(1f);
          } else if (!sections[col][row].isUsed()) {
            inputNodesEmpty[currentNodeIdx].activate(1f);
          }
          
          currentNodeIdx++;
        }
      }
      
      do {
        section = 0;
        tempIdx = outputNodes.length;
        
        for (int i = 0; i < outputNodes.length; i++) {
          if (outputNodes[i].getActivation() > section) {
            section = (int) outputNodes[i].getActivation();
            tempIdx = i;
          }
        }
        
        /* Prevents a deadlock where there is an unused 
         * section but the network isn't aware of it. */
        if (tempIdx == outputNodes.length) {
          ArrayList<BoardSection> unusedSections = getUnusedSections(sections);
          Collections.shuffle(unusedSections);
          
          endCol = unusedSections.get(0).getCol();
          endRow = unusedSections.get(0).getRow();
        } else {
          section = ((int)outputNodes[tempIdx].getActivation() % (int)Math.pow(sections.length, 2)); 
          outputNodes[tempIdx].reset();
          
          endCol = section % sections.length;
          endRow = section / sections.length;
        }
      } while (sections[endCol][endRow].isUsed());
      
      sections[endCol][endRow].takeTurn(mark);
    }
  }
  
  private ArrayList<BoardSection> getUnusedSections(BoardSection[][] sections) {
    ArrayList<BoardSection> unusedSections = new ArrayList<BoardSection>();
    
    for (int col = 0; col < sections.length; col++) {
      for (int row = 0; row < sections[0].length; row++) {
        if (!sections[col][row].isUsed()) {
          unusedSections.add(sections[col][row]);
        }
      }
    }
    
    return unusedSections;
  }
  
  public void randomizeAllWeights(float min, float max) {
    for (int i = 0; i < inputNodesX.length; i++) {
      for (Connection connect : inputNodesX[i].connections) {
        connect.weight += (rand.nextFloat() % (max - min)) + min;
      }
    }
    
    for (int i = 0; i < inputNodesO.length; i++) {
      for (Connection connect : inputNodesO[i].connections) {
        connect.weight += (rand.nextFloat() % (max - min)) + min;
      }
    }
    
    for (int i = 0; i < inputNodesEmpty.length; i++) {
      for (Connection connect : inputNodesEmpty[i].connections) {
        connect.weight += (rand.nextFloat() % (max - min)) + min;
      }
    }
    
    for (int i = 0; i < hiddenLayer.length; i++) {
      for (Connection connect : hiddenLayer[i].connections) {
        connect.weight += (rand.nextFloat() % (max - min)) + min;
      }
    }
    
    for (int i = 0; i < hiddenLayer2.length; i++) {
      for (Connection connect : hiddenLayer2[i].connections) {
        connect.weight += (rand.nextFloat() % (max - min)) + min;
      }
    }
  }
  
  private void resetAllNodes() {
    for (int i = 0; i < inputNodesX.length; i++) {
      inputNodesX[i].reset();
      inputNodesO[i].reset();
      inputNodesEmpty[i].reset();
    }
    
    for (int i = 0; i < hiddenLayer.length; i++) {
      hiddenLayer[i].reset();
    }
    
    for (int i = 0; i < hiddenLayer2.length; i++) {
      hiddenLayer2[i].reset();
    }
    
    for (int i = 0; i < outputNodes.length; i++) {
      outputNodes[i].reset();
    }
  }
  
  public String getWeightString() {
    String weights = "";
    float[][][] weightVector = getWeightVector();
    
    for (int i = 0; i < weightVector.length; i++) {
      for (int j = 0; j < weightVector[i].length; j++) {
        for (int k = 0; k < weightVector[i][j].length; k++) {
          weights += (weightVector[i][j][k] + " ");
        }
      }
    }
    
    return weights;
  }
  
  public float[][][] getWeightVector() {
    float[][][] weightVector = new float[6][inputNodesX.length][hiddenLayer.length];
    
    //Go through each InputNodeX
    for (int i = 0; i < inputNodesX.length; i++) {
      weightVector[0][i] = inputNodesX[i].getWeightVector();
    }
    
    //Go through each InputNodeO
    for (int i = 0; i < inputNodesO.length; i++) {
      weightVector[1][i] = inputNodesO[i].getWeightVector();
    }
    
  //Go through each InputNodeEmpty
    for (int i = 0; i < inputNodesEmpty.length; i++) {
      weightVector[2][i] = inputNodesEmpty[i].getWeightVector();
    }
    
    //Go through each hidden node
    for (int i = 0; i < (hiddenLayer.length / 2); i++) {
      weightVector[3][i] = hiddenLayer[i].getWeightVector(); 
      weightVector[4][i] = hiddenLayer[i + (hiddenLayer.length / 2 - 1)].getWeightVector();
    }
    
    for (int i = 0; i < hiddenLayer2.length; i++) {
      weightVector[5][i] = hiddenLayer2[i].getWeightVector();
    }
    
    return weightVector;
  }
  
  public void setWeightVector(float[][][] weightVector) {
    //Go through each InputNodeX
    for (int i = 0; i < inputNodesX.length; i++) {
      inputNodesX[i].setWeightVector(weightVector[0][i]);
    }
    
    //Go through each InputNodeX
    for (int i = 0; i < inputNodesO.length; i++) {
      inputNodesO[i].setWeightVector(weightVector[1][i]);
    }
    
    //Go through each InputNodeEmpty
    for (int i = 0; i < inputNodesEmpty.length; i++) {
      inputNodesEmpty[i].setWeightVector(weightVector[2][i]);
    }
    
    //Go through each hidden node
    for (int i = 0; i < hiddenLayer.length / 2; i++) {
      hiddenLayer[i].setWeightVector(weightVector[3][i]);
      hiddenLayer[i + (hiddenLayer.length / 2 - 1)]
          .setWeightVector(weightVector[4][i]);
    }
    
    //Go through each hidden node2
    for (int i = 0; i < hiddenLayer2.length; i++) {
      hiddenLayer2[i].setWeightVector(weightVector[4][i]);
    }
  }
  
  class Node implements Serializable {
    private static final long serialVersionUID = 1L;
    private float activation;
    private float bias;
    
    protected ArrayList<Connection> connections;
    
    private Node() {
      connections = new ArrayList<Connection>();
    }
    
    public float[] getWeightVector() {
      float[] weightVector = new float[connections.size()];
      int i = 0;
      
      for (Connection connect : connections) {
        weightVector[i++] = connect.weight;
      }
      
      return weightVector;
    }
    
    public void setWeightVector(float[] weightVector) {
      int i = 0;
      
      for (Connection connect : connections) {
        connect.weight = weightVector[i++];
      }
    }

    private Node(float bias) {
      connections = new ArrayList<Connection>();
      
      this.bias = bias;
      activation = bias;
    }
    
    protected void addConnection(Connection newConnection) {
      connections.add(newConnection);
    }
    
    protected void activate(float activation) {
      this.activation += activation;
      
      if (connections.size() > 0) {
        for (Connection connect : connections) {
          connect.activate(activation);
        }
      }
    }
    
    protected float getActivation() {
      return activation;
    }
    
    protected void reset() {
      activation = bias;
    }
  }
  
  class Connection implements Serializable {
    private static final long serialVersionUID = 1L;
    private float weight;
    private ArrayList<Node> forwardNodes;
    
    private Connection(float weight) {
      this.weight = weight;
      forwardNodes = new ArrayList<Node>();
    }
    
    protected void addNode(Node newNode) {
      forwardNodes.add(newNode);
    }
    
    protected void activate(float activation) {
      for (Node node : forwardNodes) {
        node.activate(activation * weight);
      }
    }
  }

  @Override
  public void setMarking(BoardSection.Marking newMark) {
    mark = newMark;
  }
}
