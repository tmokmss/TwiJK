import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OAuther
{
  private static AccessToken accessToken;
  private static String tokenFileName = "auth";
  private static String cons_key = "ZuDwHh5Xt1W3dTTniLTsFXQLh";
  private static String cons_key_secret = "TOu7fJtEOCgMxtRFrTmZK7JZ9fBpJXm0rb9BQ6xwBMMdOxloIT";
  private static String cryptKey = "bnLQp9VrCA";
  
  static void setAccessToken()
          throws TwitterException, IOException
  {
    Twitter twitter = TwitterFactory.getSingleton();
    twitter.setOAuthConsumer(cons_key, cons_key_secret);
    RequestToken requestToken = twitter.getOAuthRequestToken();
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    while (accessToken == null)
    {
      System.out.println("Open the following URL and grant access to your account:");
      System.out.println(requestToken.getAuthorizationURL());
      Desktop desktop = Desktop.getDesktop();
      try
      {
        desktop.browse(new URI(requestToken.getAuthorizationURL()));
      }
      catch (URISyntaxException e)
      {
        e.printStackTrace();
      }
      System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
      String pin = br.readLine();
      try
      {
        if (pin.length() > 0) {
          accessToken = twitter.getOAuthAccessToken(requestToken, pin);
        } else {
          accessToken = twitter.getOAuthAccessToken();
        }
      }
      catch (TwitterException te)
      {
        if (401 == te.getStatusCode()) {
          System.out.println("Unable to get the access token.");
        } else {
          te.printStackTrace();
        }
      }
      String token = accessToken.getToken();
      String tokenSecret = accessToken.getTokenSecret();
      long usrId = twitter.verifyCredentials().getId();
      try
      {
        saveTokens(cryptTokens(token, tokenSecret, usrId));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      System.out.println("OAuth completed.");
    }
  }

  public static AccessToken retrieveAccessToken()
  {
    String[] tokens = new String[3];
    try
    {
      tokens = readTokens();
    }
    catch (IOException e)
    {
      System.out.println("Failed to read auth file. You need new authentication.");
      return null;
    }
    accessToken = new AccessToken(tokens[0], tokens[1]);
    return accessToken;
  }

  private static void saveTokens(byte[] tokensCrypted)
          throws IOException
  {
    File file = new File(tokenFileName);
    FileOutputStream fos = new FileOutputStream(file);
    fos.write(tokensCrypted);
    fos.close();
  }

  private static String[] readTokens()
          throws IOException
  {
    File file = new File(tokenFileName);
    FileInputStream fis = new FileInputStream(file);
    byte[] data = new byte[(byte)(int)file.length()];
    fis.read(data);
    fis.close();
    try
    {
      return decryptTokens(data);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  private static byte[] cryptTokens(String token, String tokenSecret, long id)
          throws Exception
  {
    String data = token + "," + tokenSecret + "," + String.valueOf(id);
    SecretKeySpec sksSpec = new SecretKeySpec(cryptKey.getBytes(), "Blowfish");

    Cipher cipher = Cipher.getInstance("Blowfish");
    cipher.init(1, sksSpec);

    return cipher.doFinal(data.getBytes());
  }

  private static String[] decryptTokens(byte[] encrypted)
          throws Exception
  {
    String[] tokens = new String[3];
    String tempBuf = "";
    int idx = 0;

    SecretKeySpec sksSpec = new SecretKeySpec(cryptKey.getBytes(), "Blowfish");
    Cipher cipher = Cipher.getInstance("Blowfish");
    cipher.init(2, sksSpec);
    String decryptedData = new String(cipher.doFinal(encrypted));
    for (int i = 0; i < decryptedData.length(); i++)
    {
      char letter = decryptedData.charAt(i);
      switch (letter)
      {
        case ',':
          tokens[(idx++)] = tempBuf;
          tempBuf = "";
          break;
        default:
          tempBuf = tempBuf + letter;
      }
    }
    return tokens;
  }

  public static String getCons_key()
  {
    return cons_key;
  }

  public static String getCons_key_secret()
  {
    return cons_key_secret;
  }

  public static AccessToken getAccessToken()
  {
    return accessToken;
  }

  public static String getUserName()
  {
    long id = accessToken.getUserId();
    try
    {
      User user = TwitterUtl.twitter.showUser(id);
      return user.getScreenName();
    }
    catch (TwitterException e)
    {
      e.printStackTrace();
    }
    return "";
  }
}
