package us.myles.ViaVersion.protocols.protocol1_10to1_9_3.storage;

import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;

public class ResourcePackTracker extends StoredObject {
  private String lastHash = "";
  
  public ResourcePackTracker(UserConnection user) {
    super(user);
  }
  
  public String getLastHash() {
    return this.lastHash;
  }
  
  public void setLastHash(String lastHash) {
    this.lastHash = lastHash;
  }
  
  public String toString() {
    return "ResourcePackTracker{lastHash='" + this.lastHash + '\'' + '}';
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_10to1_9_3\storage\ResourcePackTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */