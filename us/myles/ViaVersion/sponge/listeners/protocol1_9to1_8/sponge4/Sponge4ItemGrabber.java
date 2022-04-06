package us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.sponge4;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.ItemGrabber;

public class Sponge4ItemGrabber implements ItemGrabber {
  public ItemStack getItem(Player player) {
    return player.getItemInHand().orElse(null);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\listeners\protocol1_9to1_8\sponge4\Sponge4ItemGrabber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */