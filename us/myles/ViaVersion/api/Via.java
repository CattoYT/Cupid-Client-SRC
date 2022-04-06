package us.myles.ViaVersion.api;

import com.google.common.base.Preconditions;
import us.myles.ViaVersion.ViaManager;
import us.myles.ViaVersion.api.platform.ViaPlatform;

public class Via {
  private static ViaPlatform platform;
  
  private static ViaManager manager;
  
  public static void init(ViaManager viaManager) {
    Preconditions.checkArgument((manager == null), "ViaManager is already set");
    platform = viaManager.getPlatform();
    manager = viaManager;
  }
  
  public static ViaAPI getAPI() {
    Preconditions.checkArgument((platform != null), "ViaVersion has not loaded the Platform");
    return platform.getApi();
  }
  
  public static ViaVersionConfig getConfig() {
    Preconditions.checkArgument((platform != null), "ViaVersion has not loaded the Platform");
    return platform.getConf();
  }
  
  public static ViaPlatform getPlatform() {
    return platform;
  }
  
  public static ViaManager getManager() {
    return manager;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\Via.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */