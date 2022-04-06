package us.myles.ViaVersion.protocols.protocol1_10to1_9_3;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.remapper.ValueTransformer;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_9;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.protocol1_10to1_9_3.storage.ResourcePackTracker;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;

public class Protocol1_10To1_9_3_4 extends Protocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3> {
  public static final ValueTransformer<Short, Float> TO_NEW_PITCH = new ValueTransformer<Short, Float>((Type)Type.FLOAT) {
      public Float transform(PacketWrapper wrapper, Short inputValue) throws Exception {
        return Float.valueOf(inputValue.shortValue() / 63.0F);
      }
    };
  
  public static final ValueTransformer<List<Metadata>, List<Metadata>> TRANSFORM_METADATA = new ValueTransformer<List<Metadata>, List<Metadata>>(Types1_9.METADATA_LIST) {
      public List<Metadata> transform(PacketWrapper wrapper, List<Metadata> inputValue) throws Exception {
        List<Metadata> metaList = new CopyOnWriteArrayList<>(inputValue);
        for (Metadata m : metaList) {
          if (m.getId() >= 5)
            m.setId(m.getId() + 1); 
        } 
        return metaList;
      }
    };
  
  public Protocol1_10To1_9_3_4() {
    super(ClientboundPackets1_9_3.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9_3.class, ServerboundPackets1_9_3.class);
  }
  
  protected void registerPackets() {
    registerOutgoing(State.PLAY, 25, 25, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map((Type)Type.VAR_INT);
            map(Type.INT);
            map(Type.INT);
            map(Type.INT);
            map((Type)Type.FLOAT);
            map(Type.UNSIGNED_BYTE, Protocol1_10To1_9_3_4.TO_NEW_PITCH);
          }
        });
    registerOutgoing(State.PLAY, 70, 70, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map(Type.INT);
            map(Type.INT);
            map(Type.INT);
            map((Type)Type.FLOAT);
            map(Type.UNSIGNED_BYTE, Protocol1_10To1_9_3_4.TO_NEW_PITCH);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(Protocol1_10To1_9_3_4.this.getNewSoundId(id)));
                  }
                });
          }
        });
    registerOutgoing(State.PLAY, 57, 57, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Types1_9.METADATA_LIST, Protocol1_10To1_9_3_4.TRANSFORM_METADATA);
          }
        });
    registerOutgoing(State.PLAY, 3, 3, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map(Type.UNSIGNED_BYTE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_9.METADATA_LIST, Protocol1_10To1_9_3_4.TRANSFORM_METADATA);
          }
        });
    registerOutgoing(State.PLAY, 5, 5, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Types1_9.METADATA_LIST, Protocol1_10To1_9_3_4.TRANSFORM_METADATA);
          }
        });
    registerOutgoing(State.PLAY, 50, 50, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ResourcePackTracker tracker = (ResourcePackTracker)wrapper.user().get(ResourcePackTracker.class);
                    tracker.setLastHash((String)wrapper.get(Type.STRING, 1));
                  }
                });
          }
        });
    registerIncoming(State.PLAY, 22, 22, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ResourcePackTracker tracker = (ResourcePackTracker)wrapper.user().get(ResourcePackTracker.class);
                    wrapper.write(Type.STRING, tracker.getLastHash());
                    wrapper.write((Type)Type.VAR_INT, wrapper.read((Type)Type.VAR_INT));
                  }
                });
          }
        });
  }
  
  public int getNewSoundId(int id) {
    int newId = id;
    if (id >= 24)
      newId++; 
    if (id >= 248)
      newId += 4; 
    if (id >= 296)
      newId += 6; 
    if (id >= 354)
      newId += 4; 
    if (id >= 372)
      newId += 4; 
    return newId;
  }
  
  public void init(UserConnection userConnection) {
    userConnection.put((StoredObject)new ResourcePackTracker(userConnection));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_10to1_9_3\Protocol1_10To1_9_3_4.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */