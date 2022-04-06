package us.myles.ViaVersion.sponge.platform;

import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.spongepowered.api.entity.living.player.Player;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;

public class SpongeViaAPI implements ViaAPI<Player> {
  public int getPlayerVersion(Player player) {
    return getPlayerVersion(player.getUniqueId());
  }
  
  public int getPlayerVersion(UUID uuid) {
    if (!isInjected(uuid))
      return ProtocolRegistry.SERVER_PROTOCOL; 
    return Via.getManager().getConnection(uuid).getProtocolInfo().getProtocolVersion();
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
  
  public void sendRawPacket(Player player, ByteBuf packet) throws IllegalArgumentException {
    sendRawPacket(player.getUniqueId(), packet);
  }
  
  public BossBar createBossBar(String title, BossColor color, BossStyle style) {
    return (BossBar)new SpongeBossBar(title, 1.0F, color, style);
  }
  
  public BossBar createBossBar(String title, float health, BossColor color, BossStyle style) {
    return (BossBar)new SpongeBossBar(title, health, color, style);
  }
  
  public SortedSet<Integer> getSupportedVersions() {
    SortedSet<Integer> outputSet = new TreeSet<>(ProtocolRegistry.getSupportedVersions());
    outputSet.removeAll((Collection<?>)Via.getPlatform().getConf().getBlockedProtocols());
    return outputSet;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\platform\SpongeViaAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */