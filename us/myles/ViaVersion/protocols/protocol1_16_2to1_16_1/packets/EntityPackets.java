package us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.entities.Entity1_16_2Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_14;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.metadata.MetadataRewriter1_16_2To1_16_1;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.storage.EntityTracker1_16_2;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public class EntityPackets {
  public static void register(final Protocol1_16_2To1_16_1 protocol) {
    MetadataRewriter1_16_2To1_16_1 metadataRewriter = (MetadataRewriter1_16_2To1_16_1)protocol.get(MetadataRewriter1_16_2To1_16_1.class);
    metadataRewriter.registerSpawnTrackerWithData((ClientboundPacketType)ClientboundPackets1_16.SPAWN_ENTITY, (EntityType)Entity1_16_2Types.EntityType.FALLING_BLOCK);
    metadataRewriter.registerTracker((ClientboundPacketType)ClientboundPackets1_16.SPAWN_MOB);
    metadataRewriter.registerTracker((ClientboundPacketType)ClientboundPackets1_16.SPAWN_PLAYER, (EntityType)Entity1_16_2Types.EntityType.PLAYER);
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_16.ENTITY_METADATA, Types1_14.METADATA_LIST);
    metadataRewriter.registerEntityDestroy((ClientboundPacketType)ClientboundPackets1_16.DESTROY_ENTITIES);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_16.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            handler(wrapper -> {
                  short gamemode = ((Short)wrapper.read(Type.UNSIGNED_BYTE)).shortValue();
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(((gamemode & 0x8) != 0)));
                  gamemode = (short)(gamemode & 0xFFFFFFF7);
                  wrapper.write(Type.UNSIGNED_BYTE, Short.valueOf(gamemode));
                });
            map(Type.BYTE);
            map(Type.STRING_ARRAY);
            handler(wrapper -> {
                  wrapper.read(Type.NBT);
                  wrapper.write(Type.NBT, protocol.getMappingData().getDimensionRegistry());
                  String dimensionType = (String)wrapper.read(Type.STRING);
                  wrapper.write(Type.NBT, EntityPackets.getDimensionData(dimensionType));
                });
            map(Type.STRING);
            map(Type.LONG);
            map(Type.UNSIGNED_BYTE, (Type)Type.VAR_INT);
            handler(wrapper -> ((EntityTracker1_16_2)wrapper.user().get(EntityTracker1_16_2.class)).addEntity(((Integer)wrapper.get(Type.INT, 0)).intValue(), (EntityType)Entity1_16_2Types.EntityType.PLAYER));
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_16.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  String dimensionType = (String)wrapper.read(Type.STRING);
                  wrapper.write(Type.NBT, EntityPackets.getDimensionData(dimensionType));
                });
          }
        });
  }
  
  public static CompoundTag getDimensionData(String dimensionType) {
    CompoundTag tag = (CompoundTag)Protocol1_16_2To1_16_1.MAPPINGS.getDimensionDataMap().get(dimensionType);
    if (tag == null) {
      Via.getPlatform().getLogger().severe("Could not get dimension data of " + dimensionType);
      throw new NullPointerException("Dimension data for " + dimensionType + " is null!");
    } 
    return tag;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16_2to1_16_1\packets\EntityPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */