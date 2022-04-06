package us.myles.ViaVersion.bungee.platform;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import us.myles.ViaVersion.AbstractViaConfig;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.bungee.providers.BungeeVersionProvider;

public class BungeeViaConfig extends AbstractViaConfig {
  private static final List<String> UNSUPPORTED = Arrays.asList(new String[] { "nms-player-ticking", "item-cache", "anti-xray-patch", "quick-move-action-fix", "velocity-ping-interval", "velocity-ping-save", "velocity-servers", "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox" });
  
  private int bungeePingInterval;
  
  private boolean bungeePingSave;
  
  private Map<String, Integer> bungeeServerProtocols;
  
  public BungeeViaConfig(File configFile) {
    super(new File(configFile, "config.yml"));
    reloadConfig();
  }
  
  protected void loadFields() {
    super.loadFields();
    this.bungeePingInterval = getInt("bungee-ping-interval", 60);
    this.bungeePingSave = getBoolean("bungee-ping-save", true);
    this.bungeeServerProtocols = (Map<String, Integer>)get("bungee-servers", Map.class, new HashMap<>());
  }
  
  public URL getDefaultConfigURL() {
    return BungeeViaConfig.class.getClassLoader().getResource("assets/viaversion/config.yml");
  }
  
  protected void handleConfig(Map<String, Object> config) {
    Map<String, Object> servers;
    if (!(config.get("bungee-servers") instanceof Map)) {
      servers = new HashMap<>();
    } else {
      servers = (Map<String, Object>)config.get("bungee-servers");
    } 
    for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)new HashSet(servers.entrySet())) {
      if (!(entry.getValue() instanceof Integer)) {
        if (entry.getValue() instanceof String) {
          ProtocolVersion found = ProtocolVersion.getClosest((String)entry.getValue());
          if (found != null) {
            servers.put(entry.getKey(), Integer.valueOf(found.getVersion()));
            continue;
          } 
          servers.remove(entry.getKey());
          continue;
        } 
        servers.remove(entry.getKey());
      } 
    } 
    if (!servers.containsKey("default"))
      servers.put("default", Integer.valueOf(BungeeVersionProvider.getLowestSupportedVersion())); 
    config.put("bungee-servers", servers);
  }
  
  public List<String> getUnsupportedOptions() {
    return UNSUPPORTED;
  }
  
  public boolean isItemCache() {
    return false;
  }
  
  public boolean isNMSPlayerTicking() {
    return false;
  }
  
  public int getBungeePingInterval() {
    return this.bungeePingInterval;
  }
  
  public boolean isBungeePingSave() {
    return this.bungeePingSave;
  }
  
  public Map<String, Integer> getBungeeServerProtocols() {
    return this.bungeeServerProtocols;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\platform\BungeeViaConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */