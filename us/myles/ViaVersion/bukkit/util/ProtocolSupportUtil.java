package us.myles.ViaVersion.bukkit.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;

public class ProtocolSupportUtil {
  private static Method protocolVersionMethod = null;
  
  private static Method getIdMethod = null;
  
  static {
    try {
      protocolVersionMethod = Class.forName("protocolsupport.api.ProtocolSupportAPI").getMethod("getProtocolVersion", new Class[] { Player.class });
      getIdMethod = Class.forName("protocolsupport.api.ProtocolVersion").getMethod("getId", new Class[0]);
    } catch (Exception exception) {}
  }
  
  public static int getProtocolVersion(Player player) {
    if (protocolVersionMethod == null)
      return -1; 
    try {
      Object version = protocolVersionMethod.invoke(null, new Object[] { player });
      return ((Integer)getIdMethod.invoke(version, new Object[0])).intValue();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } 
    return -1;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukki\\util\ProtocolSupportUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */