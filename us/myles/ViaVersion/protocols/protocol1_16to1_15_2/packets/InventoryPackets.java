package us.myles.ViaVersion.protocols.protocol1_16to1_15_2.packets;

import java.util.UUID;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.ItemRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.UUIDIntArrayType;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.data.RecipeRewriter1_14;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.storage.InventoryTracker1_16;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ListTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.LongTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class InventoryPackets {
  public static void register(Protocol1_16To1_15_2 protocol) {
    ItemRewriter itemRewriter = new ItemRewriter((Protocol)protocol, InventoryPackets::toClient, InventoryPackets::toServer);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_15.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map(Type.COMPONENT);
            handler(wrapper -> {
                  InventoryTracker1_16 inventoryTracker = (InventoryTracker1_16)wrapper.user().get(InventoryTracker1_16.class);
                  int windowId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  int windowType = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                  if (windowType >= 20)
                    wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(++windowType)); 
                  inventoryTracker.setInventory((short)windowId);
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_15.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            handler(wrapper -> {
                  InventoryTracker1_16 inventoryTracker = (InventoryTracker1_16)wrapper.user().get(InventoryTracker1_16.class);
                  inventoryTracker.setInventory((short)-1);
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_15.WINDOW_PROPERTY, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            handler(wrapper -> {
                  short property = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                  if (property >= 4 && property <= 6) {
                    short enchantmentId = ((Short)wrapper.get((Type)Type.SHORT, 1)).shortValue();
                    if (enchantmentId >= 11) {
                      enchantmentId = (short)(enchantmentId + 1);
                      wrapper.set((Type)Type.SHORT, 1, Short.valueOf(enchantmentId));
                    } 
                  } 
                });
          }
        });
    itemRewriter.registerSetCooldown((ClientboundPacketType)ClientboundPackets1_15.COOLDOWN);
    itemRewriter.registerWindowItems((ClientboundPacketType)ClientboundPackets1_15.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    itemRewriter.registerTradeList((ClientboundPacketType)ClientboundPackets1_15.TRADE_LIST, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerSetSlot((ClientboundPacketType)ClientboundPackets1_15.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerAdvancements((ClientboundPacketType)ClientboundPackets1_15.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_15.ENTITY_EQUIPMENT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int slot = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  wrapper.write(Type.BYTE, Byte.valueOf((byte)slot));
                  InventoryPackets.toClient((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                });
          }
        });
    (new RecipeRewriter1_14((Protocol)protocol, InventoryPackets::toClient)).registerDefaultHandler((ClientboundPacketType)ClientboundPackets1_15.DECLARE_RECIPES);
    itemRewriter.registerClickWindow((ServerboundPacketType)ServerboundPackets1_16.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_16.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_16.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            handler(wrapper -> {
                  InventoryTracker1_16 inventoryTracker = (InventoryTracker1_16)wrapper.user().get(InventoryTracker1_16.class);
                  inventoryTracker.setInventory((short)-1);
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_16.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> InventoryPackets.toServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    itemRewriter.registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_15.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, Type.DOUBLE);
  }
  
  public static void toClient(Item item) {
    if (item == null)
      return; 
    if (item.getIdentifier() == 771 && item.getTag() != null) {
      CompoundTag tag = item.getTag();
      Tag ownerTag = tag.get("SkullOwner");
      if (ownerTag instanceof CompoundTag) {
        CompoundTag ownerCompundTag = (CompoundTag)ownerTag;
        Tag idTag = ownerCompundTag.get("Id");
        if (idTag instanceof StringTag) {
          UUID id = UUID.fromString((String)idTag.getValue());
          ownerCompundTag.put((Tag)new IntArrayTag("Id", UUIDIntArrayType.uuidToIntArray(id)));
        } 
      } 
    } 
    oldToNewAttributes(item);
    item.setIdentifier(Protocol1_16To1_15_2.MAPPINGS.getNewItemId(item.getIdentifier()));
  }
  
  public static void toServer(Item item) {
    if (item == null)
      return; 
    item.setIdentifier(Protocol1_16To1_15_2.MAPPINGS.getOldItemId(item.getIdentifier()));
    if (item.getIdentifier() == 771 && item.getTag() != null) {
      CompoundTag tag = item.getTag();
      Tag ownerTag = tag.get("SkullOwner");
      if (ownerTag instanceof CompoundTag) {
        CompoundTag ownerCompundTag = (CompoundTag)ownerTag;
        Tag idTag = ownerCompundTag.get("Id");
        if (idTag instanceof IntArrayTag) {
          UUID id = UUIDIntArrayType.uuidFromIntArray((int[])idTag.getValue());
          ownerCompundTag.put((Tag)new StringTag("Id", id.toString()));
        } 
      } 
    } 
    newToOldAttributes(item);
  }
  
  public static void oldToNewAttributes(Item item) {
    if (item.getTag() == null)
      return; 
    ListTag attributes = (ListTag)item.getTag().get("AttributeModifiers");
    if (attributes == null)
      return; 
    for (Tag tag : attributes) {
      CompoundTag attribute = (CompoundTag)tag;
      rewriteAttributeName(attribute, "AttributeName", false);
      rewriteAttributeName(attribute, "Name", false);
      Tag leastTag = attribute.get("UUIDLeast");
      if (leastTag != null) {
        Tag mostTag = attribute.get("UUIDMost");
        int[] uuidIntArray = UUIDIntArrayType.bitsToIntArray(((Number)leastTag.getValue()).longValue(), ((Number)mostTag.getValue()).longValue());
        attribute.put((Tag)new IntArrayTag("UUID", uuidIntArray));
      } 
    } 
  }
  
  public static void newToOldAttributes(Item item) {
    if (item.getTag() == null)
      return; 
    ListTag attributes = (ListTag)item.getTag().get("AttributeModifiers");
    if (attributes == null)
      return; 
    for (Tag tag : attributes) {
      CompoundTag attribute = (CompoundTag)tag;
      rewriteAttributeName(attribute, "AttributeName", true);
      rewriteAttributeName(attribute, "Name", true);
      IntArrayTag uuidTag = (IntArrayTag)attribute.get("UUID");
      if (uuidTag != null) {
        UUID uuid = UUIDIntArrayType.uuidFromIntArray(uuidTag.getValue());
        attribute.put((Tag)new LongTag("UUIDLeast", uuid.getLeastSignificantBits()));
        attribute.put((Tag)new LongTag("UUIDMost", uuid.getMostSignificantBits()));
      } 
    } 
  }
  
  public static void rewriteAttributeName(CompoundTag compoundTag, String entryName, boolean inverse) {
    StringTag attributeNameTag = (StringTag)compoundTag.get(entryName);
    if (attributeNameTag == null)
      return; 
    String attributeName = attributeNameTag.getValue();
    if (inverse && !attributeName.startsWith("minecraft:"))
      attributeName = "minecraft:" + attributeName; 
    String mappedAttribute = (String)(inverse ? Protocol1_16To1_15_2.MAPPINGS.getAttributeMappings().inverse() : Protocol1_16To1_15_2.MAPPINGS.getAttributeMappings()).get(attributeName);
    if (mappedAttribute == null)
      return; 
    attributeNameTag.setValue(mappedAttribute);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16to1_15_2\packets\InventoryPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */