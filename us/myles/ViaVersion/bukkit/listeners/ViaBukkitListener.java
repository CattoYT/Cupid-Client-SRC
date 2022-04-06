package us.myles.ViaVersion.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import us.myles.ViaVersion.api.ViaListener;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.Protocol;

public class ViaBukkitListener extends ViaListener implements Listener {
  private final Plugin plugin;
  
  public ViaBukkitListener(Plugin plugin, Class<? extends Protocol> requiredPipeline) {
    super(requiredPipeline);
    this.plugin = plugin;
  }
  
  protected UserConnection getUserConnection(Player player) {
    return getUserConnection(player.getUniqueId());
  }
  
  protected boolean isOnPipe(Player player) {
    return isOnPipe(player.getUniqueId());
  }
  
  public void register() {
    if (isRegistered())
      return; 
    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    setRegistered(true);
  }
  
  public Plugin getPlugin() {
    return this.plugin;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\listeners\ViaBukkitListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */