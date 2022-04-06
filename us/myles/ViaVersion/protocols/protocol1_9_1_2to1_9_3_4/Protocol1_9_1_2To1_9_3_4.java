package us.myles.ViaVersion.protocols.protocol1_9_1_2to1_9_3_4;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_9_1_2to1_9_3_4.chunks.BlockEntity;
import us.myles.ViaVersion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.types.Chunk1_9_1_2Type;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public class Protocol1_9_1_2To1_9_3_4 extends Protocol<ClientboundPackets1_9_3, ClientboundPackets1_9, ServerboundPackets1_9_3, ServerboundPackets1_9> {
  public Protocol1_9_1_2To1_9_3_4() {
    super(ClientboundPackets1_9_3.class, ClientboundPackets1_9.class, ServerboundPackets1_9_3.class, ServerboundPackets1_9.class);
  }
  
  protected void registerPackets() {
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map(Type.UNSIGNED_BYTE);
            map(Type.NBT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue() == 9) {
                      Position position = (Position)wrapper.get(Type.POSITION, 0);
                      CompoundTag tag = (CompoundTag)wrapper.get(Type.NBT, 0);
                      wrapper.clearPacket();
                      wrapper.setId(ClientboundPackets1_9.UPDATE_SIGN.ordinal());
                      wrapper.write(Type.POSITION, position);
                      for (int i = 1; i < 5; i++)
                        wrapper.write(Type.STRING, tag.get("Text" + i).getValue()); 
                    } 
                  }
                });
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    Chunk1_9_3_4Type newType = new Chunk1_9_3_4Type(clientWorld);
                    Chunk1_9_1_2Type oldType = new Chunk1_9_1_2Type(clientWorld);
                    Chunk chunk = (Chunk)wrapper.read((Type)newType);
                    wrapper.write((Type)oldType, chunk);
                    BlockEntity.handle(chunk.getBlockEntities(), wrapper.user());
                  }
                });
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.UNSIGNED_BYTE);
            map(Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get(Type.INT, 1)).intValue();
                    clientChunks.setEnvironment(dimensionId);
                  }
                });
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                    clientWorld.setEnvironment(dimensionId);
                  }
                });
          }
        });
  }
  
  public void init(UserConnection userConnection) {
    if (!userConnection.has(ClientWorld.class))
      userConnection.put((StoredObject)new ClientWorld(userConnection)); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9_1_2to1_9_3_4\Protocol1_9_1_2To1_9_3_4.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */