package us.myles.ViaVersion.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.update.UpdateUtil;

public class UpdateListener {
  @Subscribe
  public void onJoin(PostLoginEvent e) {
    if (e.getPlayer().hasPermission("viaversion.update") && 
      Via.getConfig().isCheckForUpdates())
      UpdateUtil.sendUpdateMessage(e.getPlayer().getUniqueId()); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\listeners\UpdateListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */