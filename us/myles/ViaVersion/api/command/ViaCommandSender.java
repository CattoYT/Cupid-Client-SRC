package us.myles.ViaVersion.api.command;

import java.util.UUID;

public interface ViaCommandSender {
  boolean hasPermission(String paramString);
  
  void sendMessage(String paramString);
  
  UUID getUUID();
  
  String getName();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\command\ViaCommandSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */