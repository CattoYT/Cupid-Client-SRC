package us.myles.ViaVersion.protocols.protocol1_12to1_11_1.storage;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_12Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.storage.EntityTracker;

public class EntityTracker1_12 extends EntityTracker {
  public EntityTracker1_12(UserConnection user) {
    super(user, (EntityType)Entity1_12Types.EntityType.PLAYER);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_12to1_11_1\storage\EntityTracker1_12.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */