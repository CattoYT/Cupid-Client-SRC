package us.myles.ViaVersion.sponge.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.update.UpdateUtil;

public class UpdateListener {
  @Listener
  public void onJoin(ClientConnectionEvent.Join join) {
    if (join.getTargetEntity().hasPermission("viaversion.update") && 
      Via.getConfig().isCheckForUpdates())
      UpdateUtil.sendUpdateMessage(join.getTargetEntity().getUniqueId()); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\listeners\UpdateListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */