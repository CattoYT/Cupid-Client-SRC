package us.myles.ViaVersion.api.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.data.ExternalJoinGameListener;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.EntityType;

public abstract class EntityTracker extends StoredObject implements ExternalJoinGameListener {
  private final Map<Integer, EntityType> clientEntityTypes = new ConcurrentHashMap<>();
  
  private final EntityType playerType;
  
  private int clientEntityId;
  
  protected EntityTracker(UserConnection user, EntityType playerType) {
    super(user);
    this.playerType = playerType;
  }
  
  public void removeEntity(int entityId) {
    this.clientEntityTypes.remove(Integer.valueOf(entityId));
  }
  
  public void addEntity(int entityId, EntityType type) {
    this.clientEntityTypes.put(Integer.valueOf(entityId), type);
  }
  
  public boolean hasEntity(int entityId) {
    return this.clientEntityTypes.containsKey(Integer.valueOf(entityId));
  }
  
  @Nullable
  public EntityType getEntity(int entityId) {
    return this.clientEntityTypes.get(Integer.valueOf(entityId));
  }
  
  public void onExternalJoinGame(int playerEntityId) {
    this.clientEntityId = playerEntityId;
    this.clientEntityTypes.put(Integer.valueOf(playerEntityId), this.playerType);
  }
  
  public int getClientEntityId() {
    return this.clientEntityId;
  }
  
  public void setClientEntityId(int clientEntityId) {
    this.clientEntityId = clientEntityId;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\storage\EntityTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */