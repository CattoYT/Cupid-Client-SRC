package us.myles.ViaVersion.protocols.protocol1_14to1_13_2.data;

import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data.ComponentRewriter1_13;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.packets.InventoryPackets;
import us.myles.viaversion.libs.gson.JsonObject;

public class ComponentRewriter1_14 extends ComponentRewriter1_13 {
  public ComponentRewriter1_14(Protocol protocol) {
    super(protocol);
  }
  
  protected void handleItem(Item item) {
    InventoryPackets.toClient(item);
  }
  
  protected void handleTranslate(JsonObject object, String translate) {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14to1_13_2\data\ComponentRewriter1_14.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */