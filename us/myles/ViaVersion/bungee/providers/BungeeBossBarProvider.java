package us.myles.ViaVersion.bungee.providers;

import java.util.UUID;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.bungee.storage.BungeeStorage;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BossBarProvider;

public class BungeeBossBarProvider extends BossBarProvider {
  public void handleAdd(UserConnection user, UUID barUUID) {
    if (user.has(BungeeStorage.class)) {
      BungeeStorage storage = (BungeeStorage)user.get(BungeeStorage.class);
      if (storage.getBossbar() != null)
        storage.getBossbar().add(barUUID); 
    } 
  }
  
  public void handleRemove(UserConnection user, UUID barUUID) {
    if (user.has(BungeeStorage.class)) {
      BungeeStorage storage = (BungeeStorage)user.get(BungeeStorage.class);
      if (storage.getBossbar() != null)
        storage.getBossbar().remove(barUUID); 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\providers\BungeeBossBarProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */