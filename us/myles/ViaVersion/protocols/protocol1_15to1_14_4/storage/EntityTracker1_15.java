package us.myles.ViaVersion.protocols.protocol1_15to1_14_4.storage;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_15Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.storage.EntityTracker;

public class EntityTracker1_15 extends EntityTracker {
  public EntityTracker1_15(UserConnection user) {
    super(user, (EntityType)Entity1_15Types.EntityType.PLAYER);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_15to1_14_4\storage\EntityTracker1_15.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */