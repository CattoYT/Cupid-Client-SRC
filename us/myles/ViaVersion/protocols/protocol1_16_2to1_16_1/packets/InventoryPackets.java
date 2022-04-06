package us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.ItemRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.data.RecipeRewriter1_16;

public class InventoryPackets {
  public static void register(Protocol1_16_2To1_16_1 protocol) {
    ItemRewriter itemRewriter = new ItemRewriter((Protocol)protocol, InventoryPackets::toClient, InventoryPackets::toServer);
    itemRewriter.registerSetCooldown((ClientboundPacketType)ClientboundPackets1_16.COOLDOWN);
    itemRewriter.registerWindowItems((ClientboundPacketType)ClientboundPackets1_16.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    itemRewriter.registerTradeList((ClientboundPacketType)ClientboundPackets1_16.TRADE_LIST, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerSetSlot((ClientboundPacketType)ClientboundPackets1_16.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerEntityEquipmentArray((ClientboundPacketType)ClientboundPackets1_16.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerAdvancements((ClientboundPacketType)ClientboundPackets1_16.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_16.UNLOCK_RECIPES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  wrapper.passthrough(Type.BOOLEAN);
                  wrapper.passthrough(Type.BOOLEAN);
                  wrapper.passthrough(Type.BOOLEAN);
                  wrapper.passthrough(Type.BOOLEAN);
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(false));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(false));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(false));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(false));
                });
          }
        });
    (new RecipeRewriter1_16((Protocol)protocol, InventoryPackets::toClient)).registerDefaultHandler((ClientboundPacketType)ClientboundPackets1_16.DECLARE_RECIPES);
    itemRewriter.registerClickWindow((ServerboundPacketType)ServerboundPackets1_16_2.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_16_2.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_16_2.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> InventoryPackets.toServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    itemRewriter.registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_16.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, Type.DOUBLE);
  }
  
  public static void toClient(Item item) {
    if (item == null)
      return; 
    item.setIdentifier(Protocol1_16_2To1_16_1.MAPPINGS.getNewItemId(item.getIdentifier()));
  }
  
  public static void toServer(Item item) {
    if (item == null)
      return; 
    item.setIdentifier(Protocol1_16_2To1_16_1.MAPPINGS.getOldItemId(item.getIdentifier()));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16_2to1_16_1\packets\InventoryPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */