import java.io.IOException;
import java.util.Properties;

public class Property
{
  private static Properties prop;
  
  public static void readProperties()
  {
    prop = new Properties();
    java.io.InputStream inStream = prop.getClass().getResourceAsStream("/config.properties");
    try {
      prop.load(inStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static int getPropertyInt(String key) {
    return Integer.parseInt(prop.getProperty(key));
  }
}