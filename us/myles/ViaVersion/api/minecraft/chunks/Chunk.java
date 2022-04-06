package us.myles.ViaVersion.api.minecraft.chunks;

import java.util.List;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public interface Chunk {
  int getX();
  
  int getZ();
  
  boolean isBiomeData();
  
  boolean isFullChunk();
  
  @Deprecated
  default boolean isGroundUp() {
    return isFullChunk();
  }
  
  boolean isIgnoreOldLightData();
  
  void setIgnoreOldLightData(boolean paramBoolean);
  
  int getBitmask();
  
  ChunkSection[] getSections();
  
  int[] getBiomeData();
  
  void setBiomeData(int[] paramArrayOfint);
  
  CompoundTag getHeightMap();
  
  void setHeightMap(CompoundTag paramCompoundTag);
  
  List<CompoundTag> getBlockEntities();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\minecraft\chunks\Chunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */