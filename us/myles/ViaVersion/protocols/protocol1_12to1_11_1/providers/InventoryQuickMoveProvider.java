package us.myles.ViaVersion.protocols.protocol1_12to1_11_1.providers;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.platform.providers.Provider;

public class InventoryQuickMoveProvider implements Provider {
  public boolean registerQuickMoveAction(short windowId, short slotId, short actionId, UserConnection userConnection) {
    return false;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_12to1_11_1\providers\InventoryQuickMoveProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */