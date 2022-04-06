package us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.MappingData;
import us.myles.ViaVersion.api.data.MappingDataLoader;
import us.myles.ViaVersion.api.minecraft.nbt.BinaryTagIO;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ListTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class MappingData extends MappingData {
  private final Map<String, CompoundTag> dimensionDataMap = new HashMap<>();
  
  private CompoundTag dimensionRegistry;
  
  public MappingData() {
    super("1.16", "1.16.2", true);
  }
  
  public void loadExtras(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings) {
    try {
      this.dimensionRegistry = BinaryTagIO.readCompressedInputStream(MappingDataLoader.getResource("dimension-registry-1.16.2.nbt"));
    } catch (IOException e) {
      Via.getPlatform().getLogger().severe("Error loading dimension registry:");
      e.printStackTrace();
    } 
    ListTag dimensions = (ListTag)((CompoundTag)this.dimensionRegistry.get("minecraft:dimension_type")).get("value");
    for (Tag dimension : dimensions) {
      CompoundTag dimensionCompound = (CompoundTag)dimension;
      CompoundTag dimensionData = new CompoundTag("", ((CompoundTag)dimensionCompound.get("element")).getValue());
      this.dimensionDataMap.put(((StringTag)dimensionCompound.get("name")).getValue(), dimensionData);
    } 
  }
  
  public Map<String, CompoundTag> getDimensionDataMap() {
    return this.dimensionDataMap;
  }
  
  public CompoundTag getDimensionRegistry() {
    return this.dimensionRegistry;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16_2to1_16_1\data\MappingData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */