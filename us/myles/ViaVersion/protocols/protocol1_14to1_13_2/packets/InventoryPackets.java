package us.myles.ViaVersion.protocols.protocol1_14to1_13_2.packets;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.ComponentRewriter;
import us.myles.ViaVersion.api.rewriters.ItemRewriter;
import us.myles.ViaVersion.api.rewriters.RecipeRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ChatRewriter;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data.RecipeRewriter1_13_2;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import us.myles.viaversion.libs.bungeecordchat.api.ChatColor;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ListTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class InventoryPackets {
  private static final String NBT_TAG_NAME = "ViaVersion|" + Protocol1_14To1_13_2.class.getSimpleName();
  
  private static final Set<String> REMOVED_RECIPE_TYPES = Sets.newHashSet((Object[])new String[] { "crafting_special_banneraddpattern", "crafting_special_repairitem" });
  
  private static final ComponentRewriter COMPONENT_REWRITER = new ComponentRewriter() {
      protected void handleTranslate(JsonObject object, String translate) {
        super.handleTranslate(object, translate);
        if (translate.startsWith("block.") && translate.endsWith(".name"))
          object.addProperty("translate", translate.substring(0, translate.length() - 5)); 
      }
    };
  
  public static void register(Protocol protocol) {
    ItemRewriter itemRewriter = new ItemRewriter(protocol, InventoryPackets::toClient, InventoryPackets::toServer);
    itemRewriter.registerSetCooldown((ClientboundPacketType)ClientboundPackets1_13.COOLDOWN);
    itemRewriter.registerAdvancements((ClientboundPacketType)ClientboundPackets1_13.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.OPEN_WINDOW, null, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Short windowsId = (Short)wrapper.read(Type.UNSIGNED_BYTE);
                    String type = (String)wrapper.read(Type.STRING);
                    JsonElement title = (JsonElement)wrapper.read(Type.COMPONENT);
                    InventoryPackets.COMPONENT_REWRITER.processText(title);
                    Short slots = (Short)wrapper.read(Type.UNSIGNED_BYTE);
                    if (type.equals("EntityHorse")) {
                      wrapper.setId(31);
                      int entityId = ((Integer)wrapper.read(Type.INT)).intValue();
                      wrapper.write(Type.UNSIGNED_BYTE, windowsId);
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(slots.intValue()));
                      wrapper.write(Type.INT, Integer.valueOf(entityId));
                    } else {
                      wrapper.setId(46);
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(windowsId.intValue()));
                      int typeId = -1;
                      switch (type) {
                        case "minecraft:container":
                        case "minecraft:chest":
                          typeId = slots.shortValue() / 9 - 1;
                          break;
                        case "minecraft:crafting_table":
                          typeId = 11;
                          break;
                        case "minecraft:furnace":
                          typeId = 13;
                          break;
                        case "minecraft:dropper":
                        case "minecraft:dispenser":
                          typeId = 6;
                          break;
                        case "minecraft:enchanting_table":
                          typeId = 12;
                          break;
                        case "minecraft:brewing_stand":
                          typeId = 10;
                          break;
                        case "minecraft:villager":
                          typeId = 18;
                          break;
                        case "minecraft:beacon":
                          typeId = 8;
                          break;
                        case "minecraft:anvil":
                          typeId = 7;
                          break;
                        case "minecraft:hopper":
                          typeId = 15;
                          break;
                        case "minecraft:shulker_box":
                          typeId = 19;
                          break;
                      } 
                      if (typeId == -1)
                        Via.getPlatform().getLogger().warning("Can't open inventory for 1.14 player! Type: " + type + " Size: " + slots); 
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(typeId));
                      wrapper.write(Type.COMPONENT, title);
                    } 
                  }
                });
          }
        });
    itemRewriter.registerWindowItems((ClientboundPacketType)ClientboundPackets1_13.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
    itemRewriter.registerSetSlot((ClientboundPacketType)ClientboundPackets1_13.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String channel = (String)wrapper.get(Type.STRING, 0);
                    if (channel.equals("minecraft:trader_list") || channel.equals("trader_list")) {
                      wrapper.setId(39);
                      wrapper.resetReader();
                      wrapper.read(Type.STRING);
                      int windowId = ((Integer)wrapper.read(Type.INT)).intValue();
                      ((EntityTracker1_14)wrapper.user().get(EntityTracker1_14.class)).setLatestTradeWindowId(windowId);
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(windowId));
                      int size = ((Short)wrapper.passthrough(Type.UNSIGNED_BYTE)).shortValue();
                      for (int i = 0; i < size; i++) {
                        InventoryPackets.toClient((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                        InventoryPackets.toClient((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                        boolean secondItem = ((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue();
                        if (secondItem)
                          InventoryPackets.toClient((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)); 
                        wrapper.passthrough(Type.BOOLEAN);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                        wrapper.write(Type.INT, Integer.valueOf(0));
                        wrapper.write(Type.INT, Integer.valueOf(0));
                        wrapper.write((Type)Type.FLOAT, Float.valueOf(0.0F));
                      } 
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                      wrapper.write(Type.BOOLEAN, Boolean.valueOf(false));
                    } else if (channel.equals("minecraft:book_open") || channel.equals("book_open")) {
                      int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                      wrapper.clearPacket();
                      wrapper.setId(45);
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(hand));
                    } 
                  }
                });
          }
        });
    itemRewriter.registerEntityEquipment((ClientboundPacketType)ClientboundPackets1_13.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
    final RecipeRewriter1_13_2 recipeRewriter = new RecipeRewriter1_13_2(protocol, InventoryPackets::toClient);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.DECLARE_RECIPES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  int deleted = 0;
                  for (int i = 0; i < size; i++) {
                    String id = (String)wrapper.read(Type.STRING);
                    String type = (String)wrapper.read(Type.STRING);
                    if (InventoryPackets.REMOVED_RECIPE_TYPES.contains(type)) {
                      deleted++;
                    } else {
                      wrapper.write(Type.STRING, type);
                      wrapper.write(Type.STRING, id);
                      recipeRewriter.handle(wrapper, type);
                    } 
                  } 
                  wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(size - deleted));
                });
          }
        });
    itemRewriter.registerClickWindow((ServerboundPacketType)ServerboundPackets1_14.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_14.SELECT_TRADE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    PacketWrapper resyncPacket = wrapper.create(8);
                    resyncPacket.write(Type.UNSIGNED_BYTE, Short.valueOf((short)((EntityTracker1_14)wrapper.user().get(EntityTracker1_14.class)).getLatestTradeWindowId()));
                    resyncPacket.write((Type)Type.SHORT, Short.valueOf((short)-999));
                    resyncPacket.write(Type.BYTE, Byte.valueOf((byte)2));
                    resyncPacket.write((Type)Type.SHORT, Short.valueOf((short)ThreadLocalRandom.current().nextInt()));
                    resyncPacket.write((Type)Type.VAR_INT, Integer.valueOf(5));
                    CompoundTag tag = new CompoundTag("");
                    tag.put((Tag)new DoubleTag("force_resync", Double.NaN));
                    resyncPacket.write(Type.FLAT_VAR_INT_ITEM, new Item(1, (byte)1, (short)0, tag));
                    resyncPacket.sendToServer(Protocol1_14To1_13_2.class, true, false);
                  }
                });
          }
        });
    itemRewriter.registerCreativeInvAction((ServerboundPacketType)ServerboundPackets1_14.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
    itemRewriter.registerSpawnParticle((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, (Type)Type.FLOAT);
  }
  
  public static void toClient(Item item) {
    if (item == null)
      return; 
    item.setIdentifier(Protocol1_14To1_13_2.MAPPINGS.getNewItemId(item.getIdentifier()));
    if (item.getTag() == null)
      return; 
    Tag displayTag = item.getTag().get("display");
    if (displayTag instanceof CompoundTag) {
      CompoundTag display = (CompoundTag)displayTag;
      Tag loreTag = display.get("Lore");
      if (loreTag instanceof ListTag) {
        ListTag lore = (ListTag)loreTag;
        display.put((Tag)new ListTag(NBT_TAG_NAME + "|Lore", lore.clone().getValue()));
        for (Tag loreEntry : lore) {
          if (loreEntry instanceof StringTag) {
            String jsonText = ChatRewriter.fromLegacyTextAsString(((StringTag)loreEntry).getValue(), ChatColor.WHITE, true);
            ((StringTag)loreEntry).setValue(jsonText);
          } 
        } 
      } 
    } 
  }
  
  public static void toServer(Item item) {
    if (item == null)
      return; 
    item.setIdentifier(Protocol1_14To1_13_2.MAPPINGS.getOldItemId(item.getIdentifier()));
    if (item.getTag() == null)
      return; 
    Tag displayTag = item.getTag().get("display");
    if (displayTag instanceof CompoundTag) {
      CompoundTag display = (CompoundTag)displayTag;
      Tag loreTag = display.get("Lore");
      if (loreTag instanceof ListTag) {
        ListTag lore = (ListTag)loreTag;
        ListTag savedLore = (ListTag)display.remove(NBT_TAG_NAME + "|Lore");
        if (savedLore != null) {
          display.put((Tag)new ListTag("Lore", savedLore.getValue()));
        } else {
          for (Tag loreEntry : lore) {
            if (loreEntry instanceof StringTag)
              ((StringTag)loreEntry).setValue(ChatRewriter.jsonTextToLegacy(((StringTag)loreEntry).getValue())); 
          } 
        } 
      } 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14to1_13_2\packets\InventoryPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */