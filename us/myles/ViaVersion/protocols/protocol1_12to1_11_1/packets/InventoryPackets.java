package us.myles.ViaVersion.protocols.protocol1_12to1_11_1.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.ItemRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.BedRewriter;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.ServerboundPackets1_12;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.providers.InventoryQuickMoveProvider;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;

public class InventoryPackets {
  public static void register(Protocol1_12To1_11_1 protocol) {
    ItemRewriter itemRewriter = new ItemRewriter((Protocol)protocol, BedRewriter::toClientItem, BedRewriter::toServerItem);
    itemRewriter.registerSetSlot((ClientboundPacketType)ClientboundPackets1_9_3.SET_SLOT, Type.ITEM);
    itemRewriter.registerWindowItems((ClientboundPacketType)ClientboundPackets1_9_3.WINDOW_ITEMS, Type.ITEM_ARRAY);
    itemRewriter.registerEntityEquipment((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_EQUIPMENT, Type.ITEM);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((String)wrapper.get(Type.STRING, 0)).equalsIgnoreCase("MC|TrList")) {
                      wrapper.passthrough(Type.INT);
                      int size = ((Short)wrapper.passthrough(Type.UNSIGNED_BYTE)).shortValue();
                      for (int i = 0; i < size; i++) {
                        BedRewriter.toClientItem((Item)wrapper.passthrough(Type.ITEM));
                        BedRewriter.toClientItem((Item)wrapper.passthrough(Type.ITEM));
                        boolean secondItem = ((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue();
                        if (secondItem)
                          BedRewriter.toClientItem((Item)wrapper.passthrough(Type.ITEM)); 
                        wrapper.passthrough(Type.BOOLEAN);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_12.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map(Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.VAR_INT);
            map(Type.ITEM);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item item = (Item)wrapper.get(Type.ITEM, 0);
                    if (!Via.getConfig().is1_12QuickMoveActionFix()) {
                      BedRewriter.toServerItem(item);
                      return;
                    } 
                    byte button = ((Byte)wrapper.get(Type.BYTE, 0)).byteValue();
                    int mode = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (mode == 1 && button == 0 && item == null) {
                      short windowId = ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue();
                      short slotId = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                      short actionId = ((Short)wrapper.get((Type)Type.SHORT, 1)).shortValue();
                      InventoryQuickMoveProvider provider = (InventoryQuickMoveProvider)Via.getManager().getProviders().get(InventoryQuickMoveProvider.class);
                      boolean succeed = provider.registerQuickMoveAction(windowId, slotId, actionId, wrapper.user());
                      if (succeed)
                        wrapper.cancel(); 
                    } else {
                      BedRewriter.toServerItem(item);
                    } 
                  }
                });
          }
        });
    itemRewriter.registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_12.CREATIVE_INVENTORY_ACTION, Type.ITEM);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_12to1_11_1\packets\InventoryPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */