package us.myles.ViaVersion.api.configuration;

import java.util.Map;

public interface ConfigurationProvider {
  void set(String paramString, Object paramObject);
  
  void saveConfig();
  
  void reloadConfig();
  
  Map<String, Object> getValues();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\configuration\ConfigurationProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */