import twitter4j.TwitterException;

public class TwitterUtl extends Thread
{
  static twitter4j.Twitter twitter;
  String myStatus;
  long myId;
  String order;
  
  public void run()
  {
    switch (order){
      case "favorite":
        try {
          twitter.createFavorite(this.myId);
          System.out.println("Successfully created a favorite.");
        } catch (TwitterException e) {
          e.printStackTrace();
        }
      break;

      case "retweet":
        try {
          twitter.retweetStatus(this.myId);
          System.out.println("Successfully retweeted a status.");
        } catch (TwitterException e) {
          e.printStackTrace();
        }
      break;
      
      case "post":
        try {
          twitter.updateStatus(this.myStatus);
          System.out.println("Successfully posted a status.");
        } catch (TwitterException e) {
          e.printStackTrace();
        }
      break;
    }
  }
  
  public static void authTwitter() {
    twitter4j.TwitterFactory tf = new twitter4j.TwitterFactory();
    twitter = tf.getInstance();
    twitter.setOAuthConsumer(OAuther.getCons_key(), OAuther.getCons_key_secret());
    twitter.setOAuthAccessToken(OAuther.getAccessToken());
  }
  
  public void favorite(long id) {
    this.order = "favorite";
    this.myId = id;
    start();
  }
  
  public void retweet(long id) {
    this.order = "retweet";
    this.myId = id;
    start();
  }
  
  public void post(String status) {
    this.order = "post";
    this.myStatus = status;
    start();
  }
}