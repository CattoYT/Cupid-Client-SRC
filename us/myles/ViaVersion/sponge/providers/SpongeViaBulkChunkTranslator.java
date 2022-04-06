package us.myles.ViaVersion.sponge.providers;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.logging.Level;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BulkChunkTranslatorProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.ClientChunks;
import us.myles.ViaVersion.util.ReflectionUtil;

public class SpongeViaBulkChunkTranslator extends BulkChunkTranslatorProvider {
  private static ReflectionUtil.ClassReflection mapChunkBulkRef;
  
  private static ReflectionUtil.ClassReflection mapChunkRef;
  
  static {
    try {
      mapChunkBulkRef = new ReflectionUtil.ClassReflection(Class.forName("net.minecraft.network.play.server.S26PacketMapChunkBulk"));
      mapChunkRef = new ReflectionUtil.ClassReflection(Class.forName("net.minecraft.network.play.server.S21PacketChunkData"));
    } catch (ClassNotFoundException classNotFoundException) {
    
    } catch (Exception e) {
      Via.getPlatform().getLogger().log(Level.WARNING, "Failed to initialise chunks reflection", e);
    } 
  }
  
  public List<Object> transformMapChunkBulk(Object packet, ClientChunks clientChunks) {
    List<Object> list = Lists.newArrayList();
    try {
      int[] xcoords = (int[])mapChunkBulkRef.getFieldValue("field_149266_a", packet, int[].class);
      int[] zcoords = (int[])mapChunkBulkRef.getFieldValue("field_149264_b", packet, int[].class);
      Object[] chunkMaps = (Object[])mapChunkBulkRef.getFieldValue("field_179755_c", packet, Object[].class);
      for (int i = 0; i < chunkMaps.length; i++) {
        int x = xcoords[i];
        int z = zcoords[i];
        Object chunkMap = chunkMaps[i];
        Object chunkPacket = mapChunkRef.newInstance();
        mapChunkRef.setFieldValue("field_149284_a", chunkPacket, Integer.valueOf(x));
        mapChunkRef.setFieldValue("field_149282_b", chunkPacket, Integer.valueOf(z));
        mapChunkRef.setFieldValue("field_179758_c", chunkPacket, chunkMap);
        mapChunkRef.setFieldValue("field_149279_g", chunkPacket, Boolean.valueOf(true));
        clientChunks.getBulkChunks().add(Long.valueOf(ClientChunks.toLong(x, z)));
        list.add(chunkPacket);
      } 
    } catch (Exception e) {
      Via.getPlatform().getLogger().log(Level.WARNING, "Failed to transform chunks bulk", e);
    } 
    return list;
  }
  
  public boolean isFiltered(Class<?> packetClass) {
    return packetClass.getName().endsWith("S26PacketMapChunkBulk");
  }
  
  public boolean isPacketLevel() {
    return false;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\providers\SpongeViaBulkChunkTranslator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */