package us.myles.ViaVersion.bukkit.platform;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import us.myles.ViaVersion.ViaVersionPlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.platform.ViaPlatformLoader;
import us.myles.ViaVersion.api.platform.providers.Provider;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.bukkit.classgenerator.ClassGenerator;
import us.myles.ViaVersion.bukkit.listeners.UpdateListener;
import us.myles.ViaVersion.bukkit.listeners.multiversion.PlayerSneakListener;
import us.myles.ViaVersion.bukkit.listeners.protocol1_15to1_14_4.EntityToggleGlideListener;
import us.myles.ViaVersion.bukkit.listeners.protocol1_9to1_8.ArmorListener;
import us.myles.ViaVersion.bukkit.listeners.protocol1_9to1_8.BlockListener;
import us.myles.ViaVersion.bukkit.listeners.protocol1_9to1_8.DeathListener;
import us.myles.ViaVersion.bukkit.listeners.protocol1_9to1_8.HandItemCache;
import us.myles.ViaVersion.bukkit.listeners.protocol1_9to1_8.PaperPatch;
import us.myles.ViaVersion.bukkit.providers.BukkitBlockConnectionProvider;
import us.myles.ViaVersion.bukkit.providers.BukkitInventoryQuickMoveProvider;
import us.myles.ViaVersion.bukkit.providers.BukkitViaBulkChunkTranslator;
import us.myles.ViaVersion.bukkit.providers.BukkitViaMovementTransmitter;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.providers.InventoryQuickMoveProvider;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.providers.BlockConnectionProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BulkChunkTranslatorProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;

public class BukkitViaLoader implements ViaPlatformLoader {
  private final ViaVersionPlugin plugin;
  
  private final Set<Listener> listeners = new HashSet<>();
  
  private final Set<BukkitTask> tasks = new HashSet<>();
  
  private HandItemCache handItemCache;
  
  public BukkitViaLoader(ViaVersionPlugin plugin) {
    this.plugin = plugin;
  }
  
  public void registerListener(Listener listener) {
    Bukkit.getPluginManager().registerEvents(storeListener(listener), (Plugin)this.plugin);
  }
  
  public <T extends Listener> T storeListener(T listener) {
    this.listeners.add((Listener)listener);
    return listener;
  }
  
  public void load() {
    registerListener((Listener)new UpdateListener());
    ViaVersionPlugin plugin = (ViaVersionPlugin)Bukkit.getPluginManager().getPlugin("ViaVersion");
    ClassGenerator.registerPSConnectListener(plugin);
    if (ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_9.getVersion()) {
      ((ArmorListener)storeListener(new ArmorListener((Plugin)plugin))).register();
      ((DeathListener)storeListener(new DeathListener((Plugin)plugin))).register();
      ((BlockListener)storeListener(new BlockListener((Plugin)plugin))).register();
      if (plugin.getConf().isItemCache()) {
        this.handItemCache = new HandItemCache();
        this.tasks.add(this.handItemCache.runTaskTimerAsynchronously((Plugin)plugin, 2L, 2L));
      } 
    } 
    if (ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_14.getVersion()) {
      boolean use1_9Fix = (plugin.getConf().is1_9HitboxFix() && ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_9.getVersion());
      if (use1_9Fix || plugin.getConf().is1_14HitboxFix())
        try {
          ((PlayerSneakListener)storeListener(new PlayerSneakListener(plugin, use1_9Fix, plugin.getConf().is1_14HitboxFix()))).register();
        } catch (ReflectiveOperationException e) {
          Via.getPlatform().getLogger().warning("Could not load hitbox fix - please report this on our GitHub");
          e.printStackTrace();
        }  
    } 
    if (ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_15.getVersion())
      try {
        Class.forName("org.bukkit.event.entity.EntityToggleGlideEvent");
        ((EntityToggleGlideListener)storeListener(new EntityToggleGlideListener(plugin))).register();
      } catch (ClassNotFoundException classNotFoundException) {} 
    if ((Bukkit.getVersion().toLowerCase(Locale.ROOT).contains("paper") || 
      Bukkit.getVersion().toLowerCase(Locale.ROOT).contains("taco") || 
      Bukkit.getVersion().toLowerCase(Locale.ROOT).contains("torch")) && ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_12
      .getVersion()) {
      plugin.getLogger().info("Enabling Paper/TacoSpigot/Torch patch: Fixes block placement.");
      ((PaperPatch)storeListener(new PaperPatch((Plugin)plugin))).register();
    } 
    if (ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_9.getVersion()) {
      Via.getManager().getProviders().use(BulkChunkTranslatorProvider.class, (Provider)new BukkitViaBulkChunkTranslator());
      Via.getManager().getProviders().use(MovementTransmitterProvider.class, (Provider)new BukkitViaMovementTransmitter());
      Via.getManager().getProviders().use(HandItemProvider.class, (Provider)new HandItemProvider() {
            public Item getHandItem(UserConnection info) {
              if (BukkitViaLoader.this.handItemCache != null)
                return BukkitViaLoader.this.handItemCache.getHandItem(info.getProtocolInfo().getUuid()); 
              try {
                return Bukkit.getScheduler().callSyncMethod(Bukkit.getPluginManager().getPlugin("ViaVersion"), () -> {
                      UUID playerUUID = info.getProtocolInfo().getUuid();
                      Player player = Bukkit.getPlayer(playerUUID);
                      return (player != null) ? HandItemCache.convert(player.getItemInHand()) : null;
                    }).get(10L, TimeUnit.SECONDS);
              } catch (Exception e) {
                Via.getPlatform().getLogger().severe("Error fetching hand item: " + e.getClass().getName());
                if (Via.getManager().isDebug())
                  e.printStackTrace(); 
                return null;
              } 
            }
          });
    } 
    if (ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_12.getVersion() && 
      plugin.getConf().is1_12QuickMoveActionFix())
      Via.getManager().getProviders().use(InventoryQuickMoveProvider.class, (Provider)new BukkitInventoryQuickMoveProvider()); 
    if (ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_13.getVersion() && 
      Via.getConfig().getBlockConnectionMethod().equalsIgnoreCase("world")) {
      BukkitBlockConnectionProvider blockConnectionProvider = new BukkitBlockConnectionProvider();
      Via.getManager().getProviders().use(BlockConnectionProvider.class, (Provider)blockConnectionProvider);
      ConnectionData.blockConnectionProvider = (BlockConnectionProvider)blockConnectionProvider;
    } 
  }
  
  public void unload() {
    for (Listener listener : this.listeners)
      HandlerList.unregisterAll(listener); 
    this.listeners.clear();
    for (BukkitTask task : this.tasks)
      task.cancel(); 
    this.tasks.clear();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\platform\BukkitViaLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */