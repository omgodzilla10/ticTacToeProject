import java.util.ArrayList;
import java.util.Random;

public class SmarterAI implements AIOpponent {
  BoardSection.Marking mark;
  Random rand;
  Node[] inputNodesX;
  Node[] inputNodesO;
  Node[] hiddenLayer;
  Node outputNode;
  
  public SmarterAI(int height, int width, BoardSection.Marking mark) {
    this.mark = mark;
    rand = new Random();
    
    inputNodesX = new Node[height * width];
    inputNodesO = new Node[height * width];
    hiddenLayer = new Node[height * width];
    outputNode = new Node();
    
    init(height, width);
  }
  
  private void init(int height, int width) {
    for (int i = 0; i < width * height; i++) {
      hiddenLayer[i] = new Node();
    }
    
    for (int i = 0; i < width * height; i++) {
      inputNodesX[i] = new Node();
      inputNodesO[i] = new Node();
      Connection endConnect = new Connection(0f);
      
      for (int j = 0; j < hiddenLayer.length; j++) {
        Connection xConnect = new Connection(0f);
        Connection yConnect = new Connection(0f);
        
        xConnect.addNode(hiddenLayer[j]);
        yConnect.addNode(hiddenLayer[j]);
        
        inputNodesX[i].addConnection(xConnect);
        inputNodesO[i].addConnection(yConnect);
      }
      
      endConnect.addNode(outputNode);
      hiddenLayer[i].addConnection(endConnect);
    }
    
    randomize();
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
          inputNodesX[currentNodeIdx].activate(1f);
        }
        
        currentNodeIdx++;
      }
    }
    
    unusedSections = getUnusedSections(sections);
    if (unusedSections.size() > 0) {
      section = ((int)outputNode.getActivation()) % (sections.length * sections[0].length);
      
      endCol = section / sections.length;
      endRow = section % sections[0].length;
      while (sections[endCol][endRow].isUsed()) {
        endCol = Math.abs(rand.nextInt()) % sections.length;
        endRow = Math.abs(rand.nextInt()) % sections[0].length;
      }
      
      sections[endCol][endRow].takeTurn(false);
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
    randomizeAllWeights(-5f, 5f);
  }
  
  private void randomizeAllWeights(float min, float max) {
    for (int i = 0; i < inputNodesX.length; i++) {
      for (Connection connect : inputNodesX[i].connections) {
        connect.weight = (rand.nextFloat() % (max - min + 1)) + min;
      }
      
      for (Connection connect : inputNodesO[i].connections) {
        connect.weight = (rand.nextFloat() % (max - min + 1)) + min;
      }
      
      for (Connection connect : hiddenLayer[i].connections) {
        connect.weight = (rand.nextFloat() % (max - min + 1)) + min;
      }
    }
  }
  
  private void resetAllNodes() {
    for (int i = 0; i < inputNodesX.length; i++) {
      inputNodesX[i].reset();
      inputNodesO[i].reset();
      hiddenLayer[i].reset();
    }
    
    outputNode.reset();
  }
  
  class Node {
    private float activation;
    private float bias;
    
    protected ArrayList<Connection> connections;
    
    private Node() {
      connections = new ArrayList<Connection>();
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
