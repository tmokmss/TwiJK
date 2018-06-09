import java.io.PrintStream;
import twitter4j.*;

public class TwitterListen extends Thread {
  private int BUFFER_SIZE = 20;
  private String[] buffer = new String[this.BUFFER_SIZE];
  private long[] id = new long[this.BUFFER_SIZE];
  private int index = 0;
  private String[] words;
  twitter4j.Twitter twitter;
  private static twitter4j.TwitterStream twitterStream;
  
  TwitterListen(String[] words) {
    this.words = words;
  }
  
  public void run() {
    twitterStream = twitter4j.TwitterStreamFactory.getSingleton();
    twitterStream.setOAuthConsumer(OAuther.getCons_key(), OAuther.getCons_key_secret());
    twitterStream.setOAuthAccessToken(OAuther.getAccessToken());
    
    twitter4j.StatusListener listener = new twitter4j.StatusListener()
    {
      public void onStatus(twitter4j.Status status) {
        if (!status.isRetweet())
        {
          if (index < buffer.length) {
            buffer[index] = status.getText();
            id[index] = status.getId();
            index += 1;
          }
          else {
            TweetLine.setSpeed(20);
          }
        }
        System.out.println("@" + status.getUser().getScreenName());
        System.out.println(status.getText());
        System.out.println("---------------------");
      }
      
      public void onDeletionNotice(twitter4j.StatusDeletionNotice statusDeletionNotice) {
        System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
      }
      
      public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
      }
      
      public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
      }

      public void onStallWarning(StallWarning warning)
      {
        System.out.println("Got stall warning:" + warning);
      }

      public void onException(Exception ex) {
        ex.printStackTrace();
      }
      
    };
    twitterStream.addListener(listener);
    twitter4j.FilterQuery filterq = new twitter4j.FilterQuery();
    filterq.track(this.words);
    twitterStream.filter(filterq);
  }
  
  public int availableNum() {
    return this.index;
  }
  
  public TweetLine[] getBuffer() {
    TweetLine[] tempbuf = new TweetLine[this.index];
    try {
      for (int i = 0; i < this.index; i++) {
        tempbuf[i] = new TweetLine(this.buffer[i]);
        tempbuf[i].setId(this.id[i]);
      }
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
    this.index = 0;
    return tempbuf;
  }
  
  public static void updateFilter(String[] words) {
    twitter4j.FilterQuery filterq = new twitter4j.FilterQuery();
    filterq.track(words);
    twitterStream.filter(filterq);
  }
}