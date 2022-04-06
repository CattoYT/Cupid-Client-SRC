package us.myles.ViaVersion.protocols.protocol1_9to1_8.storage;

import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;

public class InventoryTracker extends StoredObject {
  private String inventory;
  
  public InventoryTracker(UserConnection user) {
    super(user);
  }
  
  public String getInventory() {
    return this.inventory;
  }
  
  public void setInventory(String inventory) {
    this.inventory = inventory;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\storage\InventoryTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */