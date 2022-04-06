package us.myles.ViaVersion.api.platform;

import us.myles.viaversion.libs.gson.JsonObject;

public interface ViaInjector {
  void inject() throws Exception;
  
  void uninject() throws Exception;
  
  int getServerProtocolVersion() throws Exception;
  
  String getEncoderName();
  
  String getDecoderName();
  
  JsonObject getDump();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\platform\ViaInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */