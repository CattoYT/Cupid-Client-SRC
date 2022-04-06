package us.myles.ViaVersion.protocols.protocol1_13_1to1_13.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.ItemRewriter;
import us.myles.ViaVersion.api.rewriters.RecipeRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data.RecipeRewriter1_13_2;

public class InventoryPackets {
  public static void register(Protocol1_13_1To1_13 protocol) {
    ItemRewriter itemRewriter = new ItemRewriter((Protocol)protocol, InventoryPackets::toClient, InventoryPackets::toServer);
    itemRewriter.registerSetSlot((ClientboundPacketType)ClientboundPackets1_13.SET_SLOT, Type.FLAT_ITEM);
    itemRewriter.registerWindowItems((ClientboundPacketType)ClientboundPackets1_13.WINDOW_ITEMS, Type.FLAT_ITEM_ARRAY);
    itemRewriter.registerAdvancements((ClientboundPacketType)ClientboundPackets1_13.ADVANCEMENTS, Type.FLAT_ITEM);
    itemRewriter.registerSetCooldown((ClientboundPacketType)ClientboundPackets1_13.COOLDOWN);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String channel = (String)wrapper.get(Type.STRING, 0);
                    if (channel.equals("minecraft:trader_list") || channel.equals("trader_list")) {
                      wrapper.passthrough(Type.INT);
                      int size = ((Short)wrapper.passthrough(Type.UNSIGNED_BYTE)).shortValue();
                      for (int i = 0; i < size; i++) {
                        InventoryPackets.toClient((Item)wrapper.passthrough(Type.FLAT_ITEM));
                        InventoryPackets.toClient((Item)wrapper.passthrough(Type.FLAT_ITEM));
                        boolean secondItem = ((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue();
                        if (secondItem)
                          InventoryPackets.toClient((Item)wrapper.passthrough(Type.FLAT_ITEM)); 
                        wrapper.passthrough(Type.BOOLEAN);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                      } 
                    } 
                  }
                });
          }
        });
    itemRewriter.registerEntityEquipment((ClientboundPacketType)ClientboundPackets1_13.ENTITY_EQUIPMENT, Type.FLAT_ITEM);
    final RecipeRewriter1_13_2 recipeRewriter = new RecipeRewriter1_13_2((Protocol)protocol, InventoryPackets::toClient);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.DECLARE_RECIPES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  for (int i = 0; i < size; i++) {
                    String id = (String)wrapper.passthrough(Type.STRING);
                    String type = ((String)wrapper.passthrough(Type.STRING)).replace("minecraft:", "");
                    recipeRewriter.handle(wrapper, type);
                  } 
                });
          }
        });
    itemRewriter.registerClickWindow((ServerboundPacketType)ServerboundPackets1_13.CLICK_WINDOW, Type.FLAT_ITEM);
    itemRewriter.registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_13.CREATIVE_INVENTORY_ACTION, Type.FLAT_ITEM);
    itemRewriter.registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PARTICLE, Type.FLAT_ITEM, (Type)Type.FLOAT);
  }
  
  public static void toClient(Item item) {
    if (item == null)
      return; 
    item.setIdentifier(Protocol1_13_1To1_13.MAPPINGS.getNewItemId(item.getIdentifier()));
  }
  
  public static void toServer(Item item) {
    if (item == null)
      return; 
    item.setIdentifier(Protocol1_13_1To1_13.MAPPINGS.getOldItemId(item.getIdentifier()));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13_1to1_13\packets\InventoryPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */