package us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.sponge5;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.ItemGrabber;

public class Sponge5ItemGrabber implements ItemGrabber {
  public ItemStack getItem(Player player) {
    return player.getItemInHand(HandTypes.MAIN_HAND).orElse(null);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\listeners\protocol1_9to1_8\sponge5\Sponge5ItemGrabber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */