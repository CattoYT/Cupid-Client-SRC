package us.myles.ViaVersion.protocols.protocol1_9to1_8.storage;

import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Position;

public class PlaceBlockTracker extends StoredObject {
  private long lastPlaceTimestamp = 0L;
  
  private Position lastPlacedPosition = null;
  
  public PlaceBlockTracker(UserConnection user) {
    super(user);
  }
  
  public boolean isExpired(int ms) {
    return (System.currentTimeMillis() > this.lastPlaceTimestamp + ms);
  }
  
  public void updateTime() {
    this.lastPlaceTimestamp = System.currentTimeMillis();
  }
  
  public long getLastPlaceTimestamp() {
    return this.lastPlaceTimestamp;
  }
  
  public Position getLastPlacedPosition() {
    return this.lastPlacedPosition;
  }
  
  public void setLastPlacedPosition(Position lastPlacedPosition) {
    this.lastPlacedPosition = lastPlacedPosition;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\storage\PlaceBlockTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */