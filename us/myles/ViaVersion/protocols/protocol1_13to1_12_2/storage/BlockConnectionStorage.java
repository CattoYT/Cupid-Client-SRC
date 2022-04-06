package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import us.myles.ViaVersion.api.Pair;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.minecraft.chunks.NibbleArray;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.packets.WorldPackets;

public class BlockConnectionStorage extends StoredObject {
  private static final short[] REVERSE_BLOCK_MAPPINGS = new short[8582];
  
  private static Constructor<?> fastUtilLongObjectHashMap;
  
  private final Map<Long, Pair<byte[], NibbleArray>> blockStorage = createLongObjectMap();
  
  static {
    try {
      fastUtilLongObjectHashMap = Class.forName("us.myles.viaversion.libs.fastutil.longs.Long2ObjectOpenHashMap").getConstructor(new Class[0]);
      Via.getPlatform().getLogger().info("Using FastUtil Long2ObjectOpenHashMap for block connections");
    } catch (ClassNotFoundException|NoSuchMethodException classNotFoundException) {}
    Arrays.fill(REVERSE_BLOCK_MAPPINGS, (short)-1);
    for (int i = 0; i < 4096; i++) {
      int newBlock = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(i);
      if (newBlock != -1)
        REVERSE_BLOCK_MAPPINGS[newBlock] = (short)i; 
    } 
  }
  
  public BlockConnectionStorage(UserConnection user) {
    super(user);
  }
  
  public void store(int x, int y, int z, int blockState) {
    short mapping = REVERSE_BLOCK_MAPPINGS[blockState];
    if (mapping == -1)
      return; 
    blockState = mapping;
    long pair = getChunkSectionIndex(x, y, z);
    Pair<byte[], NibbleArray> map = getChunkSection(pair, ((blockState & 0xF) != 0));
    int blockIndex = encodeBlockPos(x, y, z);
    ((byte[])map.getKey())[blockIndex] = (byte)(blockState >> 4);
    NibbleArray nibbleArray = (NibbleArray)map.getValue();
    if (nibbleArray != null)
      nibbleArray.set(blockIndex, blockState); 
  }
  
  public int get(int x, int y, int z) {
    long pair = getChunkSectionIndex(x, y, z);
    Pair<byte[], NibbleArray> map = this.blockStorage.get(Long.valueOf(pair));
    if (map == null)
      return 0; 
    short blockPosition = encodeBlockPos(x, y, z);
    NibbleArray nibbleArray = (NibbleArray)map.getValue();
    return WorldPackets.toNewId((((byte[])map
        .getKey())[blockPosition] & 0xFF) << 4 | ((nibbleArray == null) ? 0 : nibbleArray
        .get(blockPosition)));
  }
  
  public void remove(int x, int y, int z) {
    long pair = getChunkSectionIndex(x, y, z);
    Pair<byte[], NibbleArray> map = this.blockStorage.get(Long.valueOf(pair));
    if (map == null)
      return; 
    int blockIndex = encodeBlockPos(x, y, z);
    NibbleArray nibbleArray = (NibbleArray)map.getValue();
    if (nibbleArray != null) {
      nibbleArray.set(blockIndex, 0);
      boolean allZero = true;
      for (int j = 0; j < 4096; j++) {
        if (nibbleArray.get(j) != 0) {
          allZero = false;
          break;
        } 
      } 
      if (allZero)
        map.setValue(null); 
    } 
    ((byte[])map.getKey())[blockIndex] = 0;
    byte[] arrayOfByte;
    int i;
    byte b;
    for (arrayOfByte = (byte[])map.getKey(), i = arrayOfByte.length, b = 0; b < i; ) {
      short entry = (short)arrayOfByte[b];
      if (entry != 0)
        return; 
      b++;
    } 
    this.blockStorage.remove(Long.valueOf(pair));
  }
  
  public void clear() {
    this.blockStorage.clear();
  }
  
  public void unloadChunk(int x, int z) {
    for (int y = 0; y < 256; y += 16)
      this.blockStorage.remove(Long.valueOf(getChunkSectionIndex(x << 4, y, z << 4))); 
  }
  
  private Pair<byte[], NibbleArray> getChunkSection(long index, boolean requireNibbleArray) {
    Pair<byte[], NibbleArray> map = this.blockStorage.get(Long.valueOf(index));
    if (map == null) {
      map = new Pair(new byte[4096], null);
      this.blockStorage.put(Long.valueOf(index), map);
    } 
    if (map.getValue() == null && requireNibbleArray)
      map.setValue(new NibbleArray(4096)); 
    return map;
  }
  
  private long getChunkSectionIndex(int x, int y, int z) {
    return ((x >> 4) & 0x3FFFFFFL) << 38L | ((y >> 4) & 0xFFFL) << 26L | (z >> 4) & 0x3FFFFFFL;
  }
  
  private long getChunkSectionIndex(Position position) {
    return getChunkSectionIndex(position.getX(), position.getY(), position.getZ());
  }
  
  private short encodeBlockPos(int x, int y, int z) {
    return (short)((y & 0xF) << 8 | (x & 0xF) << 4 | z & 0xF);
  }
  
  private short encodeBlockPos(Position pos) {
    return encodeBlockPos(pos.getX(), pos.getY(), pos.getZ());
  }
  
  private <T> Map<Long, T> createLongObjectMap() {
    if (fastUtilLongObjectHashMap != null)
      try {
        return (Map<Long, T>)fastUtilLongObjectHashMap.newInstance(new Object[0]);
      } catch (IllegalAccessException|InstantiationException|java.lang.reflect.InvocationTargetException e) {
        e.printStackTrace();
      }  
    return new HashMap<>();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\storage\BlockConnectionStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */