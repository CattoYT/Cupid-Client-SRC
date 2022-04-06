package us.myles.ViaVersion.api.minecraft.chunks;

import java.util.List;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public class BaseChunk implements Chunk {
  protected final int x;
  
  protected final int z;
  
  protected final boolean fullChunk;
  
  protected boolean ignoreOldLightData;
  
  protected final int bitmask;
  
  protected final ChunkSection[] sections;
  
  protected int[] biomeData;
  
  protected CompoundTag heightMap;
  
  protected final List<CompoundTag> blockEntities;
  
  public BaseChunk(int x, int z, boolean fullChunk, boolean ignoreOldLightData, int bitmask, ChunkSection[] sections, int[] biomeData, CompoundTag heightMap, List<CompoundTag> blockEntities) {
    this.x = x;
    this.z = z;
    this.fullChunk = fullChunk;
    this.ignoreOldLightData = ignoreOldLightData;
    this.bitmask = bitmask;
    this.sections = sections;
    this.biomeData = biomeData;
    this.heightMap = heightMap;
    this.blockEntities = blockEntities;
  }
  
  public BaseChunk(int x, int z, boolean fullChunk, boolean ignoreOldLightData, int bitmask, ChunkSection[] sections, int[] biomeData, List<CompoundTag> blockEntities) {
    this(x, z, fullChunk, ignoreOldLightData, bitmask, sections, biomeData, null, blockEntities);
  }
  
  public boolean isBiomeData() {
    return (this.biomeData != null);
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getZ() {
    return this.z;
  }
  
  public boolean isFullChunk() {
    return this.fullChunk;
  }
  
  public boolean isIgnoreOldLightData() {
    return this.ignoreOldLightData;
  }
  
  public void setIgnoreOldLightData(boolean ignoreOldLightData) {
    this.ignoreOldLightData = ignoreOldLightData;
  }
  
  public int getBitmask() {
    return this.bitmask;
  }
  
  public ChunkSection[] getSections() {
    return this.sections;
  }
  
  public int[] getBiomeData() {
    return this.biomeData;
  }
  
  public void setBiomeData(int[] biomeData) {
    this.biomeData = biomeData;
  }
  
  public CompoundTag getHeightMap() {
    return this.heightMap;
  }
  
  public void setHeightMap(CompoundTag heightMap) {
    this.heightMap = heightMap;
  }
  
  public List<CompoundTag> getBlockEntities() {
    return this.blockEntities;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\minecraft\chunks\BaseChunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */