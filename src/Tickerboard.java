import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.Timer;

public class Tickerboard extends JFrame implements java.awt.event.ActionListener
{
  private static final long serialVersionUID = 3673853824050798273L;
  private static int FONTSIZE;
  private static int MAX_COMMENT;
  private static int FPS = 10;
  private static Color foreDefault = new Color(21, 21, 21, 200);
  private static Color foreFavored = new Color(255, 172, 51, 200);
  private static Color foreRetweeted = new Color(92, 145, 59, 220);
  
  private static TwitterListen listener;
  private static TweetFlame tf;
  private Timer timer;
  private TweetLine[] tweets = new TweetLine[MAX_COMMENT];
  private int lineNum = 0;
  private Font font = new Font("SansSerif", 1, FONTSIZE);
  

  public static void main(String[] args)
  {
    if (OAuther.retrieveAccessToken() == null) {
      try {
        OAuther.setAccessToken();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    Property.readProperties();
    configInit();
    
    listener = new TwitterListen(args);
    TwitterUtl.authTwitter();
    
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        String title = "TwiJK - monitoring: ";
        for (int i = 0; i < args.length; i++) {
          title = title + args[i] + " ";
        }
        title = title + "; logged in as @" + OAuther.getUserName();
        System.out.println(title);
        Tickerboard t = new Tickerboard(title);
        t.setUndecorated(true);
        t.setAlwaysOnTop(true);
        t.setBackground(new Color(0, 0, 0, 0));
        t.setLayout(null);
        t.setExtendedState(6);
        t.setVisible(true);
        
        t.addKeyListener(new java.awt.event.KeyListener()
        {
          public void keyTyped(KeyEvent e)
          {
            if (e.getKeyChar() == 'A') {
              Tickerboard.tf = new TweetFlame();
            }
          }
          public void keyPressed(KeyEvent e) {}

          public void keyReleased(KeyEvent e) {}
        });
      }
    });
  }
  

  private static void configInit()
  {
    FONTSIZE = Property.getPropertyInt("FONTSIZE");
    MAX_COMMENT = Property.getPropertyInt("MAX_NUM");
    FPS = Property.getPropertyInt("FPS");
    TweetLine.setSpeed(Property.getPropertyInt("speed"));
  }

  private Tickerboard(String title) {
    super(title);
    
    setSize(400, 400);
    setDefaultCloseOperation(3);
    
    this.timer = new Timer(1000 / FPS, this);
    this.timer.start();
    
    listener.start();
  }

  public void actionPerformed(ActionEvent e) {
    int bufNum = listener.availableNum();
    if (bufNum > 0) {
      TweetLine[] tempbuf = new TweetLine[bufNum];
      tempbuf = listener.getBuffer();
      for (int i = 0; i < bufNum; i++) {
        if (this.lineNum < MAX_COMMENT) {
          this.tweets[this.lineNum] = tempbuf[i];
          this.lineNum += 1;
        }
      }
    }
    
    for (int i = 0; i < this.lineNum; i++) {
      if (this.tweets[i].isFirst()) {
        this.tweets[i].x = getWidth();
        this.tweets[i].y = ((int)(Math.random() * (getHeight() - FONTSIZE)));
        this.tweets[i].setFontsize(FONTSIZE);
        this.tweets[i].setFont(this.font);
        this.tweets[i].setForeground(foreDefault);
        add(this.tweets[i]);
      }
      
      if ((this.tweets[i].isClicked()) && (!this.tweets[i].isFavoured())) {
        TwitterUtl tu = new TwitterUtl();
        tu.favorite(this.tweets[i].getId());
        
        this.tweets[i].favour();
        this.tweets[i].setForeground(foreFavored);
      }
      
      if ((this.tweets[i].isCtrlClicked()) && (!this.tweets[i].isRetweeted())) {
        TwitterUtl tu = new TwitterUtl();
        tu.retweet(this.tweets[i].getId());
        
        this.tweets[i].retweet();
        this.tweets[i].setForeground(foreRetweeted);
      }
      
      if (this.tweets[i].isDead()) {
        remove(this.tweets[i]);
        this.tweets[i] = null;
        this.lineNum -= 1;
        this.tweets = shiftArray(this.tweets, i, this.lineNum);
      }
      else
      {
        this.tweets[i].updatePosition();
      }
    }
  }
  
  private TweetLine[] shiftArray(TweetLine[] array, int startIdx, int validIdx) {
    for (int i = startIdx; i < validIdx; i++) {
      array[i] = array[(i + 1)];
    }
    array[validIdx] = null;
    return array;
  }
}