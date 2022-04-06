package us.myles.ViaVersion.protocols.protocol1_14to1_13_2.packets;

import java.util.LinkedList;
import java.util.List;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.entities.Entity1_13Types;
import us.myles.ViaVersion.api.entities.Entity1_14Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_14;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_13_2;
import us.myles.ViaVersion.api.type.types.version.Types1_14;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.metadata.MetadataRewriter1_14To1_13_2;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;

public class EntityPackets {
  public static void register(final Protocol1_14To1_13_2 protocol) {
    final MetadataRewriter1_14To1_13_2 metadataRewriter = (MetadataRewriter1_14To1_13_2)protocol.get(MetadataRewriter1_14To1_13_2.class);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map(Type.BYTE, (Type)Type.VAR_INT);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Type.INT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int typeId = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    Entity1_13Types.EntityType type1_13 = Entity1_13Types.getTypeFromId(typeId, true);
                    typeId = metadataRewriter.getNewEntityId(type1_13.getId());
                    Entity1_14Types.EntityType type1_14 = Entity1_14Types.getTypeFromId(typeId);
                    if (type1_14 != null) {
                      int data = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                      if (type1_14.is((EntityType)Entity1_14Types.EntityType.FALLING_BLOCK)) {
                        wrapper.set(Type.INT, 0, Integer.valueOf(protocol.getMappingData().getNewBlockStateId(data)));
                      } else if (type1_14.is((EntityType)Entity1_14Types.EntityType.MINECART)) {
                        switch (data) {
                          case 1:
                            typeId = Entity1_14Types.EntityType.CHEST_MINECART.getId();
                            break;
                          case 2:
                            typeId = Entity1_14Types.EntityType.FURNACE_MINECART.getId();
                            break;
                          case 3:
                            typeId = Entity1_14Types.EntityType.TNT_MINECART.getId();
                            break;
                          case 4:
                            typeId = Entity1_14Types.EntityType.SPAWNER_MINECART.getId();
                            break;
                          case 5:
                            typeId = Entity1_14Types.EntityType.HOPPER_MINECART.getId();
                            break;
                          case 6:
                            typeId = Entity1_14Types.EntityType.COMMAND_BLOCK_MINECART.getId();
                            break;
                        } 
                      } else if ((type1_14.is((EntityType)Entity1_14Types.EntityType.ITEM) && data > 0) || type1_14
                        .isOrHasParent((EntityType)Entity1_14Types.EntityType.ABSTRACT_ARROW)) {
                        if (type1_14.isOrHasParent((EntityType)Entity1_14Types.EntityType.ABSTRACT_ARROW))
                          wrapper.set(Type.INT, 0, Integer.valueOf(data - 1)); 
                        PacketWrapper velocity = wrapper.create(69);
                        velocity.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
                        velocity.write((Type)Type.SHORT, wrapper.get((Type)Type.SHORT, 0));
                        velocity.write((Type)Type.SHORT, wrapper.get((Type)Type.SHORT, 1));
                        velocity.write((Type)Type.SHORT, wrapper.get((Type)Type.SHORT, 2));
                        velocity.send(Protocol1_14To1_13_2.class);
                      } 
                      ((EntityTracker1_14)wrapper.user().get(EntityTracker1_14.class)).addEntity(entityId, (EntityType)type1_14);
                    } 
                    wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(typeId));
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.SPAWN_MOB, new PacketRemapper() {
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
            map(Types1_13_2.METADATA_LIST, Types1_14.METADATA_LIST);
            handler(metadataRewriter.getTrackerAndRewriter(Types1_14.METADATA_LIST));
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PAINTING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            map(Type.POSITION, Type.POSITION1_14);
            map(Type.BYTE);
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Types1_13_2.METADATA_LIST, Types1_14.METADATA_LIST);
            handler(metadataRewriter.getTrackerAndRewriter(Types1_14.METADATA_LIST, (EntityType)Entity1_14Types.EntityType.PLAYER));
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.ENTITY_ANIMATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short animation = ((Short)wrapper.passthrough(Type.UNSIGNED_BYTE)).shortValue();
                    if (animation == 2) {
                      EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().get(EntityTracker1_14.class);
                      int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                      tracker.setSleeping(entityId, false);
                      PacketWrapper metadataPacket = wrapper.create(67);
                      metadataPacket.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
                      List<Metadata> metadataList = new LinkedList<>();
                      if (tracker.getClientEntityId() != entityId)
                        metadataList.add(new Metadata(6, (MetaType)MetaType1_14.Pose, Integer.valueOf(MetadataRewriter1_14To1_13_2.recalculatePlayerPose(entityId, tracker)))); 
                      metadataList.add(new Metadata(12, (MetaType)MetaType1_14.OptPosition, null));
                      metadataPacket.write(Types1_14.METADATA_LIST, metadataList);
                      metadataPacket.send(Protocol1_14To1_13_2.class);
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.USE_BED, (ClientboundPacketType)ClientboundPackets1_14.ENTITY_METADATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().get(EntityTracker1_14.class);
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    tracker.setSleeping(entityId, true);
                    Position position = (Position)wrapper.read(Type.POSITION);
                    List<Metadata> metadataList = new LinkedList<>();
                    metadataList.add(new Metadata(12, (MetaType)MetaType1_14.OptPosition, position));
                    if (tracker.getClientEntityId() != entityId)
                      metadataList.add(new Metadata(6, (MetaType)MetaType1_14.Pose, Integer.valueOf(MetadataRewriter1_14To1_13_2.recalculatePlayerPose(entityId, tracker)))); 
                    wrapper.write(Types1_14.METADATA_LIST, metadataList);
                  }
                });
          }
        });
    metadataRewriter.registerEntityDestroy((ClientboundPacketType)ClientboundPackets1_13.DESTROY_ENTITIES);
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_13.ENTITY_METADATA, Types1_13_2.METADATA_LIST, Types1_14.METADATA_LIST);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14to1_13_2\packets\EntityPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */