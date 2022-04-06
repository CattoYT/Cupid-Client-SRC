package us.myles.ViaVersion.bukkit.listeners.protocol1_9to1_8;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.bukkit.listeners.ViaBukkitListener;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ArmorType;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;

public class ArmorListener extends ViaBukkitListener {
  private static final UUID ARMOR_ATTRIBUTE = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");
  
  public ArmorListener(Plugin plugin) {
    super(plugin, Protocol1_9To1_8.class);
  }
  
  public void sendArmorUpdate(Player player) {
    if (!isOnPipe(player))
      return; 
    int armor = 0;
    for (ItemStack stack : player.getInventory().getArmorContents())
      armor += ArmorType.findById(stack.getTypeId()).getArmorPoints(); 
    PacketWrapper wrapper = new PacketWrapper(75, null, getUserConnection(player));
    try {
      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(player.getEntityId()));
      wrapper.write(Type.INT, Integer.valueOf(1));
      wrapper.write(Type.STRING, "generic.armor");
      wrapper.write(Type.DOUBLE, Double.valueOf(0.0D));
      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(1));
      wrapper.write(Type.UUID, ARMOR_ATTRIBUTE);
      wrapper.write(Type.DOUBLE, Double.valueOf(armor));
      wrapper.write(Type.BYTE, Byte.valueOf((byte)0));
      wrapper.send(Protocol1_9To1_8.class);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent e) {
    HumanEntity human = e.getWhoClicked();
    if (human instanceof Player && e.getInventory() instanceof org.bukkit.inventory.CraftingInventory) {
      Player player = (Player)human;
      if (e.getCurrentItem() != null && 
        ArmorType.isArmor(e.getCurrentItem().getTypeId())) {
        sendDelayedArmorUpdate(player);
        return;
      } 
      if (e.getRawSlot() >= 5 && e.getRawSlot() <= 8)
        sendDelayedArmorUpdate(player); 
    } 
  }
  
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInteract(PlayerInteractEvent e) {
    if (e.getItem() != null && (
      e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      Player player = e.getPlayer();
      Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> sendArmorUpdate(player), 3L);
    } 
  }
  
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onItemBreak(PlayerItemBreakEvent e) {
    sendDelayedArmorUpdate(e.getPlayer());
  }
  
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onJoin(PlayerJoinEvent e) {
    sendDelayedArmorUpdate(e.getPlayer());
  }
  
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onRespawn(PlayerRespawnEvent e) {
    sendDelayedArmorUpdate(e.getPlayer());
  }
  
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onWorldChange(PlayerChangedWorldEvent e) {
    sendArmorUpdate(e.getPlayer());
  }
  
  public void sendDelayedArmorUpdate(Player player) {
    if (!isOnPipe(player))
      return; 
    Via.getPlatform().runSync(() -> sendArmorUpdate(player));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\listeners\protocol1_9to1_8\ArmorListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */