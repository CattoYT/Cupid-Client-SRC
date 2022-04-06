package us.myles.ViaVersion.bukkit.providers;

import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import us.myles.ViaVersion.ViaVersionPlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.bukkit.util.NMSUtil;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BulkChunkTranslatorProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.ClientChunks;
import us.myles.ViaVersion.util.ReflectionUtil;

public class BukkitViaBulkChunkTranslator extends BulkChunkTranslatorProvider {
  private static ReflectionUtil.ClassReflection mapChunkBulkRef;
  
  private static ReflectionUtil.ClassReflection mapChunkRef;
  
  private static Method obfuscateRef;
  
  static {
    try {
      mapChunkBulkRef = new ReflectionUtil.ClassReflection(NMSUtil.nms("PacketPlayOutMapChunkBulk"));
      mapChunkRef = new ReflectionUtil.ClassReflection(NMSUtil.nms("PacketPlayOutMapChunk"));
      if (((ViaVersionPlugin)Via.getPlatform()).isSpigot())
        obfuscateRef = Class.forName("org.spigotmc.AntiXray").getMethod("obfuscate", new Class[] { int.class, int.class, int.class, byte[].class, NMSUtil.nms("World") }); 
    } catch (ClassNotFoundException classNotFoundException) {
    
    } catch (Exception e) {
      Via.getPlatform().getLogger().log(Level.WARNING, "Failed to initialise chunks reflection", e);
    } 
  }
  
  public List<Object> transformMapChunkBulk(Object packet, ClientChunks clientChunks) {
    List<Object> list = Lists.newArrayList();
    try {
      int[] xcoords = (int[])mapChunkBulkRef.getFieldValue("a", packet, int[].class);
      int[] zcoords = (int[])mapChunkBulkRef.getFieldValue("b", packet, int[].class);
      Object[] chunkMaps = (Object[])mapChunkBulkRef.getFieldValue("c", packet, Object[].class);
      if (Via.getConfig().isAntiXRay() && ((ViaVersionPlugin)Via.getPlatform()).isSpigot())
        try {
          Object world = mapChunkBulkRef.getFieldValue("world", packet, Object.class);
          Object spigotConfig = ReflectionUtil.getPublic(world, "spigotConfig", Object.class);
          Object antiXrayInstance = ReflectionUtil.getPublic(spigotConfig, "antiXrayInstance", Object.class);
          for (int j = 0; j < xcoords.length; j++) {
            Object b = ReflectionUtil.get(chunkMaps[j], "b", Object.class);
            Object a = ReflectionUtil.get(chunkMaps[j], "a", Object.class);
            obfuscateRef.invoke(antiXrayInstance, new Object[] { Integer.valueOf(xcoords[j]), Integer.valueOf(zcoords[j]), b, a, world });
          } 
        } catch (Exception exception) {} 
      for (int i = 0; i < chunkMaps.length; i++) {
        int x = xcoords[i];
        int z = zcoords[i];
        Object chunkMap = chunkMaps[i];
        Object chunkPacket = mapChunkRef.newInstance();
        mapChunkRef.setFieldValue("a", chunkPacket, Integer.valueOf(x));
        mapChunkRef.setFieldValue("b", chunkPacket, Integer.valueOf(z));
        mapChunkRef.setFieldValue("c", chunkPacket, chunkMap);
        mapChunkRef.setFieldValue("d", chunkPacket, Boolean.valueOf(true));
        clientChunks.getBulkChunks().add(Long.valueOf(ClientChunks.toLong(x, z)));
        list.add(chunkPacket);
      } 
    } catch (Exception e) {
      Via.getPlatform().getLogger().log(Level.WARNING, "Failed to transform chunks bulk", e);
    } 
    return list;
  }
  
  public boolean isFiltered(Class<?> packetClass) {
    return packetClass.getName().endsWith("PacketPlayOutMapChunkBulk");
  }
  
  public boolean isPacketLevel() {
    return false;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\providers\BukkitViaBulkChunkTranslator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */