package us.myles.ViaVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.protocol.ProtocolConstants;
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
import us.myles.ViaVersion.bungee.commands.BungeeCommand;
import us.myles.ViaVersion.bungee.commands.BungeeCommandHandler;
import us.myles.ViaVersion.bungee.commands.BungeeCommandSender;
import us.myles.ViaVersion.bungee.platform.BungeeTaskId;
import us.myles.ViaVersion.bungee.platform.BungeeViaAPI;
import us.myles.ViaVersion.bungee.platform.BungeeViaConfig;
import us.myles.ViaVersion.bungee.platform.BungeeViaInjector;
import us.myles.ViaVersion.bungee.platform.BungeeViaLoader;
import us.myles.ViaVersion.bungee.service.ProtocolDetectorService;
import us.myles.ViaVersion.commands.ViaCommandHandler;
import us.myles.ViaVersion.dump.PluginInfo;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.gson.JsonObject;

public class BungeePlugin extends Plugin implements ViaPlatform<ProxiedPlayer>, Listener {
  private final ViaConnectionManager connectionManager = new ViaConnectionManager();
  
  private BungeeViaAPI api;
  
  private BungeeViaConfig config;
  
  public void onLoad() {
    try {
      ProtocolConstants.class.getField("MINECRAFT_1_16_3");
    } catch (NoSuchFieldException e) {
      getLogger().warning("      / \\");
      getLogger().warning("     /   \\");
      getLogger().warning("    /  |  \\");
      getLogger().warning("   /   |   \\         BUNGEECORD IS OUTDATED");
      getLogger().warning("  /         \\   VIAVERSION MAY NOT WORK AS INTENDED");
      getLogger().warning(" /     o     \\");
      getLogger().warning("/_____________\\");
    } 
    this.api = new BungeeViaAPI();
    this.config = new BungeeViaConfig(getDataFolder());
    BungeeCommandHandler commandHandler = new BungeeCommandHandler();
    ProxyServer.getInstance().getPluginManager().registerCommand(this, (Command)new BungeeCommand(commandHandler));
    Via.init(ViaManager.builder()
        .platform(this)
        .injector((ViaInjector)new BungeeViaInjector())
        .loader((ViaPlatformLoader)new BungeeViaLoader(this))
        .commandHandler((ViaCommandHandler)commandHandler)
        .build());
  }
  
  public void onEnable() {
    if (ProxyServer.getInstance().getPluginManager().getPlugin("ViaBackwards") != null)
      MappingDataLoader.enableMappingsCache(); 
    Via.getManager().init();
  }
  
  public String getPlatformName() {
    return getProxy().getName();
  }
  
  public String getPlatformVersion() {
    return getProxy().getVersion();
  }
  
  public boolean isProxy() {
    return true;
  }
  
  public String getPluginVersion() {
    return getDescription().getVersion();
  }
  
  public TaskId runAsync(Runnable runnable) {
    return (TaskId)new BungeeTaskId(Integer.valueOf(getProxy().getScheduler().runAsync(this, runnable).getId()));
  }
  
  public TaskId runSync(Runnable runnable) {
    return runAsync(runnable);
  }
  
  public TaskId runSync(Runnable runnable, Long ticks) {
    return (TaskId)new BungeeTaskId(Integer.valueOf(getProxy().getScheduler().schedule(this, runnable, ticks.longValue() * 50L, TimeUnit.MILLISECONDS).getId()));
  }
  
  public TaskId runRepeatingSync(Runnable runnable, Long ticks) {
    return (TaskId)new BungeeTaskId(Integer.valueOf(getProxy().getScheduler().schedule(this, runnable, 0L, ticks.longValue() * 50L, TimeUnit.MILLISECONDS).getId()));
  }
  
  public void cancelTask(TaskId taskId) {
    if (taskId == null)
      return; 
    if (taskId.getObject() == null)
      return; 
    if (taskId instanceof BungeeTaskId)
      getProxy().getScheduler().cancel(((Integer)taskId.getObject()).intValue()); 
  }
  
  public ViaCommandSender[] getOnlinePlayers() {
    ViaCommandSender[] array = new ViaCommandSender[getProxy().getPlayers().size()];
    int i = 0;
    for (ProxiedPlayer player : getProxy().getPlayers())
      array[i++] = (ViaCommandSender)new BungeeCommandSender((CommandSender)player); 
    return array;
  }
  
  public void sendMessage(UUID uuid, String message) {
    getProxy().getPlayer(uuid).sendMessage(message);
  }
  
  public boolean kickPlayer(UUID uuid, String message) {
    ProxiedPlayer player = getProxy().getPlayer(uuid);
    if (player != null) {
      player.disconnect(message);
      return true;
    } 
    return false;
  }
  
  public boolean isPluginEnabled() {
    return true;
  }
  
  public ViaAPI<ProxiedPlayer> getApi() {
    return (ViaAPI<ProxiedPlayer>)this.api;
  }
  
  public BungeeViaConfig getConf() {
    return this.config;
  }
  
  public ConfigurationProvider getConfigurationProvider() {
    return (ConfigurationProvider)this.config;
  }
  
  public void onReload() {}
  
  public JsonObject getDump() {
    JsonObject platformSpecific = new JsonObject();
    List<PluginInfo> plugins = new ArrayList<>();
    for (Plugin p : ProxyServer.getInstance().getPluginManager().getPlugins())
      plugins.add(new PluginInfo(true, p
            
            .getDescription().getName(), p
            .getDescription().getVersion(), p
            .getDescription().getMain(), 
            Collections.singletonList(p.getDescription().getAuthor()))); 
    platformSpecific.add("plugins", GsonUtil.getGson().toJsonTree(plugins));
    platformSpecific.add("servers", GsonUtil.getGson().toJsonTree(ProtocolDetectorService.getDetectedIds()));
    return platformSpecific;
  }
  
  public boolean isOldClientsAllowed() {
    return true;
  }
  
  public ViaConnectionManager getConnectionManager() {
    return this.connectionManager;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\BungeePlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */