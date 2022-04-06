package us.myles.ViaVersion.bungee.providers;

import java.lang.reflect.Method;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.bungee.storage.BungeeStorage;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.EntityIdProvider;

public class BungeeEntityIdProvider extends EntityIdProvider {
  private static Method getClientEntityId;
  
  static {
    try {
      getClientEntityId = Class.forName("net.md_5.bungee.UserConnection").getDeclaredMethod("getClientEntityId", new Class[0]);
    } catch (NoSuchMethodException|ClassNotFoundException e) {
      e.printStackTrace();
    } 
  }
  
  public int getEntityId(UserConnection user) throws Exception {
    BungeeStorage storage = (BungeeStorage)user.get(BungeeStorage.class);
    ProxiedPlayer player = storage.getPlayer();
    return ((Integer)getClientEntityId.invoke(player, new Object[0])).intValue();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\providers\BungeeEntityIdProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */