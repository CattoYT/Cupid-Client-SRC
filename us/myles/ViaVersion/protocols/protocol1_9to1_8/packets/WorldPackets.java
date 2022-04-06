package us.myles.ViaVersion.protocols.protocol1_9to1_8.packets;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk1_8;
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
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BulkChunkTranslatorProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.CommandBlockProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.sounds.Effect;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.sounds.SoundEffect;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.ClientChunks;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.PlaceBlockTracker;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.types.Chunk1_9to1_8Type;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class WorldPackets {
  public static void register(Protocol protocol) {
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.UPDATE_SIGN, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.POSITION);
            map(Type.INT);
            map(Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                    id = Effect.getNewId(id);
                    wrapper.set(Type.INT, 0, Integer.valueOf(id));
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                    if (id == 2002) {
                      int data = ((Integer)wrapper.get(Type.INT, 1)).intValue();
                      int newData = ItemRewriter.getNewEffectID(data);
                      wrapper.set(Type.INT, 1, Integer.valueOf(newData));
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.NAMED_SOUND, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String name = (String)wrapper.get(Type.STRING, 0);
                    SoundEffect effect = SoundEffect.getByName(name);
                    int catid = 0;
                    String newname = name;
                    if (effect != null) {
                      catid = effect.getCategory().getId();
                      newname = effect.getNewName();
                    } 
                    wrapper.set(Type.STRING, 0, newname);
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(catid));
                    if (effect != null && effect.isBreaksound()) {
                      EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                      int x = ((Integer)wrapper.passthrough(Type.INT)).intValue();
                      int y = ((Integer)wrapper.passthrough(Type.INT)).intValue();
                      int z = ((Integer)wrapper.passthrough(Type.INT)).intValue();
                      if (tracker.interactedBlockRecently((int)Math.floor(x / 8.0D), (int)Math.floor(y / 8.0D), (int)Math.floor(z / 8.0D)))
                        wrapper.cancel(); 
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientChunks clientChunks = (ClientChunks)wrapper.user().get(ClientChunks.class);
                    Chunk1_9to1_8Type type = new Chunk1_9to1_8Type(clientChunks);
                    Chunk1_8 chunk = (Chunk1_8)wrapper.read((Type)type);
                    if (chunk.isUnloadPacket()) {
                      wrapper.setId(29);
                      wrapper.write(Type.INT, Integer.valueOf(chunk.getX()));
                      wrapper.write(Type.INT, Integer.valueOf(chunk.getZ()));
                      CommandBlockProvider provider = (CommandBlockProvider)Via.getManager().getProviders().get(CommandBlockProvider.class);
                      provider.unloadChunk(wrapper.user(), chunk.getX(), chunk.getZ());
                    } else {
                      wrapper.write((Type)type, chunk);
                    } 
                    wrapper.read(Type.REMAINING_BYTES);
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.MAP_BULK_CHUNK, null, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.cancel();
                    BulkChunkTranslatorProvider provider = (BulkChunkTranslatorProvider)Via.getManager().getProviders().get(BulkChunkTranslatorProvider.class);
                    if (!provider.isPacketLevel())
                      return; 
                    List<Object> list = provider.transformMapChunkBulk(wrapper, (ClientChunks)wrapper.user().get(ClientChunks.class));
                    for (Object obj : list) {
                      if (!(obj instanceof PacketWrapper))
                        throw new IOException("transformMapChunkBulk returned the wrong object type"); 
                      PacketWrapper output = (PacketWrapper)obj;
                      ByteBuf buffer = wrapper.user().getChannel().alloc().buffer();
                      try {
                        output.setId(-1);
                        output.writeToBuffer(buffer);
                        PacketWrapper chunkPacket = new PacketWrapper(33, buffer, wrapper.user());
                        chunkPacket.send(Protocol1_9To1_8.class, false, true);
                      } finally {
                        buffer.release();
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map(Type.UNSIGNED_BYTE);
            map(Type.NBT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue();
                    if (action == 1) {
                      CompoundTag tag = (CompoundTag)wrapper.get(Type.NBT, 0);
                      if (tag != null)
                        if (tag.contains("EntityId")) {
                          String entity = (String)tag.get("EntityId").getValue();
                          CompoundTag spawn = new CompoundTag("SpawnData");
                          spawn.put((Tag)new StringTag("id", entity));
                          tag.put((Tag)spawn);
                        } else {
                          CompoundTag spawn = new CompoundTag("SpawnData");
                          spawn.put((Tag)new StringTag("id", "AreaEffectCloud"));
                          tag.put((Tag)spawn);
                        }  
                    } 
                    if (action == 2) {
                      CommandBlockProvider provider = (CommandBlockProvider)Via.getManager().getProviders().get(CommandBlockProvider.class);
                      provider.addOrUpdateBlock(wrapper.user(), (Position)wrapper.get(Type.POSITION, 0), (CompoundTag)wrapper.get(Type.NBT, 0));
                      wrapper.cancel();
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT);
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.UPDATE_SIGN, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.PLAYER_DIGGING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT, Type.UNSIGNED_BYTE);
            map(Type.POSITION);
            map(Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int status = ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue();
                    if (status == 6)
                      wrapper.cancel(); 
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int status = ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue();
                    if (status == 5 || status == 4 || status == 3) {
                      EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                      if (entityTracker.isBlocking()) {
                        entityTracker.setBlocking(false);
                        entityTracker.setSecondHand(null);
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.USE_ITEM, null, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    wrapper.clearInputBuffer();
                    wrapper.setId(8);
                    wrapper.write(Type.POSITION, new Position(-1, (short)-1, -1));
                    wrapper.write(Type.UNSIGNED_BYTE, Short.valueOf((short)255));
                    Item item = Protocol1_9To1_8.getHandItem(wrapper.user());
                    if (Via.getConfig().isShieldBlocking()) {
                      EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                      if (item != null && Protocol1_9To1_8.isSword(item.getIdentifier())) {
                        if (hand == 0) {
                          if (!tracker.isBlocking()) {
                            tracker.setBlocking(true);
                            Item shield = new Item(442, (byte)1, (short)0, null);
                            tracker.setSecondHand(shield);
                          } 
                          wrapper.cancel();
                        } 
                      } else {
                        tracker.setSecondHand(null);
                        tracker.setBlocking(false);
                      } 
                    } 
                    wrapper.write(Type.ITEM, item);
                    wrapper.write(Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                    wrapper.write(Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                    wrapper.write(Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT, Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT, Type.NOTHING);
            create(new ValueCreator() {
                  public void write(PacketWrapper wrapper) throws Exception {
                    Item item = Protocol1_9To1_8.getHandItem(wrapper.user());
                    wrapper.write(Type.ITEM, item);
                  }
                });
            map(Type.UNSIGNED_BYTE);
            map(Type.UNSIGNED_BYTE);
            map(Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Position position = (Position)wrapper.get(Type.POSITION, 0);
                    PlaceBlockTracker tracker = (PlaceBlockTracker)wrapper.user().get(PlaceBlockTracker.class);
                    if (tracker.getLastPlacedPosition() != null && tracker.getLastPlacedPosition().equals(position) && !tracker.isExpired(50))
                      wrapper.cancel(); 
                    tracker.updateTime();
                    tracker.setLastPlacedPosition(position);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int face = ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue();
                    if (face == 255)
                      return; 
                    Position p = (Position)wrapper.get(Type.POSITION, 0);
                    int x = p.getX();
                    short y = p.getY();
                    int z = p.getZ();
                    switch (face) {
                      case 0:
                        y = (short)(y - 1);
                        break;
                      case 1:
                        y = (short)(y + 1);
                        break;
                      case 2:
                        z--;
                        break;
                      case 3:
                        z++;
                        break;
                      case 4:
                        x--;
                        break;
                      case 5:
                        x++;
                        break;
                    } 
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.addBlockInteraction(new Position(x, y, z));
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    CommandBlockProvider provider = (CommandBlockProvider)Via.getManager().getProviders().get(CommandBlockProvider.class);
                    Position pos = (Position)wrapper.get(Type.POSITION, 0);
                    Optional<CompoundTag> tag = provider.get(wrapper.user(), pos);
                    if (tag.isPresent()) {
                      PacketWrapper updateBlockEntity = new PacketWrapper(9, null, wrapper.user());
                      updateBlockEntity.write(Type.POSITION, pos);
                      updateBlockEntity.write(Type.UNSIGNED_BYTE, Short.valueOf((short)2));
                      updateBlockEntity.write(Type.NBT, tag.get());
                      updateBlockEntity.send(Protocol1_9To1_8.class);
                    } 
                  }
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\packets\WorldPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */