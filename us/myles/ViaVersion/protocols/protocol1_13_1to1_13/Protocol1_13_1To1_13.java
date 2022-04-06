package us.myles.ViaVersion.protocols.protocol1_13_1to1_13;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.MappingData;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.remapper.ValueTransformer;
import us.myles.ViaVersion.api.rewriters.StatisticsRewriter;
import us.myles.ViaVersion.api.rewriters.TagRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_13_1to1_13.metadata.MetadataRewriter1_13_1To1_13;
import us.myles.ViaVersion.protocols.protocol1_13_1to1_13.packets.EntityPackets;
import us.myles.ViaVersion.protocols.protocol1_13_1to1_13.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_13_1to1_13.packets.WorldPackets;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage.EntityTracker1_13;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

public class Protocol1_13_1To1_13 extends Protocol<ClientboundPackets1_13, ClientboundPackets1_13, ServerboundPackets1_13, ServerboundPackets1_13> {
  public static final MappingData MAPPINGS = new MappingData("1.13", "1.13.2", true);
  
  public Protocol1_13_1To1_13() {
    super(ClientboundPackets1_13.class, ClientboundPackets1_13.class, ServerboundPackets1_13.class, ServerboundPackets1_13.class);
  }
  
  protected void registerPackets() {
    new MetadataRewriter1_13_1To1_13(this);
    EntityPackets.register(this);
    InventoryPackets.register(this);
    WorldPackets.register(this);
    registerIncoming((ServerboundPacketType)ServerboundPackets1_13.TAB_COMPLETE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.STRING, new ValueTransformer<String, String>(Type.STRING) {
                  public String transform(PacketWrapper wrapper, String inputValue) {
                    return inputValue.startsWith("/") ? inputValue.substring(1) : inputValue;
                  }
                });
          }
        });
    registerIncoming((ServerboundPacketType)ServerboundPackets1_13.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            map(Type.FLAT_ITEM);
            map(Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item item = (Item)wrapper.get(Type.FLAT_ITEM, 0);
                    InventoryPackets.toServer(item);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    if (hand == 1)
                      wrapper.cancel(); 
                  }
                });
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.TAB_COMPLETE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int start = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(start + 1));
                    int count = ((Integer)wrapper.get((Type)Type.VAR_INT, 3)).intValue();
                    for (int i = 0; i < count; i++) {
                      wrapper.passthrough(Type.STRING);
                      boolean hasTooltip = ((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue();
                      if (hasTooltip)
                        wrapper.passthrough(Type.STRING); 
                    } 
                  }
                });
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.BOSSBAR, new PacketRemapper() {
          public void registerMap() {
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (action == 0) {
                      wrapper.passthrough(Type.COMPONENT);
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.passthrough((Type)Type.VAR_INT);
                      wrapper.passthrough((Type)Type.VAR_INT);
                      short flags = (short)((Byte)wrapper.read(Type.BYTE)).byteValue();
                      if ((flags & 0x2) != 0)
                        flags = (short)(flags | 0x4); 
                      wrapper.write(Type.UNSIGNED_BYTE, Short.valueOf(flags));
                    } 
                  }
                });
          }
        });
    (new TagRewriter(this, null)).register((ClientboundPacketType)ClientboundPackets1_13.TAGS);
    (new StatisticsRewriter(this, null)).register((ClientboundPacketType)ClientboundPackets1_13.STATISTICS);
  }
  
  public void init(UserConnection userConnection) {
    userConnection.put((StoredObject)new EntityTracker1_13(userConnection));
    if (!userConnection.has(ClientWorld.class))
      userConnection.put((StoredObject)new ClientWorld(userConnection)); 
  }
  
  public MappingData getMappingData() {
    return MAPPINGS;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13_1to1_13\Protocol1_13_1To1_13.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */