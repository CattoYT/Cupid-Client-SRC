package us.myles.ViaVersion.bungee.providers;

import java.lang.reflect.Method;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MainHandProvider;

public class BungeeMainHandProvider extends MainHandProvider {
  private static Method getSettings = null;
  
  private static Method setMainHand = null;
  
  static {
    try {
      getSettings = Class.forName("net.md_5.bungee.UserConnection").getDeclaredMethod("getSettings", new Class[0]);
      setMainHand = Class.forName("net.md_5.bungee.protocol.packet.ClientSettings").getDeclaredMethod("setMainHand", new Class[] { int.class });
    } catch (Exception exception) {}
  }
  
  public void setMainHand(UserConnection user, int hand) {
    ProtocolInfo info = user.getProtocolInfo();
    if (info == null || info.getUuid() == null)
      return; 
    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(info.getUuid());
    if (player == null)
      return; 
    try {
      Object settings = getSettings.invoke(player, new Object[0]);
      if (settings != null)
        setMainHand.invoke(settings, new Object[] { Integer.valueOf(hand) }); 
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      e.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\providers\BungeeMainHandProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */