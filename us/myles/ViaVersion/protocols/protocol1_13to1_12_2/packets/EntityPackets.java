package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.entities.Entity1_13Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_12;
import us.myles.ViaVersion.api.type.types.version.Types1_13;
import us.myles.ViaVersion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.metadata.MetadataRewriter1_13To1_12_2;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage.EntityTracker1_13;

public class EntityPackets {
  public static void register(Protocol1_13To1_12_2 protocol) {
    final MetadataRewriter1_13To1_12_2 metadataRewriter = (MetadataRewriter1_13To1_12_2)protocol.get(MetadataRewriter1_13To1_12_2.class);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_12_1.SPAWN_ENTITY, new PacketRemapper() {
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
                        int oldId = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                        int combined = (oldId & 0xFFF) << 4 | oldId >> 12 & 0xF;
                        wrapper.set(Type.INT, 0, Integer.valueOf(WorldPackets.toNewId(combined)));
                      } 
                      if (entType.is((EntityType)Entity1_13Types.EntityType.ITEM_FRAME)) {
                        int data = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                        switch (data) {
                          case 0:
                            data = 3;
                            break;
                          case 1:
                            data = 4;
                            break;
                          case 3:
                            data = 5;
                            break;
                        } 
                        wrapper.set(Type.INT, 0, Integer.valueOf(data));
                        ((EntityTracker1_13)wrapper.user().get(EntityTracker1_13.class)).addEntity(entityId, (EntityType)entType);
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_12_1.SPAWN_MOB, new PacketRemapper() {
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
            map(Types1_12.METADATA_LIST, Types1_13.METADATA_LIST);
            handler(metadataRewriter.getTrackerAndRewriter(Types1_13.METADATA_LIST));
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_12_1.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Types1_12.METADATA_LIST, Types1_13.METADATA_LIST);
            handler(metadataRewriter.getTrackerAndRewriter(Types1_13.METADATA_LIST, (EntityType)Entity1_13Types.EntityType.PLAYER));
          }
        });
    metadataRewriter.registerEntityDestroy((ClientboundPacketType)ClientboundPackets1_12_1.DESTROY_ENTITIES);
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_12_1.ENTITY_METADATA, Types1_12.METADATA_LIST, Types1_13.METADATA_LIST);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\packets\EntityPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */