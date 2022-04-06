package us.myles.ViaVersion.bukkit.listeners.multiversion;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import us.myles.ViaVersion.ViaVersionPlugin;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.bukkit.listeners.ViaBukkitListener;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;

public class PlayerSneakListener extends ViaBukkitListener {
  private static final float STANDING_HEIGHT = 1.8F;
  
  private static final float HEIGHT_1_14 = 1.5F;
  
  private static final float HEIGHT_1_9 = 1.6F;
  
  private static final float DEFAULT_WIDTH = 0.6F;
  
  private final boolean is1_9Fix;
  
  private final boolean is1_14Fix;
  
  private Map<Player, Boolean> sneaking;
  
  private Set<UUID> sneakingUuids;
  
  private final Method getHandle;
  
  private Method setSize;
  
  private boolean useCache;
  
  public PlayerSneakListener(ViaVersionPlugin plugin, boolean is1_9Fix, boolean is1_14Fix) throws ReflectiveOperationException {
    super((Plugin)plugin, null);
    this.is1_9Fix = is1_9Fix;
    this.is1_14Fix = is1_14Fix;
    String packageName = plugin.getServer().getClass().getPackage().getName();
    this.getHandle = Class.forName(packageName + ".entity.CraftPlayer").getMethod("getHandle", new Class[0]);
    Class<?> entityPlayerClass = Class.forName(packageName
        .replace("org.bukkit.craftbukkit", "net.minecraft.server") + ".EntityPlayer");
    try {
      this.setSize = entityPlayerClass.getMethod("setSize", new Class[] { float.class, float.class });
    } catch (NoSuchMethodException e) {
      this.setSize = entityPlayerClass.getMethod("a", new Class[] { float.class, float.class });
    } 
    if (ProtocolRegistry.SERVER_PROTOCOL >= ProtocolVersion.v1_9.getVersion()) {
      this.sneaking = new WeakHashMap<>();
      this.useCache = true;
      plugin.getServer().getScheduler().runTaskTimer((Plugin)plugin, new Runnable() {
            public void run() {
              for (Map.Entry<Player, Boolean> entry : (Iterable<Map.Entry<Player, Boolean>>)PlayerSneakListener.this.sneaking.entrySet())
                PlayerSneakListener.this.setHeight(entry.getKey(), ((Boolean)entry.getValue()).booleanValue() ? 1.5F : 1.6F); 
            }
          }1L, 1L);
    } 
    if (is1_14Fix)
      this.sneakingUuids = new HashSet<>(); 
  }
  
  @EventHandler(ignoreCancelled = true)
  public void playerToggleSneak(PlayerToggleSneakEvent event) {
    Player player = event.getPlayer();
    UserConnection userConnection = getUserConnection(player);
    if (userConnection == null)
      return; 
    ProtocolInfo info = userConnection.getProtocolInfo();
    if (info == null)
      return; 
    int protocolVersion = info.getProtocolVersion();
    if (this.is1_14Fix && protocolVersion >= ProtocolVersion.v1_14.getVersion()) {
      setHeight(player, event.isSneaking() ? 1.5F : 1.8F);
      if (event.isSneaking()) {
        this.sneakingUuids.add(player.getUniqueId());
      } else {
        this.sneakingUuids.remove(player.getUniqueId());
      } 
      if (!this.useCache)
        return; 
      if (event.isSneaking()) {
        this.sneaking.put(player, Boolean.valueOf(true));
      } else {
        this.sneaking.remove(player);
      } 
    } else if (this.is1_9Fix && protocolVersion >= ProtocolVersion.v1_9.getVersion()) {
      setHeight(player, event.isSneaking() ? 1.6F : 1.8F);
      if (!this.useCache)
        return; 
      if (event.isSneaking()) {
        this.sneaking.put(player, Boolean.valueOf(false));
      } else {
        this.sneaking.remove(player);
      } 
    } 
  }
  
  @EventHandler(ignoreCancelled = true)
  public void playerDamage(EntityDamageEvent event) {
    if (!this.is1_14Fix)
      return; 
    if (event.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION)
      return; 
    if (event.getEntityType() != EntityType.PLAYER)
      return; 
    Player player = (Player)event.getEntity();
    if (!this.sneakingUuids.contains(player.getUniqueId()))
      return; 
    double y = player.getEyeLocation().getY() + 0.045D;
    y -= (int)y;
    if (y < 0.09D)
      event.setCancelled(true); 
  }
  
  @EventHandler
  public void playerQuit(PlayerQuitEvent event) {
    if (this.sneaking != null)
      this.sneaking.remove(event.getPlayer()); 
    if (this.sneakingUuids != null)
      this.sneakingUuids.remove(event.getPlayer().getUniqueId()); 
  }
  
  private void setHeight(Player player, float height) {
    try {
      this.setSize.invoke(this.getHandle.invoke(player, new Object[0]), new Object[] { Float.valueOf(0.6F), Float.valueOf(height) });
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      e.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\listeners\multiversion\PlayerSneakListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */