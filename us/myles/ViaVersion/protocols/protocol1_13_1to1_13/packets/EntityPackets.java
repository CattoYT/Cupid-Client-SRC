package us.myles.ViaVersion.protocols.protocol1_13_1to1_13.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.entities.Entity1_13Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_13;
import us.myles.ViaVersion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import us.myles.ViaVersion.protocols.protocol1_13_1to1_13.metadata.MetadataRewriter1_13_1To1_13;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage.EntityTracker1_13;

public class EntityPackets {
  public static void register(final Protocol1_13_1To1_13 protocol) {
    final MetadataRewriter1_13_1To1_13 metadataRewriter = (MetadataRewriter1_13_1To1_13)protocol.get(MetadataRewriter1_13_1To1_13.class);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map(Type.BYTE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    byte type = ((Byte)wrapper.get(Type.BYTE, 0)).byteValue();
                    Entity1_13Types.EntityType entType = Entity1_13Types.getTypeFromId(type, true);
                    if (entType != null) {
                      if (entType.is((EntityType)Entity1_13Types.EntityType.FALLING_BLOCK)) {
                        int data = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                        wrapper.set(Type.INT, 0, Integer.valueOf(protocol.getMappingData().getNewBlockStateId(data)));
                      } 
                      ((EntityTracker1_13)wrapper.user().get(EntityTracker1_13.class)).addEntity(entityId, (EntityType)entType);
                    } 
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
            map(Types1_13.METADATA_LIST);
            handler(metadataRewriter.getTrackerAndRewriter(Types1_13.METADATA_LIST));
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
            map(Types1_13.METADATA_LIST);
            handler(metadataRewriter.getTrackerAndRewriter(Types1_13.METADATA_LIST, (EntityType)Entity1_13Types.EntityType.PLAYER));
          }
        });
    metadataRewriter.registerEntityDestroy((ClientboundPacketType)ClientboundPackets1_13.DESTROY_ENTITIES);
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_13.ENTITY_METADATA, Types1_13.METADATA_LIST);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13_1to1_13\packets\EntityPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */