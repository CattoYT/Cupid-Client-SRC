package us.myles.ViaVersion.api.command;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface ViaVersionCommand {
  void registerSubCommand(ViaSubCommand paramViaSubCommand) throws Exception;
  
  boolean hasSubCommand(String paramString);
  
  @Nullable
  ViaSubCommand getSubCommand(String paramString);
  
  boolean onCommand(ViaCommandSender paramViaCommandSender, String[] paramArrayOfString);
  
  List<String> onTabComplete(ViaCommandSender paramViaCommandSender, String[] paramArrayOfString);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\command\ViaVersionCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */