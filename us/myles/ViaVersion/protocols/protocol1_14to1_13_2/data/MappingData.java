package us.myles.ViaVersion.protocols.protocol1_14to1_13_2.data;

import java.util.HashMap;
import java.util.Map;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.MappingData;
import us.myles.ViaVersion.api.data.MappingDataLoader;
import us.myles.viaversion.libs.fastutil.ints.IntOpenHashSet;
import us.myles.viaversion.libs.fastutil.ints.IntSet;
import us.myles.viaversion.libs.gson.JsonArray;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;

public class MappingData extends MappingData {
  private IntSet motionBlocking;
  
  private IntSet nonFullBlocks;
  
  public MappingData() {
    super("1.13.2", "1.14");
  }
  
  public void loadExtras(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings) {
    JsonObject blockStates = newMappings.getAsJsonObject("blockstates");
    Map<String, Integer> blockStateMap = new HashMap<>(blockStates.entrySet().size());
    for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)blockStates.entrySet())
      blockStateMap.put(((JsonElement)entry.getValue()).getAsString(), Integer.valueOf(Integer.parseInt(entry.getKey()))); 
    JsonObject heightMapData = MappingDataLoader.loadData("heightMapData-1.14.json");
    JsonArray motionBlocking = heightMapData.getAsJsonArray("MOTION_BLOCKING");
    this.motionBlocking = (IntSet)new IntOpenHashSet(motionBlocking.size(), 1.0F);
    for (JsonElement blockState : motionBlocking) {
      String key = blockState.getAsString();
      Integer id = blockStateMap.get(key);
      if (id == null) {
        Via.getPlatform().getLogger().warning("Unknown blockstate " + key + " :(");
        continue;
      } 
      this.motionBlocking.add(id.intValue());
    } 
    if (Via.getConfig().isNonFullBlockLightFix()) {
      this.nonFullBlocks = (IntSet)new IntOpenHashSet(1611, 1.0F);
      for (Map.Entry<String, JsonElement> blockstates : (Iterable<Map.Entry<String, JsonElement>>)oldMappings.getAsJsonObject("blockstates").entrySet()) {
        String state = ((JsonElement)blockstates.getValue()).getAsString();
        if (state.contains("_slab") || state.contains("_stairs") || state.contains("_wall["))
          this.nonFullBlocks.add(this.blockStateMappings.getNewId(Integer.parseInt(blockstates.getKey()))); 
      } 
      this.nonFullBlocks.add(this.blockStateMappings.getNewId(8163));
      for (int i = 3060; i <= 3067; i++)
        this.nonFullBlocks.add(this.blockStateMappings.getNewId(i)); 
    } 
  }
  
  public IntSet getMotionBlocking() {
    return this.motionBlocking;
  }
  
  public IntSet getNonFullBlocks() {
    return this.nonFullBlocks;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14to1_13_2\data\MappingData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */