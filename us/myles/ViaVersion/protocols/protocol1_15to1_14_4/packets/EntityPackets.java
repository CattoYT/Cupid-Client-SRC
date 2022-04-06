package us.myles.ViaVersion.protocols.protocol1_15to1_14_4.packets;

import java.util.List;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.entities.Entity1_15Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_14;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.metadata.MetadataRewriter1_15To1_14_4;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.storage.EntityTracker1_15;

public class EntityPackets {
  public static void register(Protocol1_15To1_14_4 protocol) {
    final MetadataRewriter1_15To1_14_4 metadataRewriter = (MetadataRewriter1_15To1_14_4)protocol.get(MetadataRewriter1_15To1_14_4.class);
    metadataRewriter.registerSpawnTrackerWithData((ClientboundPacketType)ClientboundPackets1_14.SPAWN_ENTITY, (EntityType)Entity1_15Types.EntityType.FALLING_BLOCK);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_14.SPAWN_MOB, new PacketRemapper() {
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
            handler(metadataRewriter.getTracker());
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  List<Metadata> metadata = (List<Metadata>)wrapper.read(Types1_14.METADATA_LIST);
                  metadataRewriter.handleMetadata(entityId, metadata, wrapper.user());
                  PacketWrapper metadataUpdate = wrapper.create(68);
                  metadataUpdate.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
                  metadataUpdate.write(Types1_14.METADATA_LIST, metadata);
                  metadataUpdate.send(Protocol1_15To1_14_4.class);
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_14.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  Entity1_15Types.EntityType entityType = Entity1_15Types.EntityType.PLAYER;
                  ((EntityTracker1_15)wrapper.user().get(EntityTracker1_15.class)).addEntity(entityId, (EntityType)entityType);
                  List<Metadata> metadata = (List<Metadata>)wrapper.read(Types1_14.METADATA_LIST);
                  metadataRewriter.handleMetadata(entityId, metadata, wrapper.user());
                  PacketWrapper metadataUpdate = wrapper.create(68);
                  metadataUpdate.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
                  metadataUpdate.write(Types1_14.METADATA_LIST, metadata);
                  metadataUpdate.send(Protocol1_15To1_14_4.class);
                });
          }
        });
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_14.ENTITY_METADATA, Types1_14.METADATA_LIST);
    metadataRewriter.registerEntityDestroy((ClientboundPacketType)ClientboundPackets1_14.DESTROY_ENTITIES);
  }
  
  public static int getNewEntityId(int oldId) {
    return (oldId >= 4) ? (oldId + 1) : oldId;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_15to1_14_4\packets\EntityPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */