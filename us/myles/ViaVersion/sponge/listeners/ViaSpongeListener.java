package us.myles.ViaVersion.sponge.listeners;

import java.lang.reflect.Field;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import us.myles.ViaVersion.SpongePlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaListener;
import us.myles.ViaVersion.api.protocol.Protocol;

public class ViaSpongeListener extends ViaListener {
  private static Field entityIdField;
  
  private final SpongePlugin plugin;
  
  public ViaSpongeListener(SpongePlugin plugin, Class<? extends Protocol> requiredPipeline) {
    super(requiredPipeline);
    this.plugin = plugin;
  }
  
  public void register() {
    if (isRegistered())
      return; 
    Sponge.getEventManager().registerListeners(this.plugin, this);
    setRegistered(true);
  }
  
  protected int getEntityId(Player p) {
    try {
      if (entityIdField == null) {
        entityIdField = p.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("field_145783_c");
        entityIdField.setAccessible(true);
      } 
      return entityIdField.getInt(p);
    } catch (Exception e) {
      Via.getPlatform().getLogger().severe("Could not get the entity id, please report this on our Github");
      e.printStackTrace();
      Via.getPlatform().getLogger().severe("Could not get the entity id, please report this on our Github");
      return -1;
    } 
  }
  
  public SpongePlugin getPlugin() {
    return this.plugin;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\listeners\ViaSpongeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */