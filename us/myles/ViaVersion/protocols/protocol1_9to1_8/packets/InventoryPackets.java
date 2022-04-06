package us.myles.ViaVersion.protocols.protocol1_9to1_8.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.remapper.ValueCreator;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_8.ClientboundPackets1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ItemRewriter;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.InventoryTracker;

public class InventoryPackets {
  public static void register(Protocol protocol) {
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.WINDOW_PROPERTY, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    final short windowId = ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue();
                    final short property = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                    short value = ((Short)wrapper.get((Type)Type.SHORT, 1)).shortValue();
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    if (inventoryTracker.getInventory() != null && 
                      inventoryTracker.getInventory().equalsIgnoreCase("minecraft:enchanting_table") && 
                      property > 3 && property < 7) {
                      short level = (short)(value >> 8);
                      final short enchantID = (short)(value & 0xFF);
                      wrapper.create(wrapper.getId(), new ValueCreator() {
                            public void write(PacketWrapper wrapper) throws Exception {
                              wrapper.write(Type.UNSIGNED_BYTE, Short.valueOf(windowId));
                              wrapper.write((Type)Type.SHORT, Short.valueOf(property));
                              wrapper.write((Type)Type.SHORT, Short.valueOf(enchantID));
                            }
                          }).send(Protocol1_9To1_8.class);
                      wrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)(property + 3)));
                      wrapper.set((Type)Type.SHORT, 1, Short.valueOf(level));
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.OPEN_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            map(Type.STRING);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String inventory = (String)wrapper.get(Type.STRING, 0);
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    inventoryTracker.setInventory(inventory);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String inventory = (String)wrapper.get(Type.STRING, 0);
                    if (inventory.equals("minecraft:brewing_stand"))
                      wrapper.set(Type.UNSIGNED_BYTE, 1, Short.valueOf((short)(((Short)wrapper.get(Type.UNSIGNED_BYTE, 1)).shortValue() + 1))); 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.SET_SLOT, new PacketRemapper() {
          public void registerMap() {
            map(Type.BYTE);
            map((Type)Type.SHORT);
            map(Type.ITEM);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item stack = (Item)wrapper.get(Type.ITEM, 0);
                    ItemRewriter.toClient(stack);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    short slotID = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                    if (inventoryTracker.getInventory() != null && 
                      inventoryTracker.getInventory().equals("minecraft:brewing_stand") && 
                      slotID >= 4)
                      wrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)(slotID + 1))); 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.WINDOW_ITEMS, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            map(Type.ITEM_ARRAY);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item[] stacks = (Item[])wrapper.get(Type.ITEM_ARRAY, 0);
                    for (Item stack : stacks)
                      ItemRewriter.toClient(stack); 
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    if (inventoryTracker.getInventory() != null && 
                      inventoryTracker.getInventory().equals("minecraft:brewing_stand")) {
                      Item[] oldStack = (Item[])wrapper.get(Type.ITEM_ARRAY, 0);
                      Item[] newStack = new Item[oldStack.length + 1];
                      for (int i = 0; i < newStack.length; i++) {
                        if (i > 4) {
                          newStack[i] = oldStack[i - 1];
                        } else if (i != 4) {
                          newStack[i] = oldStack[i];
                        } 
                      } 
                      wrapper.set(Type.ITEM_ARRAY, 0, newStack);
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    inventoryTracker.setInventory(null);
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.MAP_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.BYTE);
            create(new ValueCreator() {
                  public void write(PacketWrapper wrapper) {
                    wrapper.write(Type.BOOLEAN, Boolean.valueOf(true));
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.SHORT);
            map(Type.ITEM);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item stack = (Item)wrapper.get(Type.ITEM, 0);
                    ItemRewriter.toServer(stack);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    final short slot = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                    boolean throwItem = (slot == 45);
                    if (throwItem) {
                      wrapper.create(22, new ValueCreator() {
                            public void write(PacketWrapper wrapper) throws Exception {
                              wrapper.write(Type.BYTE, Byte.valueOf((byte)0));
                              wrapper.write((Type)Type.SHORT, Short.valueOf(slot));
                              wrapper.write(Type.ITEM, null);
                            }
                          }).send(Protocol1_9To1_8.class);
                      wrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)-999));
                    } 
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.CLICK_WINDOW, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map(Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.VAR_INT, Type.BYTE);
            map(Type.ITEM);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item stack = (Item)wrapper.get(Type.ITEM, 0);
                    ItemRewriter.toServer(stack);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    final short windowID = ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue();
                    final short slot = ((Short)wrapper.get((Type)Type.SHORT, 0)).shortValue();
                    boolean throwItem = (slot == 45 && windowID == 0);
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    if (inventoryTracker.getInventory() != null && 
                      inventoryTracker.getInventory().equals("minecraft:brewing_stand")) {
                      if (slot == 4)
                        throwItem = true; 
                      if (slot > 4)
                        wrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)(slot - 1))); 
                    } 
                    if (throwItem) {
                      wrapper.create(22, new ValueCreator() {
                            public void write(PacketWrapper wrapper) throws Exception {
                              wrapper.write(Type.BYTE, Byte.valueOf((byte)windowID));
                              wrapper.write((Type)Type.SHORT, Short.valueOf(slot));
                              wrapper.write(Type.ITEM, null);
                            }
                          }).send(Protocol1_9To1_8.class);
                      wrapper.set(Type.BYTE, 0, Byte.valueOf((byte)0));
                      wrapper.set(Type.BYTE, 1, Byte.valueOf((byte)0));
                      wrapper.set((Type)Type.SHORT, 0, Short.valueOf((short)-999));
                    } 
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.CLOSE_WINDOW, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    InventoryTracker inventoryTracker = (InventoryTracker)wrapper.user().get(InventoryTracker.class);
                    inventoryTracker.setInventory(null);
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.HELD_ITEM_CHANGE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    if (entityTracker.isBlocking()) {
                      entityTracker.setBlocking(false);
                      entityTracker.setSecondHand(null);
                    } 
                  }
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\packets\InventoryPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */