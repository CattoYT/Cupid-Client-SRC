package us.myles.ViaVersion.bukkit.listeners.protocol1_9to1_8;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.bukkit.listeners.ViaBukkitListener;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;

public class DeathListener extends ViaBukkitListener {
  public DeathListener(Plugin plugin) {
    super(plugin, Protocol1_9To1_8.class);
  }
  
  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onDeath(PlayerDeathEvent e) {
    Player p = e.getEntity();
    if (isOnPipe(p) && Via.getConfig().isShowNewDeathMessages() && checkGamerule(p.getWorld()) && e.getDeathMessage() != null)
      sendPacket(p, e.getDeathMessage()); 
  }
  
  public boolean checkGamerule(World w) {
    try {
      return Boolean.parseBoolean(w.getGameRuleValue("showDeathMessages"));
    } catch (Exception e) {
      return false;
    } 
  }
  
  private void sendPacket(final Player p, final String msg) {
    Via.getPlatform().runSync(new Runnable() {
          public void run() {
            UserConnection userConnection = DeathListener.this.getUserConnection(p);
            if (userConnection != null) {
              PacketWrapper wrapper = new PacketWrapper(44, null, userConnection);
              try {
                wrapper.write((Type)Type.VAR_INT, Integer.valueOf(2));
                wrapper.write((Type)Type.VAR_INT, Integer.valueOf(p.getEntityId()));
                wrapper.write(Type.INT, Integer.valueOf(p.getEntityId()));
                Protocol1_9To1_8.FIX_JSON.write(wrapper, msg);
                wrapper.send(Protocol1_9To1_8.class);
              } catch (Exception e) {
                e.printStackTrace();
              } 
            } 
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\listeners\protocol1_9to1_8\DeathListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */