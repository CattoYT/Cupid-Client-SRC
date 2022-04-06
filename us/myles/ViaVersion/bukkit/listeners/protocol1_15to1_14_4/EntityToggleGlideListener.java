package us.myles.ViaVersion.bukkit.listeners.protocol1_15to1_14_4;

import java.util.Arrays;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import us.myles.ViaVersion.ViaVersionPlugin;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_14;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_14;
import us.myles.ViaVersion.bukkit.listeners.ViaBukkitListener;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;

public class EntityToggleGlideListener extends ViaBukkitListener {
  private boolean swimmingMethodExists;
  
  public EntityToggleGlideListener(ViaVersionPlugin plugin) {
    super((Plugin)plugin, Protocol1_15To1_14_4.class);
    try {
      Player.class.getMethod("isSwimming", new Class[0]);
      this.swimmingMethodExists = true;
    } catch (NoSuchMethodException noSuchMethodException) {}
  }
  
  @EventHandler(priority = EventPriority.MONITOR)
  public void entityToggleGlide(EntityToggleGlideEvent event) {
    if (!(event.getEntity() instanceof Player))
      return; 
    Player player = (Player)event.getEntity();
    if (!isOnPipe(player))
      return; 
    if (event.isGliding() && event.isCancelled()) {
      PacketWrapper packet = new PacketWrapper(68, null, getUserConnection(player));
      try {
        packet.write((Type)Type.VAR_INT, Integer.valueOf(player.getEntityId()));
        byte bitmask = 0;
        if (player.getFireTicks() > 0)
          bitmask = (byte)(bitmask | 0x1); 
        if (player.isSneaking())
          bitmask = (byte)(bitmask | 0x2); 
        if (player.isSprinting())
          bitmask = (byte)(bitmask | 0x8); 
        if (this.swimmingMethodExists && player.isSwimming())
          bitmask = (byte)(bitmask | 0x10); 
        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
          bitmask = (byte)(bitmask | 0x20); 
        if (player.isGlowing())
          bitmask = (byte)(bitmask | 0x40); 
        packet.write(Types1_14.METADATA_LIST, Arrays.asList(new Metadata[] { new Metadata(0, (MetaType)MetaType1_14.Byte, Byte.valueOf(bitmask)) }));
        packet.send(Protocol1_15To1_14_4.class);
      } catch (Exception e) {
        e.printStackTrace();
      } 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\listeners\protocol1_15to1_14_4\EntityToggleGlideListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */