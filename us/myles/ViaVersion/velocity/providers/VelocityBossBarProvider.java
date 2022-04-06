package us.myles.ViaVersion.velocity.providers;

import java.util.UUID;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BossBarProvider;
import us.myles.ViaVersion.velocity.storage.VelocityStorage;

public class VelocityBossBarProvider extends BossBarProvider {
  public void handleAdd(UserConnection user, UUID barUUID) {
    if (user.has(VelocityStorage.class)) {
      VelocityStorage storage = (VelocityStorage)user.get(VelocityStorage.class);
      if (storage.getBossbar() != null)
        storage.getBossbar().add(barUUID); 
    } 
  }
  
  public void handleRemove(UserConnection user, UUID barUUID) {
    if (user.has(VelocityStorage.class)) {
      VelocityStorage storage = (VelocityStorage)user.get(VelocityStorage.class);
      if (storage.getBossbar() != null)
        storage.getBossbar().remove(barUUID); 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\providers\VelocityBossBarProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */