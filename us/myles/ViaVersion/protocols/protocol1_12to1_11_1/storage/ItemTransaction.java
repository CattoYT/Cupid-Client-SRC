package us.myles.ViaVersion.protocols.protocol1_12to1_11_1.storage;

public class ItemTransaction {
  private final short windowId;
  
  private final short slotId;
  
  private final short actionId;
  
  public ItemTransaction(short windowId, short slotId, short actionId) {
    this.windowId = windowId;
    this.slotId = slotId;
    this.actionId = actionId;
  }
  
  public short getWindowId() {
    return this.windowId;
  }
  
  public short getSlotId() {
    return this.slotId;
  }
  
  public short getActionId() {
    return this.actionId;
  }
  
  public String toString() {
    return "ItemTransaction{windowId=" + this.windowId + ", slotId=" + this.slotId + ", actionId=" + this.actionId + '}';
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_12to1_11_1\storage\ItemTransaction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */