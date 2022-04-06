package us.myles.ViaVersion;

import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import org.slf4j.Logger;
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
import us.myles.ViaVersion.commands.ViaCommandHandler;
import us.myles.ViaVersion.dump.PluginInfo;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.ViaVersion.velocity.command.VelocityCommandHandler;
import us.myles.ViaVersion.velocity.platform.VelocityTaskId;
import us.myles.ViaVersion.velocity.platform.VelocityViaAPI;
import us.myles.ViaVersion.velocity.platform.VelocityViaConfig;
import us.myles.ViaVersion.velocity.platform.VelocityViaInjector;
import us.myles.ViaVersion.velocity.platform.VelocityViaLoader;
import us.myles.ViaVersion.velocity.service.ProtocolDetectorService;
import us.myles.ViaVersion.velocity.util.LoggerWrapper;
import us.myles.viaversion.libs.bungeecordchat.api.chat.TextComponent;
import us.myles.viaversion.libs.bungeecordchat.chat.ComponentSerializer;
import us.myles.viaversion.libs.gson.JsonObject;

@Plugin(id = "viaversion", name = "ViaVersion", version = "3.3.0-20w45a", authors = {"_MylesC", "creeper123123321", "Gerrygames", "KennyTV", "Matsv"}, description = "Allow newer Minecraft versions to connect to an older server version.", url = "https://viaversion.com")
public class VelocityPlugin implements ViaPlatform<Player> {
  public static ProxyServer PROXY;
  
  @Inject
  private ProxyServer proxy;
  
  @Inject
  private Logger loggerslf4j;
  
  @Inject
  @DataDirectory
  private Path configDir;
  
  private VelocityViaAPI api;
  
  private Logger logger;
  
  private VelocityViaConfig conf;
  
  private ViaConnectionManager connectionManager;
  
  @Subscribe
  public void onProxyInit(ProxyInitializeEvent e) {
    PROXY = this.proxy;
    VelocityCommandHandler commandHandler = new VelocityCommandHandler();
    PROXY.getCommandManager().register((Command)commandHandler, new String[] { "viaver", "vvvelocity", "viaversion" });
    this.api = new VelocityViaAPI();
    this.conf = new VelocityViaConfig(this.configDir.toFile());
    this.logger = (Logger)new LoggerWrapper(this.loggerslf4j);
    this.connectionManager = new ViaConnectionManager();
    Via.init(ViaManager.builder()
        .platform(this)
        .commandHandler((ViaCommandHandler)commandHandler)
        .loader((ViaPlatformLoader)new VelocityViaLoader())
        .injector((ViaInjector)new VelocityViaInjector()).build());
    if (this.proxy.getPluginManager().getPlugin("viabackwards").isPresent())
      MappingDataLoader.enableMappingsCache(); 
  }
  
  @Subscribe(order = PostOrder.LAST)
  public void onProxyLateInit(ProxyInitializeEvent e) {
    Via.getManager().init();
  }
  
  public String getPlatformName() {
    String proxyImpl = ProxyServer.class.getPackage().getImplementationTitle();
    return (proxyImpl != null) ? proxyImpl : "Velocity";
  }
  
  public String getPlatformVersion() {
    String version = ProxyServer.class.getPackage().getImplementationVersion();
    return (version != null) ? version : "Unknown";
  }
  
  public boolean isProxy() {
    return true;
  }
  
  public String getPluginVersion() {
    return "3.3.0-20w45a";
  }
  
  public TaskId runAsync(Runnable runnable) {
    return runSync(runnable);
  }
  
  public TaskId runSync(Runnable runnable) {
    return runSync(runnable, Long.valueOf(0L));
  }
  
  public TaskId runSync(Runnable runnable, Long ticks) {
    return (TaskId)new VelocityTaskId(PROXY
        .getScheduler()
        .buildTask(this, runnable)
        .delay(ticks.longValue() * 50L, TimeUnit.MILLISECONDS).schedule());
  }
  
  public TaskId runRepeatingSync(Runnable runnable, Long ticks) {
    return (TaskId)new VelocityTaskId(PROXY
        .getScheduler()
        .buildTask(this, runnable)
        .repeat(ticks.longValue() * 50L, TimeUnit.MILLISECONDS).schedule());
  }
  
  public void cancelTask(TaskId taskId) {
    if (taskId instanceof VelocityTaskId)
      ((VelocityTaskId)taskId).getObject().cancel(); 
  }
  
  public ViaCommandSender[] getOnlinePlayers() {
    return (ViaCommandSender[])PROXY.getAllPlayers().stream()
      .map(us.myles.ViaVersion.velocity.command.VelocityCommandSender::new)
      .toArray(x$0 -> new ViaCommandSender[x$0]);
  }
  
  public void sendMessage(UUID uuid, String message) {
    PROXY.getPlayer(uuid).ifPresent(it -> it.sendMessage(GsonComponentSerializer.INSTANCE.deserialize(ComponentSerializer.toString(TextComponent.fromLegacyText(message)))));
  }
  
  public boolean kickPlayer(UUID uuid, String message) {
    return ((Boolean)PROXY.getPlayer(uuid).map(it -> {
          it.disconnect(GsonComponentSerializer.INSTANCE.deserialize(ComponentSerializer.toString(TextComponent.fromLegacyText(message))));
          return Boolean.valueOf(true);
        }).orElse(Boolean.valueOf(false))).booleanValue();
  }
  
  public boolean isPluginEnabled() {
    return true;
  }
  
  public ConfigurationProvider getConfigurationProvider() {
    return (ConfigurationProvider)this.conf;
  }
  
  public File getDataFolder() {
    return this.configDir.toFile();
  }
  
  public VelocityViaAPI getApi() {
    return this.api;
  }
  
  public VelocityViaConfig getConf() {
    return this.conf;
  }
  
  public void onReload() {}
  
  public JsonObject getDump() {
    JsonObject extra = new JsonObject();
    List<PluginInfo> plugins = new ArrayList<>();
    for (PluginContainer p : PROXY.getPluginManager().getPlugins())
      plugins.add(new PluginInfo(true, p
            
            .getDescription().getName().orElse(p.getDescription().getId()), p
            .getDescription().getVersion().orElse("Unknown Version"), 
            p.getInstance().isPresent() ? p.getInstance().get().getClass().getCanonicalName() : "Unknown", p
            .getDescription().getAuthors())); 
    extra.add("plugins", GsonUtil.getGson().toJsonTree(plugins));
    extra.add("servers", GsonUtil.getGson().toJsonTree(ProtocolDetectorService.getDetectedIds()));
    return extra;
  }
  
  public boolean isOldClientsAllowed() {
    return true;
  }
  
  public Logger getLogger() {
    return this.logger;
  }
  
  public ViaConnectionManager getConnectionManager() {
    return this.connectionManager;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\VelocityPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */