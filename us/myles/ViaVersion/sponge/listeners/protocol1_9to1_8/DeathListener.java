package us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8;

import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.world.World;
import us.myles.ViaVersion.SpongePlugin;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.sponge.listeners.ViaSpongeListener;

public class DeathListener extends ViaSpongeListener {
  public DeathListener(SpongePlugin plugin) {
    super(plugin, Protocol1_9To1_8.class);
  }
  
  @Listener(order = Order.LAST)
  public void onDeath(DestructEntityEvent.Death e) {
    if (!(e.getTargetEntity() instanceof Player))
      return; 
    Player p = (Player)e.getTargetEntity();
    if (isOnPipe(p.getUniqueId()) && Via.getConfig().isShowNewDeathMessages() && checkGamerule(p.getWorld()))
      sendPacket(p, e.getMessage().toPlain()); 
  }
  
  public boolean checkGamerule(World w) {
    Optional<String> gamerule = w.getGameRule("showDeathMessages");
    if (gamerule.isPresent())
      try {
        return Boolean.parseBoolean(gamerule.get());
      } catch (Exception e) {
        return false;
      }  
    return false;
  }
  
  private void sendPacket(final Player p, final String msg) {
    Via.getPlatform().runSync(new Runnable() {
          public void run() {
            PacketWrapper wrapper = new PacketWrapper(44, null, DeathListener.this.getUserConnection(p.getUniqueId()));
            try {
              int entityId = DeathListener.this.getEntityId(p);
              wrapper.write((Type)Type.VAR_INT, Integer.valueOf(2));
              wrapper.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
              wrapper.write(Type.INT, Integer.valueOf(entityId));
              Protocol1_9To1_8.FIX_JSON.write(wrapper, msg);
              wrapper.send(Protocol1_9To1_8.class);
            } catch (Exception e) {
              e.printStackTrace();
            } 
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\listeners\protocol1_9to1_8\DeathListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */