package us.myles.ViaVersion.bungee.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import us.myles.ViaVersion.BungeePlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.bungee.platform.BungeeViaConfig;
import us.myles.ViaVersion.bungee.providers.BungeeVersionProvider;

public class ProtocolDetectorService implements Runnable {
  private static final Map<String, Integer> detectedProtocolIds = new ConcurrentHashMap<>();
  
  private static ProtocolDetectorService instance;
  
  private final BungeePlugin plugin;
  
  public ProtocolDetectorService(BungeePlugin plugin) {
    this.plugin = plugin;
    instance = this;
  }
  
  public static Integer getProtocolId(String serverName) {
    Map<String, Integer> servers = ((BungeeViaConfig)Via.getConfig()).getBungeeServerProtocols();
    Integer protocol = servers.get(serverName);
    if (protocol != null)
      return protocol; 
    Integer detectedProtocol = detectedProtocolIds.get(serverName);
    if (detectedProtocol != null)
      return detectedProtocol; 
    Integer defaultProtocol = servers.get("default");
    if (defaultProtocol != null)
      return defaultProtocol; 
    return Integer.valueOf(BungeeVersionProvider.getLowestSupportedVersion());
  }
  
  public void run() {
    for (Map.Entry<String, ServerInfo> lists : (Iterable<Map.Entry<String, ServerInfo>>)this.plugin.getProxy().getServers().entrySet())
      probeServer(lists.getValue()); 
  }
  
  public static void probeServer(ServerInfo serverInfo) {
    final String key = serverInfo.getName();
    serverInfo.ping(new Callback<ServerPing>() {
          public void done(ServerPing serverPing, Throwable throwable) {
            if (throwable == null && serverPing != null && serverPing.getVersion() != null)
              if (serverPing.getVersion().getProtocol() > 0) {
                ProtocolDetectorService.detectedProtocolIds.put(key, Integer.valueOf(serverPing.getVersion().getProtocol()));
                if (((BungeeViaConfig)Via.getConfig()).isBungeePingSave()) {
                  Map<String, Integer> servers = ((BungeeViaConfig)Via.getConfig()).getBungeeServerProtocols();
                  Integer protocol = servers.get(key);
                  if (protocol != null && protocol.intValue() == serverPing.getVersion().getProtocol())
                    return; 
                  synchronized (Via.getPlatform().getConfigurationProvider()) {
                    servers.put(key, Integer.valueOf(serverPing.getVersion().getProtocol()));
                  } 
                  Via.getPlatform().getConfigurationProvider().saveConfig();
                } 
              }  
          }
        });
  }
  
  public static Map<String, Integer> getDetectedIds() {
    return new HashMap<>(detectedProtocolIds);
  }
  
  public static ProtocolDetectorService getInstance() {
    return instance;
  }
  
  public BungeePlugin getPlugin() {
    return this.plugin;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\service\ProtocolDetectorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */