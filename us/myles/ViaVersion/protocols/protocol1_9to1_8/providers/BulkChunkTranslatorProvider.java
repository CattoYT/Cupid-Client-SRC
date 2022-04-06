package us.myles.ViaVersion.protocols.protocol1_9to1_8.providers;

import java.util.ArrayList;
import java.util.List;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.platform.providers.Provider;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.CustomByteType;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.ClientChunks;

public class BulkChunkTranslatorProvider implements Provider {
  public List<Object> transformMapChunkBulk(Object packet, ClientChunks clientChunks) throws Exception {
    if (!(packet instanceof PacketWrapper))
      throw new IllegalArgumentException("The default packet has to be a PacketWrapper for transformMapChunkBulk, unexpected " + packet.getClass()); 
    List<Object> packets = new ArrayList();
    PacketWrapper wrapper = (PacketWrapper)packet;
    boolean skyLight = ((Boolean)wrapper.read(Type.BOOLEAN)).booleanValue();
    int count = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
    ChunkBulkSection[] metas = new ChunkBulkSection[count];
    for (int i = 0; i < count; i++)
      metas[i] = ChunkBulkSection.read(wrapper, skyLight); 
    for (ChunkBulkSection meta : metas) {
      CustomByteType customByteType = new CustomByteType(Integer.valueOf(meta.getLength()));
      meta.setData((byte[])wrapper.read((Type)customByteType));
      PacketWrapper chunkPacket = new PacketWrapper(33, null, wrapper.user());
      chunkPacket.write(Type.INT, Integer.valueOf(meta.getX()));
      chunkPacket.write(Type.INT, Integer.valueOf(meta.getZ()));
      chunkPacket.write(Type.BOOLEAN, Boolean.valueOf(true));
      chunkPacket.write(Type.UNSIGNED_SHORT, Integer.valueOf(meta.getBitMask()));
      chunkPacket.write((Type)Type.VAR_INT, Integer.valueOf(meta.getLength()));
      chunkPacket.write((Type)customByteType, meta.getData());
      clientChunks.getBulkChunks().add(Long.valueOf(ClientChunks.toLong(meta.getX(), meta.getZ())));
      packets.add(chunkPacket);
    } 
    return packets;
  }
  
  public boolean isFiltered(Class<?> packet) {
    return false;
  }
  
  public boolean isPacketLevel() {
    return true;
  }
  
  private static class ChunkBulkSection {
    private int x;
    
    private int z;
    
    private int bitMask;
    
    private int length;
    
    private byte[] data;
    
    public static ChunkBulkSection read(PacketWrapper wrapper, boolean skylight) throws Exception {
      ChunkBulkSection bulkSection = new ChunkBulkSection();
      bulkSection.setX(((Integer)wrapper.read(Type.INT)).intValue());
      bulkSection.setZ(((Integer)wrapper.read(Type.INT)).intValue());
      bulkSection.setBitMask(((Integer)wrapper.read(Type.UNSIGNED_SHORT)).intValue());
      int bitCount = Integer.bitCount(bulkSection.getBitMask());
      bulkSection.setLength(bitCount * 10240 + (skylight ? (bitCount * 2048) : 0) + 256);
      return bulkSection;
    }
    
    public int getX() {
      return this.x;
    }
    
    public void setX(int x) {
      this.x = x;
    }
    
    public int getZ() {
      return this.z;
    }
    
    public void setZ(int z) {
      this.z = z;
    }
    
    public int getBitMask() {
      return this.bitMask;
    }
    
    public void setBitMask(int bitMask) {
      this.bitMask = bitMask;
    }
    
    public int getLength() {
      return this.length;
    }
    
    public void setLength(int length) {
      this.length = length;
    }
    
    public byte[] getData() {
      return this.data;
    }
    
    public void setData(byte[] data) {
      this.data = data;
    }
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\providers\BulkChunkTranslatorProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */