package us.myles.ViaVersion.bungee.storage;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;

public class BungeeStorage extends StoredObject {
  private static Field bossField;
  
  private final ProxiedPlayer player;
  
  private String currentServer;
  
  private Set<UUID> bossbar;
  
  static {
    try {
      Class<?> user = Class.forName("net.md_5.bungee.UserConnection");
      bossField = user.getDeclaredField("sentBossBars");
      bossField.setAccessible(true);
    } catch (ClassNotFoundException classNotFoundException) {
    
    } catch (NoSuchFieldException noSuchFieldException) {}
  }
  
  public BungeeStorage(UserConnection user, ProxiedPlayer player) {
    super(user);
    this.player = player;
    this.currentServer = "";
    if (bossField != null)
      try {
        this.bossbar = (Set<UUID>)bossField.get(player);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }  
  }
  
  public ProxiedPlayer getPlayer() {
    return this.player;
  }
  
  public String getCurrentServer() {
    return this.currentServer;
  }
  
  public void setCurrentServer(String currentServer) {
    this.currentServer = currentServer;
  }
  
  public Set<UUID> getBossbar() {
    return this.bossbar;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    BungeeStorage that = (BungeeStorage)o;
    if (!Objects.equals(this.player, that.player))
      return false; 
    if (!Objects.equals(this.currentServer, that.currentServer))
      return false; 
    return Objects.equals(this.bossbar, that.bossbar);
  }
  
  public int hashCode() {
    int result = (this.player != null) ? this.player.hashCode() : 0;
    result = 31 * result + ((this.currentServer != null) ? this.currentServer.hashCode() : 0);
    result = 31 * result + ((this.bossbar != null) ? this.bossbar.hashCode() : 0);
    return result;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\storage\BungeeStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */