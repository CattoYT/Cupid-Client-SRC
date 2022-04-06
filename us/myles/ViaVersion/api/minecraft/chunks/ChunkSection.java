package us.myles.ViaVersion.api.minecraft.chunks;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;
import us.myles.viaversion.libs.fastutil.ints.Int2IntMap;
import us.myles.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import us.myles.viaversion.libs.fastutil.ints.IntArrayList;
import us.myles.viaversion.libs.fastutil.ints.IntList;

public class ChunkSection {
  public static final int SIZE = 4096;
  
  public static final int LIGHT_LENGTH = 2048;
  
  private final IntList palette;
  
  private final Int2IntMap inversePalette;
  
  private final int[] blocks;
  
  private NibbleArray blockLight;
  
  private NibbleArray skyLight;
  
  private int nonAirBlocksCount;
  
  public ChunkSection() {
    this.blocks = new int[4096];
    this.blockLight = new NibbleArray(4096);
    this.palette = (IntList)new IntArrayList();
    this.inversePalette = (Int2IntMap)new Int2IntOpenHashMap();
    this.inversePalette.defaultReturnValue(-1);
  }
  
  public ChunkSection(int expectedPaletteLength) {
    this.blocks = new int[4096];
    this.blockLight = new NibbleArray(4096);
    this.palette = (IntList)new IntArrayList(expectedPaletteLength);
    this.inversePalette = (Int2IntMap)new Int2IntOpenHashMap(expectedPaletteLength);
    this.inversePalette.defaultReturnValue(-1);
  }
  
  public void setBlock(int x, int y, int z, int type, int data) {
    setFlatBlock(index(x, y, z), type << 4 | data & 0xF);
  }
  
  public void setFlatBlock(int x, int y, int z, int type) {
    setFlatBlock(index(x, y, z), type);
  }
  
  public int getBlockId(int x, int y, int z) {
    return getFlatBlock(x, y, z) >> 4;
  }
  
  public int getBlockData(int x, int y, int z) {
    return getFlatBlock(x, y, z) & 0xF;
  }
  
  public int getFlatBlock(int x, int y, int z) {
    int index = this.blocks[index(x, y, z)];
    return this.palette.getInt(index);
  }
  
  public int getFlatBlock(int idx) {
    int index = this.blocks[idx];
    return this.palette.getInt(index);
  }
  
  public void setBlock(int idx, int type, int data) {
    setFlatBlock(idx, type << 4 | data & 0xF);
  }
  
  public void setPaletteIndex(int idx, int index) {
    this.blocks[idx] = index;
  }
  
  public int getPaletteIndex(int idx) {
    return this.blocks[idx];
  }
  
  public int getPaletteSize() {
    return this.palette.size();
  }
  
  public int getPaletteEntry(int index) {
    if (index < 0 || index >= this.palette.size())
      throw new IndexOutOfBoundsException(); 
    return this.palette.getInt(index);
  }
  
  public void setPaletteEntry(int index, int id) {
    if (index < 0 || index >= this.palette.size())
      throw new IndexOutOfBoundsException(); 
    int oldId = this.palette.set(index, id);
    if (oldId == id)
      return; 
    this.inversePalette.put(id, index);
    if (this.inversePalette.get(oldId) == index) {
      this.inversePalette.remove(oldId);
      for (int i = 0; i < this.palette.size(); i++) {
        if (this.palette.getInt(i) == oldId) {
          this.inversePalette.put(oldId, i);
          break;
        } 
      } 
    } 
  }
  
  public void replacePaletteEntry(int oldId, int newId) {
    int index = this.inversePalette.remove(oldId);
    if (index == -1)
      return; 
    this.inversePalette.put(newId, index);
    for (int i = 0; i < this.palette.size(); i++) {
      if (this.palette.getInt(i) == oldId)
        this.palette.set(i, newId); 
    } 
  }
  
  public void addPaletteEntry(int id) {
    this.inversePalette.put(id, this.palette.size());
    this.palette.add(id);
  }
  
  public void clearPalette() {
    this.palette.clear();
    this.inversePalette.clear();
  }
  
  public void setFlatBlock(int idx, int id) {
    int index = this.inversePalette.get(id);
    if (index == -1) {
      index = this.palette.size();
      this.palette.add(id);
      this.inversePalette.put(id, index);
    } 
    this.blocks[idx] = index;
  }
  
  public void setBlockLight(@Nullable byte[] data) {
    if (data.length != 2048)
      throw new IllegalArgumentException("Data length != 2048"); 
    if (this.blockLight == null) {
      this.blockLight = new NibbleArray(data);
    } else {
      this.blockLight.setHandle(data);
    } 
  }
  
  public void setSkyLight(@Nullable byte[] data) {
    if (data.length != 2048)
      throw new IllegalArgumentException("Data length != 2048"); 
    if (this.skyLight == null) {
      this.skyLight = new NibbleArray(data);
    } else {
      this.skyLight.setHandle(data);
    } 
  }
  
  @Nullable
  public byte[] getBlockLight() {
    return (this.blockLight == null) ? null : this.blockLight.getHandle();
  }
  
  @Nullable
  public NibbleArray getBlockLightNibbleArray() {
    return this.blockLight;
  }
  
  @Nullable
  public byte[] getSkyLight() {
    return (this.skyLight == null) ? null : this.skyLight.getHandle();
  }
  
  @Nullable
  public NibbleArray getSkyLightNibbleArray() {
    return this.skyLight;
  }
  
  public void readBlockLight(ByteBuf input) {
    if (this.blockLight == null)
      this.blockLight = new NibbleArray(4096); 
    input.readBytes(this.blockLight.getHandle());
  }
  
  public void readSkyLight(ByteBuf input) {
    if (this.skyLight == null)
      this.skyLight = new NibbleArray(4096); 
    input.readBytes(this.skyLight.getHandle());
  }
  
  public static int index(int x, int y, int z) {
    return y << 8 | z << 4 | x;
  }
  
  public void writeBlockLight(ByteBuf output) {
    output.writeBytes(this.blockLight.getHandle());
  }
  
  public void writeSkyLight(ByteBuf output) {
    output.writeBytes(this.skyLight.getHandle());
  }
  
  public boolean hasSkyLight() {
    return (this.skyLight != null);
  }
  
  public boolean hasBlockLight() {
    return (this.blockLight != null);
  }
  
  public int getNonAirBlocksCount() {
    return this.nonAirBlocksCount;
  }
  
  public void setNonAirBlocksCount(int nonAirBlocksCount) {
    this.nonAirBlocksCount = nonAirBlocksCount;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\minecraft\chunks\ChunkSection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */