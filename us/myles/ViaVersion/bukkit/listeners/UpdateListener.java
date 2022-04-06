package us.myles.ViaVersion.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.update.UpdateUtil;

public class UpdateListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    if (e.getPlayer().hasPermission("viaversion.update") && 
      Via.getConfig().isCheckForUpdates())
      UpdateUtil.sendUpdateMessage(e.getPlayer().getUniqueId()); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\listeners\UpdateListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */