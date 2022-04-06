package us.myles.ViaVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.ViaVersionConfig;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.configuration.ConfigurationProvider;
import us.myles.ViaVersion.api.data.MappingDataLoader;
import us.myles.ViaVersion.api.platform.TaskId;
import us.myles.ViaVersion.api.platform.ViaConnectionManager;
import us.myles.ViaVersion.api.platform.ViaInjector;
import us.myles.ViaVersion.api.platform.ViaPlatform;
import us.myles.ViaVersion.api.platform.ViaPlatformLoader;
import us.myles.ViaVersion.bukkit.classgenerator.ClassGenerator;
import us.myles.ViaVersion.bukkit.commands.BukkitCommandHandler;
import us.myles.ViaVersion.bukkit.commands.BukkitCommandSender;
import us.myles.ViaVersion.bukkit.platform.BukkitTaskId;
import us.myles.ViaVersion.bukkit.platform.BukkitViaAPI;
import us.myles.ViaVersion.bukkit.platform.BukkitViaConfig;
import us.myles.ViaVersion.bukkit.platform.BukkitViaInjector;
import us.myles.ViaVersion.bukkit.platform.BukkitViaLoader;
import us.myles.ViaVersion.bukkit.util.NMSUtil;
import us.myles.ViaVersion.commands.ViaCommandHandler;
import us.myles.ViaVersion.dump.PluginInfo;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.gson.JsonObject;

public class ViaVersionPlugin extends JavaPlugin implements ViaPlatform<Player> {
  private static ViaVersionPlugin instance;
  
  private final ViaConnectionManager connectionManager = new ViaConnectionManager();
  
  private final BukkitCommandHandler commandHandler;
  
  private final BukkitViaConfig conf;
  
  private final ViaAPI<Player> api = (ViaAPI<Player>)new BukkitViaAPI(this);
  
  private final List<Runnable> queuedTasks = new ArrayList<>();
  
  private final List<Runnable> asyncQueuedTasks = new ArrayList<>();
  
  private final boolean protocolSupport;
  
  private boolean compatSpigotBuild;
  
  private boolean spigot = true;
  
  private boolean lateBind;
  
  public ViaVersionPlugin() {
    instance = this;
    this.commandHandler = new BukkitCommandHandler();
    Via.init(ViaManager.builder()
        .platform(this)
        .commandHandler((ViaCommandHandler)this.commandHandler)
        .injector((ViaInjector)new BukkitViaInjector())
        .loader((ViaPlatformLoader)new BukkitViaLoader(this))
        .build());
    this.conf = new BukkitViaConfig();
    this.protocolSupport = (Bukkit.getPluginManager().getPlugin("ProtocolSupport") != null);
    if (this.protocolSupport) {
      getLogger().info("Hooking into ProtocolSupport, to prevent issues!");
      try {
        BukkitViaInjector.patchLists();
      } catch (Exception e) {
        e.printStackTrace();
      } 
    } 
  }
  
  public void onLoad() {
    try {
      Class.forName("org.spigotmc.SpigotConfig");
    } catch (ClassNotFoundException e) {
      this.spigot = false;
    } 
    try {
      NMSUtil.nms("PacketEncoder").getDeclaredField("version");
      this.compatSpigotBuild = true;
    } catch (Exception e) {
      this.compatSpigotBuild = false;
    } 
    if (getServer().getPluginManager().getPlugin("ViaBackwards") != null)
      MappingDataLoader.enableMappingsCache(); 
    ClassGenerator.generate();
    this.lateBind = !BukkitViaInjector.isBinded();
    getLogger().info("ViaVersion " + getDescription().getVersion() + (this.compatSpigotBuild ? "compat" : "") + " is now loaded" + (this.lateBind ? ", waiting for boot. (late-bind)" : ", injecting!"));
    if (!this.lateBind)
      Via.getManager().init(); 
  }
  
  public void onEnable() {
    if (this.lateBind)
      Via.getManager().init(); 
    getCommand("viaversion").setExecutor((CommandExecutor)this.commandHandler);
    getCommand("viaversion").setTabCompleter((TabCompleter)this.commandHandler);
    if (this.conf.isAntiXRay() && !this.spigot)
      getLogger().info("You have anti-xray on in your config, since you're not using spigot it won't fix xray!"); 
    for (Runnable r : this.queuedTasks)
      Bukkit.getScheduler().runTask((Plugin)this, r); 
    this.queuedTasks.clear();
    for (Runnable r : this.asyncQueuedTasks)
      Bukkit.getScheduler().runTaskAsynchronously((Plugin)this, r); 
    this.asyncQueuedTasks.clear();
  }
  
  public void onDisable() {
    Via.getManager().destroy();
  }
  
  public String getPlatformName() {
    return Bukkit.getServer().getName();
  }
  
  public String getPlatformVersion() {
    return Bukkit.getServer().getVersion();
  }
  
  public String getPluginVersion() {
    return getDescription().getVersion();
  }
  
  public TaskId runAsync(Runnable runnable) {
    if (isPluginEnabled())
      return (TaskId)new BukkitTaskId(Integer.valueOf(getServer().getScheduler().runTaskAsynchronously((Plugin)this, runnable).getTaskId())); 
    this.asyncQueuedTasks.add(runnable);
    return (TaskId)new BukkitTaskId(null);
  }
  
  public TaskId runSync(Runnable runnable) {
    if (isPluginEnabled())
      return (TaskId)new BukkitTaskId(Integer.valueOf(getServer().getScheduler().runTask((Plugin)this, runnable).getTaskId())); 
    this.queuedTasks.add(runnable);
    return (TaskId)new BukkitTaskId(null);
  }
  
  public TaskId runSync(Runnable runnable, Long ticks) {
    return (TaskId)new BukkitTaskId(Integer.valueOf(getServer().getScheduler().runTaskLater((Plugin)this, runnable, ticks.longValue()).getTaskId()));
  }
  
  public TaskId runRepeatingSync(Runnable runnable, Long ticks) {
    return (TaskId)new BukkitTaskId(Integer.valueOf(getServer().getScheduler().runTaskTimer((Plugin)this, runnable, 0L, ticks.longValue()).getTaskId()));
  }
  
  public void cancelTask(TaskId taskId) {
    if (taskId == null)
      return; 
    if (taskId.getObject() == null)
      return; 
    if (taskId instanceof BukkitTaskId)
      getServer().getScheduler().cancelTask(((Integer)taskId.getObject()).intValue()); 
  }
  
  public ViaCommandSender[] getOnlinePlayers() {
    ViaCommandSender[] array = new ViaCommandSender[Bukkit.getOnlinePlayers().size()];
    int i = 0;
    for (Player player : Bukkit.getOnlinePlayers())
      array[i++] = (ViaCommandSender)new BukkitCommandSender((CommandSender)player); 
    return array;
  }
  
  public void sendMessage(UUID uuid, String message) {
    Player player = Bukkit.getPlayer(uuid);
    if (player != null)
      player.sendMessage(message); 
  }
  
  public boolean kickPlayer(UUID uuid, String message) {
    Player player = Bukkit.getPlayer(uuid);
    if (player != null) {
      player.kickPlayer(message);
      return true;
    } 
    return false;
  }
  
  public boolean isPluginEnabled() {
    return Bukkit.getPluginManager().getPlugin("ViaVersion").isEnabled();
  }
  
  public ConfigurationProvider getConfigurationProvider() {
    return (ConfigurationProvider)this.conf;
  }
  
  public void onReload() {
    if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
      getLogger().severe("ViaVersion is already loaded, we're going to kick all the players... because otherwise we'll crash because of ProtocolLib.");
      for (Player player : Bukkit.getOnlinePlayers())
        player.kickPlayer(ChatColor.translateAlternateColorCodes('&', this.conf.getReloadDisconnectMsg())); 
    } else {
      getLogger().severe("ViaVersion is already loaded, this should work fine. If you get any console errors, try rebooting.");
    } 
  }
  
  public JsonObject getDump() {
    JsonObject platformSpecific = new JsonObject();
    List<PluginInfo> plugins = new ArrayList<>();
    for (Plugin p : Bukkit.getPluginManager().getPlugins())
      plugins.add(new PluginInfo(p.isEnabled(), p.getDescription().getName(), p.getDescription().getVersion(), p.getDescription().getMain(), p.getDescription().getAuthors())); 
    platformSpecific.add("plugins", GsonUtil.getGson().toJsonTree(plugins));
    return platformSpecific;
  }
  
  public boolean isOldClientsAllowed() {
    return !this.protocolSupport;
  }
  
  public BukkitViaConfig getConf() {
    return this.conf;
  }
  
  public ViaAPI<Player> getApi() {
    return this.api;
  }
  
  public boolean isCompatSpigotBuild() {
    return this.compatSpigotBuild;
  }
  
  public boolean isSpigot() {
    return this.spigot;
  }
  
  public boolean isProtocolSupport() {
    return this.protocolSupport;
  }
  
  public static ViaVersionPlugin getInstance() {
    return instance;
  }
  
  public ViaConnectionManager getConnectionManager() {
    return this.connectionManager;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\ViaVersionPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */