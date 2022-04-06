package us.myles.ViaVersion.sponge.platform;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.plugin.PluginContainer;
import us.myles.ViaVersion.AbstractViaConfig;

public class SpongeViaConfig extends AbstractViaConfig {
  private static final List<String> UNSUPPORTED = Arrays.asList(new String[] { 
        "anti-xray-patch", "bungee-ping-interval", "bungee-ping-save", "bungee-servers", "velocity-ping-interval", "velocity-ping-save", "velocity-servers", "quick-move-action-fix", "change-1_9-hitbox", "change-1_14-hitbox", 
        "blockconnection-method" });
  
  private final PluginContainer pluginContainer;
  
  public SpongeViaConfig(PluginContainer pluginContainer, File configFile) {
    super(new File(configFile, "config.yml"));
    this.pluginContainer = pluginContainer;
    reloadConfig();
  }
  
  public URL getDefaultConfigURL() {
    Optional<Asset> config = this.pluginContainer.getAsset("config.yml");
    if (!config.isPresent())
      throw new IllegalArgumentException("Default config is missing from jar"); 
    return ((Asset)config.get()).getUrl();
  }
  
  protected void handleConfig(Map<String, Object> config) {}
  
  public List<String> getUnsupportedOptions() {
    return UNSUPPORTED;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\platform\SpongeViaConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */