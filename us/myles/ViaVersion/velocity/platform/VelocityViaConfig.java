package us.myles.ViaVersion.velocity.platform;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import us.myles.ViaVersion.AbstractViaConfig;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

public class VelocityViaConfig extends AbstractViaConfig {
  private static final List<String> UNSUPPORTED = Arrays.asList(new String[] { "nms-player-ticking", "item-cache", "anti-xray-patch", "quick-move-action-fix", "bungee-ping-interval", "bungee-ping-save", "bungee-servers", "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox" });
  
  private int velocityPingInterval;
  
  private boolean velocityPingSave;
  
  private Map<String, Integer> velocityServerProtocols;
  
  public VelocityViaConfig(File configFile) {
    super(new File(configFile, "config.yml"));
    reloadConfig();
  }
  
  protected void loadFields() {
    super.loadFields();
    this.velocityPingInterval = getInt("velocity-ping-interval", 60);
    this.velocityPingSave = getBoolean("velocity-ping-save", true);
    this.velocityServerProtocols = (Map<String, Integer>)get("velocity-servers", Map.class, new HashMap<>());
  }
  
  public URL getDefaultConfigURL() {
    return getClass().getClassLoader().getResource("assets/viaversion/config.yml");
  }
  
  protected void handleConfig(Map<String, Object> config) {
    Map<String, Object> servers;
    if (!(config.get("velocity-servers") instanceof Map)) {
      servers = new HashMap<>();
    } else {
      servers = (Map<String, Object>)config.get("velocity-servers");
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
      try {
        servers.put("default", Integer.valueOf(VelocityViaInjector.getLowestSupportedProtocolVersion()));
      } catch (Exception e) {
        e.printStackTrace();
      }  
    config.put("velocity-servers", servers);
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
  
  public int getVelocityPingInterval() {
    return this.velocityPingInterval;
  }
  
  public boolean isVelocityPingSave() {
    return this.velocityPingSave;
  }
  
  public Map<String, Integer> getVelocityServerProtocols() {
    return this.velocityServerProtocols;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\platform\VelocityViaConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */