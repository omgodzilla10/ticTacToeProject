import java.util.ArrayList;

public class SmartAI implements AIOpponent {
  private int numNodes;
  
  public SmartAI(int width, int height) {
    numNodes = width * height;
    Node[] inputNodes = new Node[numNodes];
    Node[] outputNodes = new Node[numNodes];
    
    for (int i = 0; i < numNodes; i++) {
      inputNodes[i] = new Node();
      outputNodes[i] = new Node();
    }
    
    for (int i = 0; i < numNodes; i++) {
      for (int j = 0; j < numNodes; j++) {
        inputNodes[i].addConnection(new Connection(0));
      }
    }
  }
  
  @Override
  public void takeTurn(BoardSection[][] sections) {
    
  }
  
  class Node {
    private float activation;
    private ArrayList<Connection> connections;
    
    private Node() {
      connections = new ArrayList<Connection>();
    }
    
    protected void addConnection(Connection newConnection) {
      connections.add(newConnection);
    }
    
    protected void activate(float activation) {
      this.activation += activation;
      
      for (Connection connect : connections) {
        connect.activate(activation);
      }
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
