package us.myles.ViaVersion.bukkit.platform;

import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.ViaVersionPlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.boss.ViaBossBar;
import us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil;

public class BukkitViaAPI implements ViaAPI<Player> {
  private final ViaVersionPlugin plugin;
  
  public BukkitViaAPI(ViaVersionPlugin plugin) {
    this.plugin = plugin;
  }
  
  public int getPlayerVersion(Player player) {
    return getPlayerVersion(player.getUniqueId());
  }
  
  public int getPlayerVersion(UUID uuid) {
    if (!isInjected(uuid))
      return getExternalVersion(Bukkit.getPlayer(uuid)); 
    return Via.getManager().getConnection(uuid).getProtocolInfo().getProtocolVersion();
  }
  
  private int getExternalVersion(Player player) {
    if (!isProtocolSupport())
      return ProtocolRegistry.SERVER_PROTOCOL; 
    return ProtocolSupportUtil.getProtocolVersion(player);
  }
  
  public boolean isInjected(UUID playerUUID) {
    return Via.getManager().isClientConnected(playerUUID);
  }
  
  public String getVersion() {
    return this.plugin.getDescription().getVersion();
  }
  
  public void sendRawPacket(UUID uuid, ByteBuf packet) throws IllegalArgumentException {
    if (!isInjected(uuid))
      throw new IllegalArgumentException("This player is not controlled by ViaVersion!"); 
    UserConnection ci = Via.getManager().getConnection(uuid);
    ci.sendRawPacket(packet);
  }
  
  public void sendRawPacket(Player player, ByteBuf packet) throws IllegalArgumentException {
    sendRawPacket(player.getUniqueId(), packet);
  }
  
  public BossBar<Player> createBossBar(String title, BossColor color, BossStyle style) {
    return (BossBar<Player>)new ViaBossBar(title, 1.0F, color, style);
  }
  
  public BossBar<Player> createBossBar(String title, float health, BossColor color, BossStyle style) {
    return (BossBar<Player>)new ViaBossBar(title, health, color, style);
  }
  
  public SortedSet<Integer> getSupportedVersions() {
    SortedSet<Integer> outputSet = new TreeSet<>(ProtocolRegistry.getSupportedVersions());
    outputSet.removeAll((Collection<?>)Via.getPlatform().getConf().getBlockedProtocols());
    return outputSet;
  }
  
  public boolean isCompatSpigotBuild() {
    return this.plugin.isCompatSpigotBuild();
  }
  
  public boolean isProtocolSupport() {
    return this.plugin.isProtocolSupport();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\platform\BukkitViaAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */