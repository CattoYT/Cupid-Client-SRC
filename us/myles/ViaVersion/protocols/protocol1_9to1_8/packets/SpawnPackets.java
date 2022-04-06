package us.myles.ViaVersion.protocols.protocol1_9to1_8.packets;

import java.util.ArrayList;
import java.util.List;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.entities.Entity1_10Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_9;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.remapper.ValueCreator;
import us.myles.ViaVersion.api.remapper.ValueTransformer;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_8;
import us.myles.ViaVersion.api.type.types.version.Types1_9;
import us.myles.ViaVersion.protocols.protocol1_8.ClientboundPackets1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ItemRewriter;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;

public class SpawnPackets {
  public static final ValueTransformer<Integer, Double> toNewDouble = new ValueTransformer<Integer, Double>(Type.DOUBLE) {
      public Double transform(PacketWrapper wrapper, Integer inputValue) {
        return Double.valueOf(inputValue.intValue() / 32.0D);
      }
    };
  
  public static void register(final Protocol1_9To1_8 protocol) {
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            create(new ValueCreator() {
                  public void write(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    wrapper.write(Type.UUID, tracker.getEntityUUID(entityID));
                  }
                });
            map(Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int typeID = ((Byte)wrapper.get(Type.BYTE, 0)).byteValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.getTypeFromId(typeID, true));
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Type.INT);
            create(new ValueCreator() {
                  public void write(PacketWrapper wrapper) throws Exception {
                    int data = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                    short vX = 0, vY = 0, vZ = 0;
                    if (data > 0) {
                      vX = ((Short)wrapper.read((Type)Type.SHORT)).shortValue();
                      vY = ((Short)wrapper.read((Type)Type.SHORT)).shortValue();
                      vZ = ((Short)wrapper.read((Type)Type.SHORT)).shortValue();
                    } 
                    wrapper.write((Type)Type.SHORT, Short.valueOf(vX));
                    wrapper.write((Type)Type.SHORT, Short.valueOf(vY));
                    wrapper.write((Type)Type.SHORT, Short.valueOf(vZ));
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    final int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    final int data = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                    int typeID = ((Byte)wrapper.get(Type.BYTE, 0)).byteValue();
                    if (Entity1_10Types.getTypeFromId(typeID, true) == Entity1_10Types.EntityType.SPLASH_POTION) {
                      PacketWrapper metaPacket = wrapper.create(57, new ValueCreator() {
                            public void write(PacketWrapper wrapper) throws Exception {
                              wrapper.write((Type)Type.VAR_INT, Integer.valueOf(entityID));
                              List<Metadata> meta = new ArrayList<>();
                              Item item = new Item(373, (byte)1, (short)data, null);
                              ItemRewriter.toClient(item);
                              Metadata potion = new Metadata(5, (MetaType)MetaType1_9.Slot, item);
                              meta.add(potion);
                              wrapper.write(Types1_9.METADATA_LIST, meta);
                            }
                          });
                      metaPacket.send(Protocol1_9To1_8.class);
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.SPAWN_EXPERIENCE_ORB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.EntityType.EXPERIENCE_ORB);
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.INT, SpawnPackets.toNewDouble);
            map((Type)Type.SHORT);
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.SPAWN_GLOBAL_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.EntityType.LIGHTNING);
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.INT, SpawnPackets.toNewDouble);
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            create(new ValueCreator() {
                  public void write(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    wrapper.write(Type.UUID, tracker.getEntityUUID(entityID));
                  }
                });
            map(Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int typeID = ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.getTypeFromId(typeID, false));
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_8.METADATA_LIST, Types1_9.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    if (tracker.hasEntity(entityId)) {
                      ((MetadataRewriter1_9To1_8)protocol.get(MetadataRewriter1_9To1_8.class)).handleMetadata(entityId, metadataList, wrapper.user());
                    } else {
                      Via.getPlatform().getLogger().warning("Unable to find entity for metadata, entity ID: " + entityId);
                      metadataList.clear();
                    } 
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.handleMetadata(entityID, metadataList);
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.SPAWN_PAINTING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.EntityType.PAINTING);
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            create(new ValueCreator() {
                  public void write(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    wrapper.write(Type.UUID, tracker.getEntityUUID(entityID));
                  }
                });
            map(Type.STRING);
            map(Type.POSITION);
            map(Type.BYTE);
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.addEntity(entityID, (EntityType)Entity1_10Types.EntityType.PLAYER);
                    tracker.sendMetadataBuffer(entityID);
                  }
                });
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.INT, SpawnPackets.toNewDouble);
            map(Type.BYTE);
            map(Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short item = ((Short)wrapper.read((Type)Type.SHORT)).shortValue();
                    if (item != 0) {
                      PacketWrapper packet = new PacketWrapper(60, null, wrapper.user());
                      packet.write((Type)Type.VAR_INT, wrapper.get((Type)Type.VAR_INT, 0));
                      packet.write((Type)Type.VAR_INT, Integer.valueOf(0));
                      packet.write(Type.ITEM, new Item(item, (byte)1, (short)0, null));
                      try {
                        packet.send(Protocol1_9To1_8.class, true, true);
                      } catch (Exception e) {
                        e.printStackTrace();
                      } 
                    } 
                  }
                });
            map(Types1_8.METADATA_LIST, Types1_9.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    if (tracker.hasEntity(entityId)) {
                      ((MetadataRewriter1_9To1_8)protocol.get(MetadataRewriter1_9To1_8.class)).handleMetadata(entityId, metadataList, wrapper.user());
                    } else {
                      Via.getPlatform().getLogger().warning("Unable to find entity for metadata, entity ID: " + entityId);
                      metadataList.clear();
                    } 
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    List<Metadata> metadataList = (List<Metadata>)wrapper.get(Types1_9.METADATA_LIST, 0);
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.handleMetadata(entityID, metadataList);
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.DESTROY_ENTITIES, new PacketRemapper() {
          public void registerMap() {
            map(Type.VAR_INT_ARRAY_PRIMITIVE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int[] entities = (int[])wrapper.get(Type.VAR_INT_ARRAY_PRIMITIVE, 0);
                    for (int entity : entities)
                      ((EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class)).removeEntity(entity); 
                  }
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\packets\SpawnPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */