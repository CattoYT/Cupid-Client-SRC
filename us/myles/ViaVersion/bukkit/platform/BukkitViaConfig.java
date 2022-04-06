package us.myles.ViaVersion.bukkit.platform;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.plugin.Plugin;
import us.myles.ViaVersion.AbstractViaConfig;
import us.myles.ViaVersion.api.Via;

public class BukkitViaConfig extends AbstractViaConfig {
  private static final List<String> UNSUPPORTED = Arrays.asList(new String[] { "bungee-ping-interval", "bungee-ping-save", "bungee-servers", "velocity-ping-interval", "velocity-ping-save", "velocity-servers" });
  
  private boolean antiXRay;
  
  private boolean quickMoveActionFix;
  
  private boolean hitboxFix1_9;
  
  private boolean hitboxFix1_14;
  
  private String blockConnectionMethod;
  
  public BukkitViaConfig() {
    super(new File(((Plugin)Via.getPlatform()).getDataFolder(), "config.yml"));
    reloadConfig();
  }
  
  protected void loadFields() {
    super.loadFields();
    this.antiXRay = getBoolean("anti-xray-patch", true);
    this.quickMoveActionFix = getBoolean("quick-move-action-fix", false);
    this.hitboxFix1_9 = getBoolean("change-1_9-hitbox", false);
    this.hitboxFix1_14 = getBoolean("change-1_14-hitbox", false);
    this.blockConnectionMethod = getString("blockconnection-method", "packet");
  }
  
  public URL getDefaultConfigURL() {
    return BukkitViaConfig.class.getClassLoader().getResource("assets/viaversion/config.yml");
  }
  
  protected void handleConfig(Map<String, Object> config) {}
  
  public boolean isAntiXRay() {
    return this.antiXRay;
  }
  
  public boolean is1_12QuickMoveActionFix() {
    return this.quickMoveActionFix;
  }
  
  public boolean is1_9HitboxFix() {
    return this.hitboxFix1_9;
  }
  
  public boolean is1_14HitboxFix() {
    return this.hitboxFix1_14;
  }
  
  public String getBlockConnectionMethod() {
    return this.blockConnectionMethod;
  }
  
  public List<String> getUnsupportedOptions() {
    return UNSUPPORTED;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\platform\BukkitViaConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */