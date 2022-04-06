package us.myles.ViaVersion.boss;

import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;

public class ViaBossBar extends CommonBoss<Player> {
  public ViaBossBar(String title, float health, BossColor color, BossStyle style) {
    super(title, health, color, style);
  }
  
  public BossBar addPlayer(Player player) {
    addPlayer(player.getUniqueId());
    return this;
  }
  
  public BossBar addPlayers(Player... players) {
    for (Player p : players)
      addPlayer(p); 
    return this;
  }
  
  public BossBar removePlayer(Player player) {
    removePlayer(player.getUniqueId());
    return this;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\boss\ViaBossBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */