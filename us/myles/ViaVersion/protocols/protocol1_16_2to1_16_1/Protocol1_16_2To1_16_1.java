package us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.MappingData;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.RegistryType;
import us.myles.ViaVersion.api.rewriters.SoundRewriter;
import us.myles.ViaVersion.api.rewriters.StatisticsRewriter;
import us.myles.ViaVersion.api.rewriters.TagRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.data.MappingData;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.metadata.MetadataRewriter1_16_2To1_16_1;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.packets.EntityPackets;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.packets.WorldPackets;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.storage.EntityTracker1_16_2;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;

public class Protocol1_16_2To1_16_1 extends Protocol<ClientboundPackets1_16, ClientboundPackets1_16_2, ServerboundPackets1_16, ServerboundPackets1_16_2> {
  public static final MappingData MAPPINGS = new MappingData();
  
  private TagRewriter tagRewriter;
  
  public Protocol1_16_2To1_16_1() {
    super(ClientboundPackets1_16.class, ClientboundPackets1_16_2.class, ServerboundPackets1_16.class, ServerboundPackets1_16_2.class);
  }
  
  protected void registerPackets() {
    MetadataRewriter1_16_2To1_16_1 metadataRewriter1_16_2To1_16_1 = new MetadataRewriter1_16_2To1_16_1(this);
    EntityPackets.register(this);
    WorldPackets.register(this);
    InventoryPackets.register(this);
    this.tagRewriter = new TagRewriter(this, metadataRewriter1_16_2To1_16_1::getNewEntityId);
    this.tagRewriter.register((ClientboundPacketType)ClientboundPackets1_16.TAGS);
    (new StatisticsRewriter(this, metadataRewriter1_16_2To1_16_1::getNewEntityId)).register((ClientboundPacketType)ClientboundPackets1_16.STATISTICS);
    SoundRewriter soundRewriter = new SoundRewriter(this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16.SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16.ENTITY_SOUND);
    registerIncoming(ServerboundPackets1_16_2.RECIPE_BOOK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int recipeType = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  boolean open = ((Boolean)wrapper.read(Type.BOOLEAN)).booleanValue();
                  boolean filter = ((Boolean)wrapper.read(Type.BOOLEAN)).booleanValue();
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(1));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf((recipeType == 0)));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(filter));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf((recipeType == 1)));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(filter));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf((recipeType == 2)));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(filter));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf((recipeType == 3)));
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(filter));
                });
          }
        });
    registerIncoming(ServerboundPackets1_16_2.SEEN_RECIPE, (ServerboundPacketType)ServerboundPackets1_16.RECIPE_BOOK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  String recipe = (String)wrapper.read(Type.STRING);
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                  wrapper.write(Type.STRING, recipe);
                });
          }
        });
  }
  
  protected void onMappingDataLoaded() {
    this.tagRewriter.addTag(RegistryType.ITEM, "minecraft:stone_crafting_materials", new int[] { 14, 962 });
    this.tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:mushroom_grow_block");
    this.tagRewriter.addEmptyTags(RegistryType.ITEM, new String[] { "minecraft:soul_fire_base_blocks", "minecraft:furnace_materials", "minecraft:crimson_stems", "minecraft:gold_ores", "minecraft:piglin_loved", "minecraft:piglin_repellents", "minecraft:creeper_drop_music_discs", "minecraft:logs_that_burn", "minecraft:stone_tool_materials", "minecraft:warped_stems" });
    this.tagRewriter.addEmptyTags(RegistryType.BLOCK, new String[] { 
          "minecraft:infiniburn_nether", "minecraft:crimson_stems", "minecraft:wither_summon_base_blocks", "minecraft:infiniburn_overworld", "minecraft:piglin_repellents", "minecraft:hoglin_repellents", "minecraft:prevent_mob_spawning_inside", "minecraft:wart_blocks", "minecraft:stone_pressure_plates", "minecraft:nylium", 
          "minecraft:gold_ores", "minecraft:pressure_plates", "minecraft:logs_that_burn", "minecraft:strider_warm_blocks", "minecraft:warped_stems", "minecraft:infiniburn_end", "minecraft:base_stone_nether", "minecraft:base_stone_overworld" });
  }
  
  public void init(UserConnection userConnection) {
    userConnection.put((StoredObject)new EntityTracker1_16_2(userConnection));
  }
  
  public MappingData getMappingData() {
    return MAPPINGS;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16_2to1_16_1\Protocol1_16_2To1_16_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */