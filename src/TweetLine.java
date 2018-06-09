import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TweetLine extends JLabel implements MouseListener {
  private static final long serialVersionUID = -844523707922135233L;
  private static int speed;
  public int x;
  public int y;
  private int width;
  private int height;
  private int length;
  private boolean isFirstRefered = true;
  private boolean isDead = false;
  private boolean isClicked = false;
  private boolean isCtrlClicked = false;
  private boolean isFavoured = false;
  private boolean isRetweeted = false;
  private long id;

  TweetLine(String content) {
    super(content);
    this.length = content.length();
    addMouseListener(this);
  }

  public static void setSpeed(int speedNew) {
    speed = speedNew;
  }

  public void setFontsize(int fontsize) {
    this.width = (fontsize * this.length);
    this.height = (int)(fontsize*1.2);
  }

  public boolean isFirst() {
    if (this.isFirstRefered) {
      this.isFirstRefered = false;
      return true;
    }
    return false;
  }

  private int updateX() {
    this.x -= speed;
    if (this.x < -1 * this.length * this.height)
      this.isDead = true;
    return this.x;
  }

  public void updatePosition() {
    setBounds(updateX(), y, width, height);
  }

  public boolean isDead() {
    return this.isDead;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getId() {
    return this.id;
  }

  public boolean isClicked() {
    return this.isClicked;
  }
  
  public boolean isCtrlClicked() {
    return this.isCtrlClicked;
  }

  public void favour() {
    this.isFavoured = true;
  }

  public boolean isFavoured() {
    return this.isFavoured;
  }
  
  public void retweet() {
    this.isRetweeted = true;
  }

  public boolean isRetweeted() {
    return this.isRetweeted;
  }

  public void mouseClicked(MouseEvent e)
  {
    if (e.isControlDown()) {
      this.isCtrlClicked = true;
    } else {
      this.isClicked = true;
    }
  }

  public void mouseEntered(MouseEvent e)
  {
    if (e.isControlDown()) {
      this.isCtrlClicked = true;
    } else {
      this.isClicked = true;
    }
  }

  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e) {}

  public void mouseReleased(MouseEvent e) {}
}
