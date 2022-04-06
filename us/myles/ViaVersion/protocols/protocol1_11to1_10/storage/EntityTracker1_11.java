package us.myles.ViaVersion.protocols.protocol1_11to1_10.storage;

import com.google.common.collect.Sets;
import java.util.Set;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_11Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.storage.EntityTracker;

public class EntityTracker1_11 extends EntityTracker {
  private final Set<Integer> holograms = Sets.newConcurrentHashSet();
  
  public EntityTracker1_11(UserConnection user) {
    super(user, (EntityType)Entity1_11Types.EntityType.PLAYER);
  }
  
  public void removeEntity(int entityId) {
    super.removeEntity(entityId);
    if (isHologram(entityId))
      removeHologram(entityId); 
  }
  
  public void addHologram(int entId) {
    this.holograms.add(Integer.valueOf(entId));
  }
  
  public boolean isHologram(int entId) {
    return this.holograms.contains(Integer.valueOf(entId));
  }
  
  public void removeHologram(int entId) {
    this.holograms.remove(Integer.valueOf(entId));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_11to1_10\storage\EntityTracker1_11.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */