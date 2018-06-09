import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class TweetFlame
  extends JFrame
{
  private static final long serialVersionUID = -6557150527228697556L;
  private static String name = "Post a status...";
  private JTextField statusForm;
  
  TweetFlame() {
    super(name);
    setAlwaysOnTop(true);
    setSize(260, 90);
    setDefaultCloseOperation(2);
    setLocationRelativeTo(null);
    setVisible(true);
    
    addKeyListener(new KeyListener()
    {
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'A') {
          TweetFlame.this.setVisible(false);
        }
      }
      
      public void keyPressed(KeyEvent e) {}
      
      public void keyReleased(KeyEvent e) {}
    });
    this.statusForm = new JTextField();
    add(this.statusForm);
    this.statusForm.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        TweetFlame.this.postStatus();
      }
    });
  }
  
  private void postStatus() {
    String status = this.statusForm.getText();
    if (status.length() > 0) {
      TwitterUtl tu = new TwitterUtl();
      tu.post(status);
    }
    setVisible(false);
  }
}