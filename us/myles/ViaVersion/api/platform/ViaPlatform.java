package us.myles.ViaVersion.api.platform;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.ViaVersionConfig;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.configuration.ConfigurationProvider;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.viaversion.libs.gson.JsonObject;

public interface ViaPlatform<T> {
  Logger getLogger();
  
  String getPlatformName();
  
  String getPlatformVersion();
  
  default boolean isProxy() {
    return false;
  }
  
  String getPluginVersion();
  
  TaskId runAsync(Runnable paramRunnable);
  
  TaskId runSync(Runnable paramRunnable);
  
  TaskId runSync(Runnable paramRunnable, Long paramLong);
  
  TaskId runRepeatingSync(Runnable paramRunnable, Long paramLong);
  
  void cancelTask(TaskId paramTaskId);
  
  ViaCommandSender[] getOnlinePlayers();
  
  void sendMessage(UUID paramUUID, String paramString);
  
  boolean kickPlayer(UUID paramUUID, String paramString);
  
  default boolean disconnect(UserConnection connection, String message) {
    if (connection.isClientSide())
      return false; 
    UUID uuid = ((ProtocolInfo)connection.get(ProtocolInfo.class)).getUuid();
    if (uuid == null)
      return false; 
    return kickPlayer(uuid, message);
  }
  
  boolean isPluginEnabled();
  
  ViaAPI<T> getApi();
  
  ViaVersionConfig getConf();
  
  ConfigurationProvider getConfigurationProvider();
  
  File getDataFolder();
  
  void onReload();
  
  JsonObject getDump();
  
  boolean isOldClientsAllowed();
  
  ViaConnectionManager getConnectionManager();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\platform\ViaPlatform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */