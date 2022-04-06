package us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.storage;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_16_2Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.storage.EntityTracker;

public class EntityTracker1_16_2 extends EntityTracker {
  public EntityTracker1_16_2(UserConnection user) {
    super(user, (EntityType)Entity1_16_2Types.EntityType.PLAYER);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16_2to1_16_1\storage\EntityTracker1_16_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */