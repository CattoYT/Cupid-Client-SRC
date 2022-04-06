package us.myles.ViaVersion.velocity.platform;

import com.velocitypowered.api.proxy.Player;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.boss.CommonBoss;

public class VelocityBossBar extends CommonBoss<Player> {
  public VelocityBossBar(String title, float health, BossColor color, BossStyle style) {
    super(title, health, color, style);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\platform\VelocityBossBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */