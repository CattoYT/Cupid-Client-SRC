package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_13Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.storage.EntityTracker;

public class EntityTracker1_13 extends EntityTracker {
  public EntityTracker1_13(UserConnection user) {
    super(user, (EntityType)Entity1_13Types.EntityType.PLAYER);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\storage\EntityTracker1_13.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */