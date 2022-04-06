package us.myles.ViaVersion.protocols.protocol1_9to1_8.providers;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.platform.providers.Provider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;

public class EntityIdProvider implements Provider {
  public int getEntityId(UserConnection user) throws Exception {
    return ((EntityTracker1_9)user.get(EntityTracker1_9.class)).getClientEntityId();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\providers\EntityIdProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */