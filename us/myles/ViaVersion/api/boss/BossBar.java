package us.myles.ViaVersion.api.boss;

import java.util.Set;
import java.util.UUID;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;

public abstract class BossBar<T> {
  public abstract String getTitle();
  
  public abstract BossBar setTitle(String paramString);
  
  public abstract float getHealth();
  
  public abstract BossBar setHealth(float paramFloat);
  
  public abstract BossColor getColor();
  
  public abstract BossBar setColor(BossColor paramBossColor);
  
  public abstract BossStyle getStyle();
  
  public abstract BossBar setStyle(BossStyle paramBossStyle);
  
  @Deprecated
  public BossBar addPlayer(T player) {
    throw new UnsupportedOperationException("This method is not implemented for the platform " + Via.getPlatform().getPlatformName());
  }
  
  public abstract BossBar addPlayer(UUID paramUUID);
  
  public abstract BossBar addConnection(UserConnection paramUserConnection);
  
  @Deprecated
  public BossBar addPlayers(T... players) {
    throw new UnsupportedOperationException("This method is not implemented for the platform " + Via.getPlatform().getPlatformName());
  }
  
  @Deprecated
  public BossBar removePlayer(T player) {
    throw new UnsupportedOperationException("This method is not implemented for the platform " + Via.getPlatform().getPlatformName());
  }
  
  public abstract BossBar removePlayer(UUID paramUUID);
  
  public abstract BossBar removeConnection(UserConnection paramUserConnection);
  
  public abstract BossBar addFlag(BossFlag paramBossFlag);
  
  public abstract BossBar removeFlag(BossFlag paramBossFlag);
  
  public abstract boolean hasFlag(BossFlag paramBossFlag);
  
  public abstract Set<UUID> getPlayers();
  
  public abstract Set<UserConnection> getConnections();
  
  public abstract BossBar show();
  
  public abstract BossBar hide();
  
  public abstract boolean isVisible();
  
  public abstract UUID getId();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\boss\BossBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */