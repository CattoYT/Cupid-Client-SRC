package us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.sponge5;

import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.world.World;
import us.myles.ViaVersion.SpongePlugin;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ArmorType;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.sponge.listeners.ViaSpongeListener;

public class Sponge5ArmorListener extends ViaSpongeListener {
  private static final UUID ARMOR_ATTRIBUTE = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");
  
  public Sponge5ArmorListener(SpongePlugin plugin) {
    super(plugin, Protocol1_9To1_8.class);
  }
  
  public void sendArmorUpdate(Player player) {
    if (!isOnPipe(player.getUniqueId()))
      return; 
    int armor = 0;
    armor += calculate(player.getHelmet());
    armor += calculate(player.getChestplate());
    armor += calculate(player.getLeggings());
    armor += calculate(player.getBoots());
    PacketWrapper wrapper = new PacketWrapper(75, null, getUserConnection(player.getUniqueId()));
    try {
      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(getEntityId(player)));
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
  
  private int calculate(Optional<ItemStack> itemStack) {
    if (itemStack.isPresent())
      return ArmorType.findByType(((ItemStack)itemStack.get()).getItem().getType().getId()).getArmorPoints(); 
    return 0;
  }
  
  @Listener
  public void onInventoryClick(ClickInventoryEvent e, @Root Player player) {
    for (SlotTransaction transaction : e.getTransactions()) {
      if (ArmorType.isArmor(((ItemStackSnapshot)transaction.getFinal()).getType().getId()) || 
        ArmorType.isArmor(((ItemStackSnapshot)e.getCursorTransaction().getFinal()).getType().getId())) {
        sendDelayedArmorUpdate(player);
        break;
      } 
    } 
  }
  
  @Listener
  public void onInteract(InteractEvent event, @Root Player player) {
    if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && 
      ArmorType.isArmor(((ItemStack)player.getItemInHand(HandTypes.MAIN_HAND).get()).getItem().getId()))
      sendDelayedArmorUpdate(player); 
  }
  
  @Listener
  public void onJoin(ClientConnectionEvent.Join e) {
    sendArmorUpdate(e.getTargetEntity());
  }
  
  @Listener
  public void onRespawn(RespawnPlayerEvent e) {
    sendDelayedArmorUpdate(e.getTargetEntity());
  }
  
  @Listener
  public void onWorldChange(MoveEntityEvent.Teleport e) {
    if (!(e.getTargetEntity() instanceof Player))
      return; 
    if (!((World)e.getFromTransform().getExtent()).getUniqueId().equals(((World)e.getToTransform().getExtent()).getUniqueId()))
      sendArmorUpdate((Player)e.getTargetEntity()); 
  }
  
  public void sendDelayedArmorUpdate(final Player player) {
    if (!isOnPipe(player.getUniqueId()))
      return; 
    Via.getPlatform().runSync(new Runnable() {
          public void run() {
            Sponge5ArmorListener.this.sendArmorUpdate(player);
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\listeners\protocol1_9to1_8\sponge5\Sponge5ArmorListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */