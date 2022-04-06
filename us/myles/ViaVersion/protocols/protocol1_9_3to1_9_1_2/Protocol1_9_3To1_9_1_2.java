package us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2;

import java.util.List;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.minecraft.chunks.ChunkSection;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.remapper.ValueTransformer;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.chunks.FakeTileEntity;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.types.Chunk1_9_1_2Type;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.IntTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class Protocol1_9_3To1_9_1_2 extends Protocol<ClientboundPackets1_9, ClientboundPackets1_9_3, ServerboundPackets1_9, ServerboundPackets1_9_3> {
  public static final ValueTransformer<Short, Short> ADJUST_PITCH = new ValueTransformer<Short, Short>(Type.UNSIGNED_BYTE, Type.UNSIGNED_BYTE) {
      public Short transform(PacketWrapper wrapper, Short inputValue) throws Exception {
        return Short.valueOf((short)Math.round(inputValue.shortValue() / 63.5F * 63.0F));
      }
    };
  
  public Protocol1_9_3To1_9_1_2() {
    super(ClientboundPackets1_9.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9.class, ServerboundPackets1_9_3.class);
  }
  
  protected void registerPackets() {
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9.UPDATE_SIGN, null, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Position position = (Position)wrapper.read(Type.POSITION);
                    JsonElement[] lines = new JsonElement[4];
                    for (int i = 0; i < 4; i++)
                      lines[i] = (JsonElement)wrapper.read(Type.COMPONENT); 
                    wrapper.clearInputBuffer();
                    wrapper.setId(9);
                    wrapper.write(Type.POSITION, position);
                    wrapper.write(Type.UNSIGNED_BYTE, Short.valueOf((short)9));
                    CompoundTag tag = new CompoundTag("");
                    tag.put((Tag)new StringTag("id", "Sign"));
                    tag.put((Tag)new IntTag("x", position.getX()));
                    tag.put((Tag)new IntTag("y", position.getY()));
                    tag.put((Tag)new IntTag("z", position.getZ()));
                    for (int j = 0; j < lines.length; j++)
                      tag.put((Tag)new StringTag("Text" + (j + 1), lines[j].toString())); 
                    wrapper.write(Type.NBT, tag);
                  }
                });
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    Chunk1_9_1_2Type type = new Chunk1_9_1_2Type(clientWorld);
                    Chunk chunk = (Chunk)wrapper.passthrough((Type)type);
                    List<CompoundTag> tags = chunk.getBlockEntities();
                    for (int i = 0; i < (chunk.getSections()).length; i++) {
                      ChunkSection section = chunk.getSections()[i];
                      if (section != null)
                        for (int y = 0; y < 16; y++) {
                          for (int z = 0; z < 16; z++) {
                            for (int x = 0; x < 16; x++) {
                              int block = section.getBlockId(x, y, z);
                              if (FakeTileEntity.hasBlock(block))
                                tags.add(FakeTileEntity.getFromBlock(x + (chunk.getX() << 4), y + (i << 4), z + (chunk.getZ() << 4), block)); 
                            } 
                          } 
                        }  
                    } 
                    wrapper.write(Type.NBT_ARRAY, chunk.getBlockEntities().toArray((Object[])new CompoundTag[0]));
                  }
                });
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9.JOIN_GAME, new PacketRemapper() {
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
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9.RESPAWN, new PacketRemapper() {
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
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9.SOUND, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map(Type.INT);
            map(Type.INT);
            map(Type.INT);
            map((Type)Type.FLOAT);
            map(Protocol1_9_3To1_9_1_2.ADJUST_PITCH);
          }
        });
  }
  
  public void init(UserConnection user) {
    if (!user.has(ClientWorld.class))
      user.put((StoredObject)new ClientWorld(user)); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9_3to1_9_1_2\Protocol1_9_3To1_9_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */