package us.myles.ViaVersion.protocols.protocol1_14_1to1_14.storage;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_14Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.storage.EntityTracker;

public class EntityTracker1_14_1 extends EntityTracker {
  public EntityTracker1_14_1(UserConnection user) {
    super(user, (EntityType)Entity1_14Types.EntityType.PLAYER);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14_1to1_14\storage\EntityTracker1_14_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */