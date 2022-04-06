package us.myles.ViaVersion.bukkit.providers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.bukkit.util.NMSUtil;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.MovementTracker;

public class BukkitViaMovementTransmitter extends MovementTransmitterProvider {
  private static boolean USE_NMS = true;
  
  private Object idlePacket;
  
  private Object idlePacket2;
  
  private Method getHandle;
  
  private Field connection;
  
  private Method handleFlying;
  
  public BukkitViaMovementTransmitter() {
    Class<?> idlePacketClass;
    USE_NMS = Via.getConfig().isNMSPlayerTicking();
    try {
      idlePacketClass = NMSUtil.nms("PacketPlayInFlying");
    } catch (ClassNotFoundException e) {
      return;
    } 
    try {
      this.idlePacket = idlePacketClass.newInstance();
      this.idlePacket2 = idlePacketClass.newInstance();
      Field flying = idlePacketClass.getDeclaredField("f");
      flying.setAccessible(true);
      flying.set(this.idlePacket2, Boolean.valueOf(true));
    } catch (NoSuchFieldException|InstantiationException|IllegalArgumentException|IllegalAccessException e) {
      throw new RuntimeException("Couldn't make player idle packet, help!", e);
    } 
    if (USE_NMS) {
      try {
        this.getHandle = NMSUtil.obc("entity.CraftPlayer").getDeclaredMethod("getHandle", new Class[0]);
      } catch (NoSuchMethodException|ClassNotFoundException e) {
        throw new RuntimeException("Couldn't find CraftPlayer", e);
      } 
      try {
        this.connection = NMSUtil.nms("EntityPlayer").getDeclaredField("playerConnection");
      } catch (NoSuchFieldException|ClassNotFoundException e) {
        throw new RuntimeException("Couldn't find Player Connection", e);
      } 
      try {
        this.handleFlying = NMSUtil.nms("PlayerConnection").getDeclaredMethod("a", new Class[] { idlePacketClass });
      } catch (NoSuchMethodException|ClassNotFoundException e) {
        throw new RuntimeException("Couldn't find CraftPlayer", e);
      } 
    } 
  }
  
  public Object getFlyingPacket() {
    if (this.idlePacket == null)
      throw new NullPointerException("Could not locate flying packet"); 
    return this.idlePacket2;
  }
  
  public Object getGroundPacket() {
    if (this.idlePacket == null)
      throw new NullPointerException("Could not locate flying packet"); 
    return this.idlePacket;
  }
  
  public void sendPlayer(UserConnection info) {
    if (USE_NMS) {
      Player player = Bukkit.getPlayer(info.getProtocolInfo().getUuid());
      if (player != null)
        try {
          Object entityPlayer = this.getHandle.invoke(player, new Object[0]);
          Object pc = this.connection.get(entityPlayer);
          if (pc != null) {
            this.handleFlying.invoke(pc, new Object[] { ((MovementTracker)info.get(MovementTracker.class)).isGround() ? this.idlePacket2 : this.idlePacket });
            ((MovementTracker)info.get(MovementTracker.class)).incrementIdlePacket();
          } 
        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
          e.printStackTrace();
        }  
    } else {
      super.sendPlayer(info);
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\providers\BukkitViaMovementTransmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */