package us.myles.ViaVersion.bungee.platform;

import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.bungee.service.ProtocolDetectorService;

public class BungeeViaAPI implements ViaAPI<ProxiedPlayer> {
  public int getPlayerVersion(ProxiedPlayer player) {
    UserConnection conn = Via.getManager().getConnection(player.getUniqueId());
    if (conn == null)
      return player.getPendingConnection().getVersion(); 
    return conn.getProtocolInfo().getProtocolVersion();
  }
  
  public int getPlayerVersion(UUID uuid) {
    return getPlayerVersion(ProxyServer.getInstance().getPlayer(uuid));
  }
  
  public boolean isInjected(UUID playerUUID) {
    return Via.getManager().isClientConnected(playerUUID);
  }
  
  public String getVersion() {
    return Via.getPlatform().getPluginVersion();
  }
  
  public void sendRawPacket(UUID uuid, ByteBuf packet) throws IllegalArgumentException {
    if (!isInjected(uuid))
      throw new IllegalArgumentException("This player is not controlled by ViaVersion!"); 
    UserConnection ci = Via.getManager().getConnection(uuid);
    ci.sendRawPacket(packet);
  }
  
  public void sendRawPacket(ProxiedPlayer player, ByteBuf packet) throws IllegalArgumentException {
    sendRawPacket(player.getUniqueId(), packet);
  }
  
  public BossBar createBossBar(String title, BossColor color, BossStyle style) {
    return (BossBar)new BungeeBossBar(title, 1.0F, color, style);
  }
  
  public BossBar createBossBar(String title, float health, BossColor color, BossStyle style) {
    return (BossBar)new BungeeBossBar(title, health, color, style);
  }
  
  public SortedSet<Integer> getSupportedVersions() {
    SortedSet<Integer> outputSet = new TreeSet<>(ProtocolRegistry.getSupportedVersions());
    outputSet.removeAll((Collection<?>)Via.getPlatform().getConf().getBlockedProtocols());
    return outputSet;
  }
  
  public void probeServer(ServerInfo serverInfo) {
    ProtocolDetectorService.probeServer(serverInfo);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\platform\BungeeViaAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */