package us.myles.ViaVersion.protocols.protocol1_16to1_15_2.storage;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_16Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.storage.EntityTracker;

public class EntityTracker1_16 extends EntityTracker {
  public EntityTracker1_16(UserConnection user) {
    super(user, (EntityType)Entity1_16Types.EntityType.PLAYER);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16to1_15_2\storage\EntityTracker1_16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */