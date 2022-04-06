package us.myles.ViaVersion.bukkit.providers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.bukkit.tasks.protocol1_12to1_11_1.BukkitInventoryUpdateTask;
import us.myles.ViaVersion.bukkit.util.NMSUtil;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.providers.InventoryQuickMoveProvider;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.storage.ItemTransaction;
import us.myles.ViaVersion.util.ReflectionUtil;

public class BukkitInventoryQuickMoveProvider extends InventoryQuickMoveProvider {
  private final Map<UUID, BukkitInventoryUpdateTask> updateTasks = new ConcurrentHashMap<>();
  
  private final boolean supported;
  
  private Class<?> windowClickPacketClass;
  
  private Object clickTypeEnum;
  
  private Method nmsItemMethod;
  
  private Method craftPlayerHandle;
  
  private Field connection;
  
  private Method packetMethod;
  
  public BukkitInventoryQuickMoveProvider() {
    this.supported = isSupported();
    setupReflection();
  }
  
  public boolean registerQuickMoveAction(short windowId, short slotId, short actionId, UserConnection userConnection) {
    if (!this.supported)
      return false; 
    if (slotId < 0)
      return false; 
    if (windowId == 0)
      if (slotId >= 36 && slotId <= 44) {
        int protocolId = ProtocolRegistry.SERVER_PROTOCOL;
        if (protocolId == ProtocolVersion.v1_8.getVersion())
          return false; 
      }  
    ProtocolInfo info = userConnection.getProtocolInfo();
    UUID uuid = info.getUuid();
    BukkitInventoryUpdateTask updateTask = this.updateTasks.get(uuid);
    boolean registered = (updateTask != null);
    if (!registered) {
      updateTask = new BukkitInventoryUpdateTask(this, uuid);
      this.updateTasks.put(uuid, updateTask);
    } 
    updateTask.addItem(windowId, slotId, actionId);
    if (!registered)
      Via.getPlatform().runSync((Runnable)updateTask, Long.valueOf(5L)); 
    return true;
  }
  
  public Object buildWindowClickPacket(Player p, ItemTransaction storage) {
    if (!this.supported)
      return null; 
    InventoryView inv = p.getOpenInventory();
    short slotId = storage.getSlotId();
    Inventory tinv = inv.getTopInventory();
    InventoryType tinvtype = (tinv == null) ? null : tinv.getType();
    if (tinvtype != null) {
      int protocolId = ProtocolRegistry.SERVER_PROTOCOL;
      if (protocolId == ProtocolVersion.v1_8.getVersion() && 
        tinvtype == InventoryType.BREWING)
        if (slotId >= 5 && slotId <= 40)
          slotId = (short)(slotId - 1);  
    } 
    ItemStack itemstack = null;
    if (slotId <= inv.countSlots()) {
      itemstack = inv.getItem(slotId);
    } else {
      String cause = "Too many inventory slots: slotId: " + slotId + " invSlotCount: " + inv.countSlots() + " invType: " + inv.getType() + " topInvType: " + tinvtype;
      Via.getPlatform().getLogger().severe("Failed to get an item to create a window click packet. Please report this issue to the ViaVersion Github: " + cause);
    } 
    Object packet = null;
    try {
      packet = this.windowClickPacketClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
      Object nmsItem = (itemstack == null) ? null : this.nmsItemMethod.invoke(null, new Object[] { itemstack });
      ReflectionUtil.set(packet, "a", Integer.valueOf(storage.getWindowId()));
      ReflectionUtil.set(packet, "slot", Integer.valueOf(slotId));
      ReflectionUtil.set(packet, "button", Integer.valueOf(0));
      ReflectionUtil.set(packet, "d", Short.valueOf(storage.getActionId()));
      ReflectionUtil.set(packet, "item", nmsItem);
      int protocolId = ProtocolRegistry.SERVER_PROTOCOL;
      if (protocolId == ProtocolVersion.v1_8.getVersion()) {
        ReflectionUtil.set(packet, "shift", Integer.valueOf(1));
      } else if (protocolId >= ProtocolVersion.v1_9.getVersion()) {
        ReflectionUtil.set(packet, "shift", this.clickTypeEnum);
      } 
    } catch (Exception e) {
      Via.getPlatform().getLogger().log(Level.SEVERE, "Failed to create a window click packet. Please report this issue to the ViaVersion Github: " + e.getMessage(), e);
    } 
    return packet;
  }
  
  public boolean sendPacketToServer(Player p, Object packet) {
    if (packet == null)
      return true; 
    try {
      Object entityPlayer = this.craftPlayerHandle.invoke(p, new Object[0]);
      Object playerConnection = this.connection.get(entityPlayer);
      this.packetMethod.invoke(playerConnection, new Object[] { packet });
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      e.printStackTrace();
      return false;
    } 
    return true;
  }
  
  public void onTaskExecuted(UUID uuid) {
    this.updateTasks.remove(uuid);
  }
  
  private void setupReflection() {
    if (!this.supported)
      return; 
    try {
      this.windowClickPacketClass = NMSUtil.nms("PacketPlayInWindowClick");
      int protocolId = ProtocolRegistry.SERVER_PROTOCOL;
      if (protocolId >= ProtocolVersion.v1_9.getVersion()) {
        Class<?> eclassz = NMSUtil.nms("InventoryClickType");
        Object[] constants = eclassz.getEnumConstants();
        this.clickTypeEnum = constants[1];
      } 
      Class<?> craftItemStack = NMSUtil.obc("inventory.CraftItemStack");
      this.nmsItemMethod = craftItemStack.getDeclaredMethod("asNMSCopy", new Class[] { ItemStack.class });
    } catch (Exception e) {
      throw new RuntimeException("Couldn't find required inventory classes", e);
    } 
    try {
      this.craftPlayerHandle = NMSUtil.obc("entity.CraftPlayer").getDeclaredMethod("getHandle", new Class[0]);
    } catch (NoSuchMethodException|ClassNotFoundException e) {
      throw new RuntimeException("Couldn't find CraftPlayer", e);
    } 
    try {
      this.connection = NMSUtil.nms("EntityPlayer").getDeclaredField("playerConnection");
    } catch (NoSuchFieldException|ClassNotFoundException e) {
      throw new RuntimeException("Couldn't find Player Connection", e);
    } 
    try {
      this.packetMethod = NMSUtil.nms("PlayerConnection").getDeclaredMethod("a", new Class[] { this.windowClickPacketClass });
    } catch (NoSuchMethodException|ClassNotFoundException e) {
      throw new RuntimeException("Couldn't find CraftPlayer", e);
    } 
  }
  
  private boolean isSupported() {
    int protocolId = ProtocolRegistry.SERVER_PROTOCOL;
    if (protocolId >= ProtocolVersion.v1_8.getVersion() && protocolId <= ProtocolVersion.v1_11_1.getVersion())
      return true; 
    return false;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\providers\BukkitInventoryQuickMoveProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */