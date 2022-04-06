package us.myles.ViaVersion.protocols.protocol1_11to1_10;

import java.util.List;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Pair;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_11Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.remapper.ValueCreator;
import us.myles.ViaVersion.api.remapper.ValueTransformer;
import us.myles.ViaVersion.api.rewriters.MetadataRewriter;
import us.myles.ViaVersion.api.rewriters.SoundRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_9;
import us.myles.ViaVersion.protocols.protocol1_11to1_10.data.PotionColorMapping;
import us.myles.ViaVersion.protocols.protocol1_11to1_10.metadata.MetadataRewriter1_11To1_10;
import us.myles.ViaVersion.protocols.protocol1_11to1_10.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_11to1_10.storage.EntityTracker1_11;
import us.myles.ViaVersion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;

public class Protocol1_11To1_10 extends Protocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3> {
  private static final ValueTransformer<Float, Short> toOldByte = new ValueTransformer<Float, Short>(Type.UNSIGNED_BYTE) {
      public Short transform(PacketWrapper wrapper, Float inputValue) throws Exception {
        return Short.valueOf((short)(int)(inputValue.floatValue() * 16.0F));
      }
    };
  
  public Protocol1_11To1_10() {
    super(ClientboundPackets1_9_3.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9_3.class, ServerboundPackets1_9_3.class);
  }
  
  protected void registerPackets() {
    final MetadataRewriter1_11To1_10 metadataRewriter = new MetadataRewriter1_11To1_10(this);
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
            map(Type.UNSIGNED_BYTE, (Type)Type.VAR_INT);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_9.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    Entity1_11Types.EntityType entType = MetadataRewriter1_11To1_10.rewriteEntityType(type, (List)wrapper.get(Types1_9.METADATA_LIST, 0));
                    if (entType != null) {
                      wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(entType.getId()));
                      ((EntityTracker1_11)wrapper.user().get(EntityTracker1_11.class)).addEntity(entityId, (EntityType)entType);
                      metadataRewriter.handleMetadata(entityId, (List)wrapper.get(Types1_9.METADATA_LIST, 0), wrapper.user());
                    } 
                  }
                });
          }
        });
    (new SoundRewriter(this, this::getNewSoundId)).registerSound((ClientboundPacketType)ClientboundPackets1_9_3.SOUND);
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.COLLECT_ITEM, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            create(new ValueCreator() {
                  public void write(PacketWrapper wrapper) throws Exception {
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(1));
                  }
                });
          }
        });
    metadataRewriter1_11To1_10.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_METADATA, Types1_9.METADATA_LIST);
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_TELEPORT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (Via.getConfig().isHologramPatch()) {
                      EntityTracker1_11 tracker = (EntityTracker1_11)wrapper.user().get(EntityTracker1_11.class);
                      if (tracker.isHologram(entityID)) {
                        Double newValue = (Double)wrapper.get(Type.DOUBLE, 1);
                        newValue = Double.valueOf(newValue.doubleValue() - Via.getConfig().getHologramYOffset());
                        wrapper.set(Type.DOUBLE, 1, newValue);
                      } 
                    } 
                  }
                });
          }
        });
    metadataRewriter1_11To1_10.registerEntityDestroy((ClientboundPacketType)ClientboundPackets1_9_3.DESTROY_ENTITIES);
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.TITLE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (action >= 2)
                      wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(action + 1)); 
                  }
                });
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.BLOCK_ACTION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map(Type.UNSIGNED_BYTE);
            map(Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper actionWrapper) throws Exception {
                    if (Via.getConfig().isPistonAnimationPatch()) {
                      int id = ((Integer)actionWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                      if (id == 33 || id == 29)
                        actionWrapper.cancel(); 
                    } 
                  }
                });
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map(Type.UNSIGNED_BYTE);
            map(Type.NBT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    CompoundTag tag = (CompoundTag)wrapper.get(Type.NBT, 0);
                    if (((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue() == 1)
                      EntityIdRewriter.toClientSpawner(tag); 
                    if (tag.contains("id"))
                      ((StringTag)tag.get("id")).setValue(BlockEntityRewriter.toNewIdentifier((String)tag.get("id").getValue())); 
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
                    wrapper.clearInputBuffer();
                    if (chunk.getBlockEntities() == null)
                      return; 
                    for (CompoundTag tag : chunk.getBlockEntities()) {
                      if (tag.contains("id")) {
                        String identifier = ((StringTag)tag.get("id")).getValue();
                        if (identifier.equals("MobSpawner"))
                          EntityIdRewriter.toClientSpawner(tag); 
                        ((StringTag)tag.get("id")).setValue(BlockEntityRewriter.toNewIdentifier(identifier));
                      } 
                    } 
                  }
                });
          }
        });
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
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9_3.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.POSITION);
            map(Type.INT);
            map(Type.BOOLEAN);
            handler(packetWrapper -> {
                  int effectID = ((Integer)packetWrapper.get(Type.INT, 0)).intValue();
                  if (effectID == 2002) {
                    int data = ((Integer)packetWrapper.get(Type.INT, 1)).intValue();
                    boolean isInstant = false;
                    Pair<Integer, Boolean> newData = PotionColorMapping.getNewData(data);
                    if (newData == null) {
                      Via.getPlatform().getLogger().warning("Received unknown 1.11 -> 1.10.2 potion data (" + data + ")");
                      data = 0;
                    } else {
                      data = ((Integer)newData.getKey()).intValue();
                      isInstant = ((Boolean)newData.getValue()).booleanValue();
                    } 
                    if (isInstant)
                      packetWrapper.set(Type.INT, 0, Integer.valueOf(2007)); 
                    packetWrapper.set(Type.INT, 1, Integer.valueOf(data));
                  } 
                });
          }
        });
    registerIncoming((ServerboundPacketType)ServerboundPackets1_9_3.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.FLOAT, Protocol1_11To1_10.toOldByte);
            map((Type)Type.FLOAT, Protocol1_11To1_10.toOldByte);
            map((Type)Type.FLOAT, Protocol1_11To1_10.toOldByte);
          }
        });
    registerIncoming((ServerboundPacketType)ServerboundPackets1_9_3.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String msg = (String)wrapper.get(Type.STRING, 0);
                    if (msg.length() > 100)
                      wrapper.set(Type.STRING, 0, msg.substring(0, 100)); 
                  }
                });
          }
        });
  }
  
  private int getNewSoundId(int id) {
    if (id == 196)
      return -1; 
    if (id >= 85)
      id += 2; 
    if (id >= 176)
      id++; 
    if (id >= 197)
      id += 8; 
    if (id >= 207)
      id--; 
    if (id >= 279)
      id += 9; 
    if (id >= 296)
      id++; 
    if (id >= 390)
      id += 4; 
    if (id >= 400)
      id += 3; 
    if (id >= 450)
      id++; 
    if (id >= 455)
      id++; 
    if (id >= 470)
      id++; 
    return id;
  }
  
  public void init(UserConnection userConnection) {
    userConnection.put((StoredObject)new EntityTracker1_11(userConnection));
    if (!userConnection.has(ClientWorld.class))
      userConnection.put((StoredObject)new ClientWorld(userConnection)); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_11to1_10\Protocol1_11To1_10.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */