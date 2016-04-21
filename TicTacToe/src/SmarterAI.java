import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class SmarterAI implements AIOpponent, Serializable {
  private int wins;
  BoardSection.Marking mark;
  Random rand;
  Node[] inputNodesX;
  Node[] inputNodesO;
  Node[] hiddenLayer;
  Node[] hiddenLayer2;
  Node outputNode;
  
  public SmarterAI(int height, int width, BoardSection.Marking mark) {
    this.mark = mark;
    rand = new Random();
    
    inputNodesX = new Node[height * width];
    inputNodesO = new Node[height * width];
    hiddenLayer = new Node[height * width];
    hiddenLayer2 = new Node[height + width];
    outputNode = new Node();
    
    init(height, width);
  }
  
  private void init(int height, int width) {
    for (int i = 0; i < width * height; i++) {
      hiddenLayer[i] = new Node();
    }
    
    for (int i = 0; i < hiddenLayer2.length; i++) {
      Connection endConnect = new Connection(0f);
      hiddenLayer2[i] = new Node();
      
      endConnect.addNode(outputNode);
      hiddenLayer2[i].addConnection(endConnect);
    }
    
    for (int i = 0; i < width * height; i++) {
      inputNodesX[i] = new Node();
      inputNodesO[i] = new Node();
      
      for (int j = 0; j < hiddenLayer.length; j++) {
        Connection xConnect = new Connection(0f);
        Connection yConnect = new Connection(0f);
        
        xConnect.addNode(hiddenLayer[j]);
        yConnect.addNode(hiddenLayer[j]);
        
        inputNodesX[i].addConnection(xConnect);
        inputNodesO[i].addConnection(yConnect);
      }

      fillInnerConnections(i);
    }
    
    randomize();
  }
  
  private void fillInnerConnections(int hiddenIdx) {
    if (hiddenIdx % 3 == 0) {
      Connection innerConnect = new Connection(0f);
      
      innerConnect.addNode(hiddenLayer2[0]);
      hiddenLayer[hiddenIdx].addConnection(innerConnect);
    }
    
    if (hiddenIdx % 3 + 1 == 0) {
      Connection innerConnect = new Connection(0f);
      
      innerConnect.addNode(hiddenLayer2[1]);
      hiddenLayer[hiddenIdx].addConnection(innerConnect);
    }
    
    if (hiddenIdx % 3 + 2 == 0) {
      Connection innerConnect = new Connection(0f);
      
      innerConnect.addNode(hiddenLayer2[2]);
      hiddenLayer[hiddenIdx].addConnection(innerConnect);
    }
    
    if (hiddenIdx < 3) {
      Connection innerConnect = new Connection(0f);
      
      innerConnect.addNode(hiddenLayer2[4]);
      hiddenLayer[hiddenIdx].addConnection(innerConnect);
    } 
    
    else if (hiddenIdx < 6) {
      Connection innerConnect = new Connection(0f);
      
      innerConnect.addNode(hiddenLayer2[5]);
      hiddenLayer[hiddenIdx].addConnection(innerConnect);
    }
    
    else if (hiddenIdx < 9) {
      Connection innerConnect = new Connection(0f);
      
      innerConnect.addNode(hiddenLayer2[5]);
      hiddenLayer[hiddenIdx].addConnection(innerConnect);
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
    ArrayList<BoardSection> unusedSections;
    int currentNodeIdx = 0;
    int section;
    int endCol;
    int endRow;
    
    resetAllNodes();
    
    for (int col = 0; col < sections.length; col++) {
      for (int row = 0; row < sections.length; row++) {
        if (sections[col][row].getMarking() == mark) {
          inputNodesO[currentNodeIdx].activate(1f);
        } else if (sections[col][row].isUsed()) {
          inputNodesX[currentNodeIdx].activate(-1f);
        }
        
        currentNodeIdx++;
      }
    }
    
    unusedSections = getUnusedSections(sections);
    if (unusedSections.size() > 0) {
      section = ((int)outputNode.getActivation()) % (sections.length * sections[0].length);
      
      endCol = Math.abs(section / sections.length);
      endRow = Math.abs(section % sections[0].length);
      while (sections[endCol][endRow].isUsed()) {
        endCol = Math.abs(rand.nextInt() % sections.length);
        endRow = Math.abs(rand.nextInt() % sections[0].length);
      }
      
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
  
  public void randomize() {
    randomizeAllWeights(0f, 4f);
  }
  
  public void randomizeAllWeights(float min, float max) {
    for (int i = 0; i < inputNodesX.length; i++) {
      for (Connection connect : inputNodesX[i].connections) {
        connect.weight += (rand.nextFloat() % (max - min + 1)) + min;
      }
      
      for (Connection connect : inputNodesO[i].connections) {
        connect.weight += (rand.nextFloat() % (max - min + 1)) + min;
      }
      
      for (Connection connect : hiddenLayer[i].connections) {
        connect.weight += (rand.nextFloat() % (max - min + 1)) + min;
      }
      
      if (i < hiddenLayer2.length) {
        for (Connection connect : hiddenLayer2[i].connections) {
          connect.weight += (rand.nextFloat() % (max - min + 1)) + min;
        }
      }
    }
  }
  
  private void resetAllNodes() {
    for (int i = 0; i < inputNodesX.length; i++) {
      inputNodesX[i].reset();
      inputNodesO[i].reset();
      hiddenLayer[i].reset();
      
      if (i < hiddenLayer2.length) {
        hiddenLayer2[i].reset();
      }
    }
    
    outputNode.reset();
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
    float[][][] weightVector = new float[4][inputNodesX.length][inputNodesX.length];
    
    //Go through each InputNodeX
    for (int i = 0; i < inputNodesX.length; i++) {
      weightVector[0][i] = inputNodesX[i].getWeightVector();
    }
    
    //Go through each InputNodeO
    for (int i = 0; i < inputNodesO.length; i++) {
      weightVector[1][i] = inputNodesO[i].getWeightVector();
    }
    
    //Go through each hidden node
    for (int i = 0; i < hiddenLayer.length; i++) {
      weightVector[2][i] = hiddenLayer[i].getWeightVector(); 
    }
    
    for (int i = 0; i < hiddenLayer2.length; i++) {
      weightVector[3][i] = hiddenLayer2[i].getWeightVector();
    }
    
    return weightVector;
  }
  
  public void setWeightVector(float[][][] weightVector) {
    //Go through each InputNodeX
    for (int i = 0; i < inputNodesX.length; i++) {
      inputNodesX[i].setWeightVector(weightVector[0][i]);
    }
    
    //Go through each InputNodeO
    for (int i = 0; i < inputNodesO.length; i++) {
      inputNodesO[i].setWeightVector(weightVector[1][i]);
    }
    
    //Go through each hidden node
    for (int i = 0; i < hiddenLayer.length; i++) {
      hiddenLayer[i].setWeightVector(weightVector[2][i]);
    }
    
    //Go through each hidden node2
    for (int i = 0; i < hiddenLayer2.length; i++) {
      hiddenLayer2[i].setWeightVector(weightVector[3][i]);
    }
  }
  
  class Node {
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
  
  class Connection {
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

  
}
