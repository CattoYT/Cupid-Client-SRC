package us.myles.ViaVersion.api.rewriters;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.ParticleMappings;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;

public class ItemRewriter {
  private final Protocol protocol;
  
  private final RewriteFunction toClient;
  
  private final RewriteFunction toServer;
  
  public ItemRewriter(Protocol protocol, RewriteFunction toClient, RewriteFunction toServer) {
    this.protocol = protocol;
    this.toClient = toClient;
    this.toServer = toServer;
  }
  
  public void registerWindowItems(ClientboundPacketType packetType, final Type<Item[]> type) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            map(type);
            handler(ItemRewriter.this.itemArrayHandler(type));
          }
        });
  }
  
  public void registerSetSlot(ClientboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.BYTE);
            map((Type)Type.SHORT);
            map(type);
            handler(ItemRewriter.this.itemToClientHandler(type));
          }
        });
  }
  
  public void registerEntityEquipment(ClientboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map(type);
            handler(ItemRewriter.this.itemToClientHandler(type));
          }
        });
  }
  
  public void registerEntityEquipmentArray(ClientboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  byte slot;
                  do {
                    slot = ((Byte)wrapper.passthrough(Type.BYTE)).byteValue();
                    ItemRewriter.this.toClient.rewrite((Item)wrapper.passthrough(type));
                  } while ((slot & Byte.MIN_VALUE) != 0);
                });
          }
        });
  }
  
  public void registerCreativeInvAction(ServerboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerIncoming(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.SHORT);
            map(type);
            handler(ItemRewriter.this.itemToServerHandler(type));
          }
        });
  }
  
  public void registerClickWindow(ServerboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerIncoming(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            map((Type)Type.SHORT);
            map(Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.VAR_INT);
            map(type);
            handler(ItemRewriter.this.itemToServerHandler(type));
          }
        });
  }
  
  public void registerSetCooldown(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int itemId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(ItemRewriter.this.protocol.getMappingData().getNewItemId(itemId)));
                });
          }
        });
  }
  
  public void registerTradeList(ClientboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  int size = ((Short)wrapper.passthrough(Type.UNSIGNED_BYTE)).shortValue();
                  for (int i = 0; i < size; i++) {
                    ItemRewriter.this.toClient.rewrite((Item)wrapper.passthrough(type));
                    ItemRewriter.this.toClient.rewrite((Item)wrapper.passthrough(type));
                    if (((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue())
                      ItemRewriter.this.toClient.rewrite((Item)wrapper.passthrough(type)); 
                    wrapper.passthrough(Type.BOOLEAN);
                    wrapper.passthrough(Type.INT);
                    wrapper.passthrough(Type.INT);
                    wrapper.passthrough(Type.INT);
                    wrapper.passthrough(Type.INT);
                    wrapper.passthrough((Type)Type.FLOAT);
                    wrapper.passthrough(Type.INT);
                  } 
                });
          }
        });
  }
  
  public void registerAdvancements(ClientboundPacketType packetType, final Type<Item> type) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough(Type.BOOLEAN);
                  int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  for (int i = 0; i < size; i++) {
                    wrapper.passthrough(Type.STRING);
                    if (((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue())
                      wrapper.passthrough(Type.STRING); 
                    if (((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue()) {
                      wrapper.passthrough(Type.COMPONENT);
                      wrapper.passthrough(Type.COMPONENT);
                      ItemRewriter.this.toClient.rewrite((Item)wrapper.passthrough(type));
                      wrapper.passthrough((Type)Type.VAR_INT);
                      int flags = ((Integer)wrapper.passthrough(Type.INT)).intValue();
                      if ((flags & 0x1) != 0)
                        wrapper.passthrough(Type.STRING); 
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.passthrough((Type)Type.FLOAT);
                    } 
                    wrapper.passthrough(Type.STRING_ARRAY);
                    int arrayLength = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int array = 0; array < arrayLength; array++)
                      wrapper.passthrough(Type.STRING_ARRAY); 
                  } 
                });
          }
        });
  }
  
  public void registerSpawnParticle(ClientboundPacketType packetType, final Type<Item> itemType, final Type<?> coordType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.BOOLEAN);
            map(coordType);
            map(coordType);
            map(coordType);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map(Type.INT);
            handler(wrapper -> {
                  int id = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                  if (id == -1)
                    return; 
                  ParticleMappings mappings = ItemRewriter.this.protocol.getMappingData().getParticleMappings();
                  if (id == mappings.getBlockId() || id == mappings.getFallingDustId()) {
                    int data = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(ItemRewriter.this.protocol.getMappingData().getNewBlockStateId(data)));
                  } else if (id == mappings.getItemId()) {
                    ItemRewriter.this.toClient.rewrite((Item)wrapper.passthrough(itemType));
                  } 
                  int newId = ItemRewriter.this.protocol.getMappingData().getNewParticleId(id);
                  if (newId != id)
                    wrapper.set(Type.INT, 0, Integer.valueOf(newId)); 
                });
          }
        });
  }
  
  public PacketHandler itemArrayHandler(Type<Item[]> type) {
    return wrapper -> {
        Item[] items = (Item[])wrapper.get(type, 0);
        for (Item item : items)
          this.toClient.rewrite(item); 
      };
  }
  
  public PacketHandler itemToClientHandler(Type<Item> type) {
    return wrapper -> this.toClient.rewrite((Item)wrapper.get(type, 0));
  }
  
  public PacketHandler itemToServerHandler(Type<Item> type) {
    return wrapper -> this.toServer.rewrite((Item)wrapper.get(type, 0));
  }
  
  @FunctionalInterface
  public static interface RewriteFunction {
    void rewrite(Item param1Item);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\rewriters\ItemRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */