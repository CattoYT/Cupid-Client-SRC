package us.myles.ViaVersion.protocols.protocol1_16to1_15_2.packets;

import java.util.UUID;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.entities.Entity1_16Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_14;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data.MappingData;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.metadata.MetadataRewriter1_16To1_15_2;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.storage.EntityTracker1_16;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.storage.InventoryTracker1_16;
import us.myles.viaversion.libs.opennbt.tag.builtin.ByteTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.FloatTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.IntTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ListTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.LongTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class EntityPackets {
  private static final PacketHandler DIMENSION_HANDLER;
  
  static {
    DIMENSION_HANDLER = (wrapper -> {
        String dimensionName;
        int dimension = ((Integer)wrapper.read(Type.INT)).intValue();
        switch (dimension) {
          case -1:
            dimensionName = "minecraft:the_nether";
            break;
          case 0:
            dimensionName = "minecraft:overworld";
            break;
          case 1:
            dimensionName = "minecraft:the_end";
            break;
          default:
            Via.getPlatform().getLogger().warning("Invalid dimension id: " + dimension);
            dimensionName = "minecraft:overworld";
            break;
        } 
        wrapper.write(Type.STRING, dimensionName);
        wrapper.write(Type.STRING, dimensionName);
      });
  }
  
  public static final CompoundTag DIMENSIONS_TAG = new CompoundTag("");
  
  private static final String[] WORLD_NAMES = new String[] { "minecraft:overworld", "minecraft:the_nether", "minecraft:the_end" };
  
  static {
    ListTag list = new ListTag("dimension", CompoundTag.class);
    list.add((Tag)createOverworldEntry());
    list.add((Tag)createOverworldCavesEntry());
    list.add((Tag)createNetherEntry());
    list.add((Tag)createEndEntry());
    DIMENSIONS_TAG.put((Tag)list);
  }
  
  private static CompoundTag createOverworldEntry() {
    CompoundTag tag = new CompoundTag("");
    tag.put((Tag)new StringTag("name", "minecraft:overworld"));
    tag.put((Tag)new ByteTag("has_ceiling", (byte)0));
    addSharedOverwaldEntries(tag);
    return tag;
  }
  
  private static CompoundTag createOverworldCavesEntry() {
    CompoundTag tag = new CompoundTag("");
    tag.put((Tag)new StringTag("name", "minecraft:overworld_caves"));
    tag.put((Tag)new ByteTag("has_ceiling", (byte)1));
    addSharedOverwaldEntries(tag);
    return tag;
  }
  
  private static void addSharedOverwaldEntries(CompoundTag tag) {
    tag.put((Tag)new ByteTag("piglin_safe", (byte)0));
    tag.put((Tag)new ByteTag("natural", (byte)1));
    tag.put((Tag)new FloatTag("ambient_light", 0.0F));
    tag.put((Tag)new StringTag("infiniburn", "minecraft:infiniburn_overworld"));
    tag.put((Tag)new ByteTag("respawn_anchor_works", (byte)0));
    tag.put((Tag)new ByteTag("has_skylight", (byte)1));
    tag.put((Tag)new ByteTag("bed_works", (byte)1));
    tag.put((Tag)new ByteTag("has_raids", (byte)1));
    tag.put((Tag)new IntTag("logical_height", 256));
    tag.put((Tag)new ByteTag("shrunk", (byte)0));
    tag.put((Tag)new ByteTag("ultrawarm", (byte)0));
  }
  
  private static CompoundTag createNetherEntry() {
    CompoundTag tag = new CompoundTag("");
    tag.put((Tag)new ByteTag("piglin_safe", (byte)1));
    tag.put((Tag)new ByteTag("natural", (byte)0));
    tag.put((Tag)new FloatTag("ambient_light", 0.1F));
    tag.put((Tag)new StringTag("infiniburn", "minecraft:infiniburn_nether"));
    tag.put((Tag)new ByteTag("respawn_anchor_works", (byte)1));
    tag.put((Tag)new ByteTag("has_skylight", (byte)0));
    tag.put((Tag)new ByteTag("bed_works", (byte)0));
    tag.put((Tag)new LongTag("fixed_time", 18000L));
    tag.put((Tag)new ByteTag("has_raids", (byte)0));
    tag.put((Tag)new StringTag("name", "minecraft:the_nether"));
    tag.put((Tag)new IntTag("logical_height", 128));
    tag.put((Tag)new ByteTag("shrunk", (byte)1));
    tag.put((Tag)new ByteTag("ultrawarm", (byte)1));
    tag.put((Tag)new ByteTag("has_ceiling", (byte)1));
    return tag;
  }
  
  private static CompoundTag createEndEntry() {
    CompoundTag tag = new CompoundTag("");
    tag.put((Tag)new ByteTag("piglin_safe", (byte)0));
    tag.put((Tag)new ByteTag("natural", (byte)0));
    tag.put((Tag)new FloatTag("ambient_light", 0.0F));
    tag.put((Tag)new StringTag("infiniburn", "minecraft:infiniburn_end"));
    tag.put((Tag)new ByteTag("respawn_anchor_works", (byte)0));
    tag.put((Tag)new ByteTag("has_skylight", (byte)0));
    tag.put((Tag)new ByteTag("bed_works", (byte)0));
    tag.put((Tag)new LongTag("fixed_time", 6000L));
    tag.put((Tag)new ByteTag("has_raids", (byte)1));
    tag.put((Tag)new StringTag("name", "minecraft:the_end"));
    tag.put((Tag)new IntTag("logical_height", 256));
    tag.put((Tag)new ByteTag("shrunk", (byte)0));
    tag.put((Tag)new ByteTag("ultrawarm", (byte)0));
    tag.put((Tag)new ByteTag("has_ceiling", (byte)0));
    return tag;
  }
  
  public static void register(final Protocol1_16To1_15_2 protocol) {
    MetadataRewriter1_16To1_15_2 metadataRewriter = (MetadataRewriter1_16To1_15_2)protocol.get(MetadataRewriter1_16To1_15_2.class);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_15.SPAWN_GLOBAL_ENTITY, (ClientboundPacketType)ClientboundPackets1_16.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  ((EntityTracker1_16)wrapper.user().get(EntityTracker1_16.class)).addEntity(entityId, (EntityType)Entity1_16Types.EntityType.LIGHTNING_BOLT);
                  wrapper.write(Type.UUID, UUID.randomUUID());
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(Entity1_16Types.EntityType.LIGHTNING_BOLT.getId()));
                  wrapper.read(Type.BYTE);
                  wrapper.passthrough(Type.DOUBLE);
                  wrapper.passthrough(Type.DOUBLE);
                  wrapper.passthrough(Type.DOUBLE);
                  wrapper.write(Type.BYTE, Byte.valueOf((byte)0));
                  wrapper.write(Type.BYTE, Byte.valueOf((byte)0));
                  wrapper.write(Type.INT, Integer.valueOf(0));
                  wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
                  wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
                  wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
                });
          }
        });
    metadataRewriter.registerSpawnTrackerWithData((ClientboundPacketType)ClientboundPackets1_15.SPAWN_ENTITY, (EntityType)Entity1_16Types.EntityType.FALLING_BLOCK);
    metadataRewriter.registerTracker((ClientboundPacketType)ClientboundPackets1_15.SPAWN_MOB);
    metadataRewriter.registerTracker((ClientboundPacketType)ClientboundPackets1_15.SPAWN_PLAYER, (EntityType)Entity1_16Types.EntityType.PLAYER);
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_15.ENTITY_METADATA, Types1_14.METADATA_LIST);
    metadataRewriter.registerEntityDestroy((ClientboundPacketType)ClientboundPackets1_15.DESTROY_ENTITIES);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_15.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            handler(EntityPackets.DIMENSION_HANDLER);
            map(Type.LONG);
            map(Type.UNSIGNED_BYTE);
            handler(wrapper -> {
                  wrapper.write(Type.BYTE, Byte.valueOf((byte)-1));
                  String levelType = (String)wrapper.read(Type.STRING);
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(false));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(levelType.equals("flat")));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(true));
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_15.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.UNSIGNED_BYTE);
            handler(wrapper -> {
                  wrapper.write(Type.BYTE, Byte.valueOf((byte)-1));
                  wrapper.write(Type.STRING_ARRAY, EntityPackets.WORLD_NAMES);
                  wrapper.write(Type.NBT, EntityPackets.DIMENSIONS_TAG);
                });
            handler(EntityPackets.DIMENSION_HANDLER);
            map(Type.LONG);
            map(Type.UNSIGNED_BYTE);
            handler(wrapper -> {
                  ((EntityTracker1_16)wrapper.user().get(EntityTracker1_16.class)).addEntity(((Integer)wrapper.get(Type.INT, 0)).intValue(), (EntityType)Entity1_16Types.EntityType.PLAYER);
                  String type = (String)wrapper.read(Type.STRING);
                  wrapper.passthrough((Type)Type.VAR_INT);
                  wrapper.passthrough(Type.BOOLEAN);
                  wrapper.passthrough(Type.BOOLEAN);
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(false));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(type.equals("flat")));
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_15.ENTITY_PROPERTIES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  int size = ((Integer)wrapper.passthrough(Type.INT)).intValue();
                  int actualSize = size;
                  for (int i = 0; i < size; i++) {
                    String key = (String)wrapper.read(Type.STRING);
                    String attributeIdentifier = (String)protocol.getMappingData().getAttributeMappings().get(key);
                    if (attributeIdentifier == null) {
                      attributeIdentifier = "minecraft:" + key;
                      if (!MappingData.isValid1_13Channel(attributeIdentifier)) {
                        if (!Via.getConfig().isSuppressConversionWarnings())
                          Via.getPlatform().getLogger().warning("Invalid attribute: " + key); 
                        actualSize--;
                        wrapper.read(Type.DOUBLE);
                        int k = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                        for (int m = 0; m < k; m++) {
                          wrapper.read(Type.UUID);
                          wrapper.read(Type.DOUBLE);
                          wrapper.read(Type.BYTE);
                        } 
                        continue;
                      } 
                    } 
                    wrapper.write(Type.STRING, attributeIdentifier);
                    wrapper.passthrough(Type.DOUBLE);
                    int modifierSize = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int j = 0; j < modifierSize; j++) {
                      wrapper.passthrough(Type.UUID);
                      wrapper.passthrough(Type.DOUBLE);
                      wrapper.passthrough(Type.BYTE);
                    } 
                    continue;
                  } 
                  if (size != actualSize)
                    wrapper.set(Type.INT, 0, Integer.valueOf(actualSize)); 
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_16.ANIMATION, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  InventoryTracker1_16 inventoryTracker = (InventoryTracker1_16)wrapper.user().get(InventoryTracker1_16.class);
                  if (inventoryTracker.getInventory() != -1)
                    wrapper.cancel(); 
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16to1_15_2\packets\EntityPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */