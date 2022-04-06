package us.myles.ViaVersion.protocols.protocol1_17to1_16_4.storage;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;

public class BiomeStorage extends StoredObject {
  private final Map<Long, int[]> chunkBiomes = (Map)new HashMap<>();
  
  private String world;
  
  public BiomeStorage(UserConnection user) {
    super(user);
  }
  
  @Nullable
  public String getWorld() {
    return this.world;
  }
  
  public void setWorld(String world) {
    this.world = world;
  }
  
  @Nullable
  public int[] getBiomes(int x, int z) {
    return this.chunkBiomes.get(Long.valueOf(getChunkSectionIndex(x, z)));
  }
  
  public void setBiomes(int x, int z, int[] biomes) {
    this.chunkBiomes.put(Long.valueOf(getChunkSectionIndex(x, z)), biomes);
  }
  
  public void clearBiomes(int x, int z) {
    this.chunkBiomes.remove(Long.valueOf(getChunkSectionIndex(x, z)));
  }
  
  public void clearBiomes() {
    this.chunkBiomes.clear();
  }
  
  private long getChunkSectionIndex(int x, int z) {
    return (x & 0x3FFFFFFL) << 38L | z & 0x3FFFFFFL;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_17to1_16_4\storage\BiomeStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */