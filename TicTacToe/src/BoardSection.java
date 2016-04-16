import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.border.Border;

public class BoardSection extends JPanel {
  private Point location;
  
  public BoardSection(int col, int row) {
    location = new Point(col, row);
  }
  
  public void init() {
    setColor(Color.WHITE);
    setBorder(new Border());
    
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        
      }
    });
  }
  
  public void setColor(Color newColor) {
    setBackground(newColor);
  }
}
