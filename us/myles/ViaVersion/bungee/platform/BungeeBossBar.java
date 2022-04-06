package us.myles.ViaVersion.bungee.platform;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.boss.CommonBoss;

public class BungeeBossBar extends CommonBoss<ProxiedPlayer> {
  public BungeeBossBar(String title, float health, BossColor color, BossStyle style) {
    super(title, health, color, style);
  }
  
  public BossBar addPlayer(ProxiedPlayer player) {
    addPlayer(player.getUniqueId());
    return (BossBar)this;
  }
  
  public BossBar addPlayers(ProxiedPlayer... players) {
    for (ProxiedPlayer p : players)
      addPlayer(p); 
    return (BossBar)this;
  }
  
  public BossBar removePlayer(ProxiedPlayer player) {
    removePlayer(player.getUniqueId());
    return (BossBar)this;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\platform\BungeeBossBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */