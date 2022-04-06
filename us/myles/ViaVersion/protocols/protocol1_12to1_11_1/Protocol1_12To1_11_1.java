package us.myles.ViaVersion.protocols.protocol1_12to1_11_1;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.minecraft.chunks.ChunkSection;
import us.myles.ViaVersion.api.platform.providers.Provider;
import us.myles.ViaVersion.api.platform.providers.ViaProviders;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.MetadataRewriter;
import us.myles.ViaVersion.api.rewriters.SoundRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_12;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.metadata.MetadataRewriter1_12To1_11_1;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.providers.InventoryQuickMoveProvider;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.storage.EntityTracker1_12;
import us.myles.ViaVersion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.IntTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class Protocol1_12To1_11_1 extends Protocol<ClientboundPackets1_9_3, ClientboundPackets1_12, ServerboundPackets1_9_3, ServerboundPackets1_12> {
  public Protocol1_12To1_11_1() {
    super(ClientboundPackets1_9_3.class, ClientboundPackets1_12.class, ServerboundPackets1_9_3.class, ServerboundPackets1_12.class);
  }
  
  protected void registerPackets() {
    final MetadataRewriter1_12To1_11_1 metadataRewriter = new MetadataRewriter1_12To1_11_1(this);
    InventoryPackets.register(this);
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map(Type.BYTE);
            handler(metadataRewriter.getObjectTracker());
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_12.METADATA_LIST);
            handler(metadataRewriter.getTrackerAndRewriter(Types1_12.METADATA_LIST));
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (!Via.getConfig().is1_12NBTArrayFix())
                      return; 
                    try {
                      JsonElement obj = (JsonElement)Protocol1_9To1_8.FIX_JSON.transform(null, ((JsonElement)wrapper.passthrough(Type.COMPONENT)).toString());
                      TranslateRewriter.toClient(obj, wrapper.user());
                      ChatItemRewriter.toClient(obj, wrapper.user());
                      wrapper.set(Type.COMPONENT, 0, obj);
                    } catch (Exception e) {
                      e.printStackTrace();
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
                    Chunk1_9_3_4Type type = new Chunk1_9_3_4Type(clientWorld);
                    Chunk chunk = (Chunk)wrapper.passthrough((Type)type);
                    for (int i = 0; i < (chunk.getSections()).length; i++) {
                      ChunkSection section = chunk.getSections()[i];
                      if (section != null)
                        for (int y = 0; y < 16; y++) {
                          for (int z = 0; z < 16; z++) {
                            for (int x = 0; x < 16; x++) {
                              int block = section.getBlockId(x, y, z);
                              if (block == 26) {
                                CompoundTag tag = new CompoundTag("");
                                tag.put((Tag)new IntTag("color", 14));
                                tag.put((Tag)new IntTag("x", x + (chunk.getX() << 4)));
                                tag.put((Tag)new IntTag("y", y + (i << 4)));
                                tag.put((Tag)new IntTag("z", z + (chunk.getZ() << 4)));
                                tag.put((Tag)new StringTag("id", "minecraft:bed"));
                                chunk.getBlockEntities().add(tag);
                              } 
                            } 
                          } 
                        }  
                    } 
                  }
                });
          }
        });
    metadataRewriter1_12To1_11_1.registerEntityDestroy((ClientboundPacketType)ClientboundPackets1_9_3.DESTROY_ENTITIES);
    metadataRewriter1_12To1_11_1.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_METADATA, Types1_12.METADATA_LIST);
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.UNSIGNED_BYTE);
            map(Type.INT);
            handler(wrapper -> {
                  ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
                  int dimensionId = ((Integer)wrapper.get(Type.INT, 1)).intValue();
                  clientChunks.setEnvironment(dimensionId);
                });
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            handler(wrapper -> {
                  ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                  int dimensionId = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                  clientWorld.setEnvironment(dimensionId);
                });
          }
        });
    (new SoundRewriter(this, this::getNewSoundId)).registerSound((ClientboundPacketType)ClientboundPackets1_9_3.SOUND);
    cancelIncoming(ServerboundPackets1_12.PREPARE_CRAFTING_GRID);
    registerIncoming(ServerboundPackets1_12.CLIENT_SETTINGS, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map(Type.BYTE);
            map((Type)Type.VAR_INT);
            map(Type.BOOLEAN);
            map(Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String locale = (String)wrapper.get(Type.STRING, 0);
                    if (locale.length() > 7)
                      wrapper.set(Type.STRING, 0, locale.substring(0, 7)); 
                  }
                });
          }
        });
    cancelIncoming(ServerboundPackets1_12.RECIPE_BOOK_DATA);
    cancelIncoming(ServerboundPackets1_12.ADVANCEMENT_TAB);
  }
  
  private int getNewSoundId(int id) {
    int newId = id;
    if (id >= 26)
      newId += 2; 
    if (id >= 70)
      newId += 4; 
    if (id >= 74)
      newId++; 
    if (id >= 143)
      newId += 3; 
    if (id >= 185)
      newId++; 
    if (id >= 263)
      newId += 7; 
    if (id >= 301)
      newId += 33; 
    if (id >= 317)
      newId += 2; 
    if (id >= 491)
      newId += 3; 
    return newId;
  }
  
  protected void register(ViaProviders providers) {
    providers.register(InventoryQuickMoveProvider.class, (Provider)new InventoryQuickMoveProvider());
  }
  
  public void init(UserConnection userConnection) {
    userConnection.put((StoredObject)new EntityTracker1_12(userConnection));
    if (!userConnection.has(ClientWorld.class))
      userConnection.put((StoredObject)new ClientWorld(userConnection)); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_12to1_11_1\Protocol1_12To1_11_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */