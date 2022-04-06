package us.myles.ViaVersion.bukkit.listeners.protocol1_9to1_8;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import us.myles.ViaVersion.bukkit.listeners.ViaBukkitListener;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;

public class PaperPatch extends ViaBukkitListener {
  public PaperPatch(Plugin plugin) {
    super(plugin, Protocol1_9To1_8.class);
  }
  
  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void onPlace(BlockPlaceEvent e) {
    if (isOnPipe(e.getPlayer())) {
      Location location = e.getPlayer().getLocation();
      Location diff = location.clone().subtract(e.getBlock().getLocation().add(0.5D, 0.0D, 0.5D));
      Material block = e.getBlockPlaced().getType();
      if (isPlacable(block))
        return; 
      if (location.getBlock().equals(e.getBlock())) {
        e.setCancelled(true);
      } else if (location.getBlock().getRelative(BlockFace.UP).equals(e.getBlock())) {
        e.setCancelled(true);
      } else if (Math.abs(diff.getX()) <= 0.8D && Math.abs(diff.getZ()) <= 0.8D) {
        if (diff.getY() <= 0.1D && diff.getY() >= -0.1D) {
          e.setCancelled(true);
          return;
        } 
        BlockFace relative = e.getBlockAgainst().getFace(e.getBlock());
        if (relative == BlockFace.UP && 
          diff.getY() < 1.0D && diff.getY() >= 0.0D)
          e.setCancelled(true); 
      } 
    } 
  }
  
  private boolean isPlacable(Material material) {
    if (!material.isSolid())
      return true; 
    switch (material.getId()) {
      case 63:
      case 68:
      case 176:
      case 177:
        return true;
    } 
    return false;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\listeners\protocol1_9to1_8\PaperPatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */