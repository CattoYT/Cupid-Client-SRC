package us.myles.ViaVersion.protocols.protocol1_17to1_16_4.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.ItemRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.data.RecipeRewriter1_16;
import us.myles.ViaVersion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;

public class InventoryPackets {
  public static void register(Protocol1_17To1_16_4 protocol) {
    ItemRewriter itemRewriter = new ItemRewriter((Protocol)protocol, InventoryPackets::toClient, InventoryPackets::toServer);
    itemRewriter.registerSetCooldown((ClientboundPacketType)ClientboundPackets1_16_2.COOLDOWN);
    itemRewriter.registerWindowItems((ClientboundPacketType)ClientboundPackets1_16_2.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    itemRewriter.registerTradeList((ClientboundPacketType)ClientboundPackets1_16_2.TRADE_LIST, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerSetSlot((ClientboundPacketType)ClientboundPackets1_16_2.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerAdvancements((ClientboundPacketType)ClientboundPackets1_16_2.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerEntityEquipmentArray((ClientboundPacketType)ClientboundPackets1_16_2.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
    (new RecipeRewriter1_16((Protocol)protocol, InventoryPackets::toClient)).registerDefaultHandler((ClientboundPacketType)ClientboundPackets1_16_2.DECLARE_RECIPES);
    itemRewriter.registerClickWindow((ServerboundPacketType)ServerboundPackets1_16_2.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_16_2.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_16_2.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> InventoryPackets.toServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    itemRewriter.registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, Type.DOUBLE);
  }
  
  public static void toClient(Item item) {
    if (item == null)
      return; 
    item.setIdentifier(Protocol1_17To1_16_4.MAPPINGS.getNewItemId(item.getIdentifier()));
  }
  
  public static void toServer(Item item) {
    if (item == null)
      return; 
    item.setIdentifier(Protocol1_17To1_16_4.MAPPINGS.getOldItemId(item.getIdentifier()));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_17to1_16_4\packets\InventoryPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */