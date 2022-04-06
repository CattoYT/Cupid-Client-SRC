package META-INF.versions.9.org.apache.logging.log4j.util;

import java.util.Base64;

public final class Base64Util {
  private static final Base64.Encoder encoder = Base64.getEncoder();
  
  public static String encode(String str) {
    return (str != null) ? encoder.encodeToString(str.getBytes()) : null;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar!\META-INF\versions\9\org\apache\logging\log4\\util\Base64Util.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */