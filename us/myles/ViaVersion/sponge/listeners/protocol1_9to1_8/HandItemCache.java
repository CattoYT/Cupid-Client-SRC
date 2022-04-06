package us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.sponge4.Sponge4ItemGrabber;
import us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.sponge5.Sponge5ItemGrabber;

public class HandItemCache implements Runnable {
  public static boolean CACHE = false;
  
  private static Map<UUID, Item> handCache = new ConcurrentHashMap<>();
  
  private static Field GET_DAMAGE;
  
  private static Method GET_ID;
  
  private static ItemGrabber grabber;
  
  static {
    try {
      Class.forName("org.spongepowered.api.event.entity.DisplaceEntityEvent");
      grabber = (ItemGrabber)new Sponge4ItemGrabber();
    } catch (ClassNotFoundException e) {
      grabber = (ItemGrabber)new Sponge5ItemGrabber();
    } 
  }
  
  public static Item getHandItem(UUID player) {
    return handCache.get(player);
  }
  
  public void run() {
    List<UUID> players = new ArrayList<>(handCache.keySet());
    for (Player p : Sponge.getServer().getOnlinePlayers()) {
      handCache.put(p.getUniqueId(), convert(grabber.getItem(p)));
      players.remove(p.getUniqueId());
    } 
    for (UUID uuid : players)
      handCache.remove(uuid); 
  }
  
  public static Item convert(ItemStack itemInHand) {
    if (itemInHand == null)
      return new Item(0, (byte)0, (short)0, null); 
    if (GET_DAMAGE == null)
      try {
        GET_DAMAGE = itemInHand.getClass().getDeclaredField("field_77991_e");
        GET_DAMAGE.setAccessible(true);
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      }  
    if (GET_ID == null)
      try {
        GET_ID = Class.forName("net.minecraft.item.Item").getDeclaredMethod("func_150891_b", new Class[] { Class.forName("net.minecraft.item.Item") });
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }  
    int id = 0;
    if (GET_ID != null)
      try {
        id = ((Integer)GET_ID.invoke(null, new Object[] { itemInHand.getItem() })).intValue();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }  
    int damage = 0;
    if (GET_DAMAGE != null)
      try {
        damage = ((Integer)GET_DAMAGE.get(itemInHand)).intValue();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }  
    return new Item(id, (byte)itemInHand.getQuantity(), (short)damage, null);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\listeners\protocol1_9to1_8\HandItemCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */