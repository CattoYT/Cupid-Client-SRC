package us.myles.ViaVersion.protocols.protocol1_14_1to1_14.packets;

import us.myles.ViaVersion.api.entities.Entity1_14Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_14;
import us.myles.ViaVersion.protocols.protocol1_14_1to1_14.Protocol1_14_1To1_14;
import us.myles.ViaVersion.protocols.protocol1_14_1to1_14.metadata.MetadataRewriter1_14_1To1_14;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;

public class EntityPackets {
  public static void register(Protocol1_14_1To1_14 protocol) {
    final MetadataRewriter1_14_1To1_14 metadataRewriter = (MetadataRewriter1_14_1To1_14)protocol.get(MetadataRewriter1_14_1To1_14.class);
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
            map(Types1_14.METADATA_LIST);
            handler(metadataRewriter.getTrackerAndRewriter(Types1_14.METADATA_LIST));
          }
        });
    metadataRewriter.registerEntityDestroy((ClientboundPacketType)ClientboundPackets1_14.DESTROY_ENTITIES);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_14.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Types1_14.METADATA_LIST);
            handler(metadataRewriter.getTrackerAndRewriter(Types1_14.METADATA_LIST, (EntityType)Entity1_14Types.EntityType.PLAYER));
          }
        });
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_14.ENTITY_METADATA, Types1_14.METADATA_LIST);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14_1to1_14\packets\EntityPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */