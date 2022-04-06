package us.myles.ViaVersion.api.command;

import java.util.Collections;
import java.util.List;
import us.myles.ViaVersion.commands.ViaCommandHandler;

public abstract class ViaSubCommand {
  public abstract String name();
  
  public abstract String description();
  
  public String usage() {
    return name();
  }
  
  public String permission() {
    return "viaversion.admin";
  }
  
  public abstract boolean execute(ViaCommandSender paramViaCommandSender, String[] paramArrayOfString);
  
  public List<String> onTabComplete(ViaCommandSender sender, String[] args) {
    return Collections.emptyList();
  }
  
  public String color(String s) {
    return ViaCommandHandler.color(s);
  }
  
  public void sendMessage(ViaCommandSender sender, String message, Object... args) {
    ViaCommandHandler.sendMessage(sender, message, args);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\command\ViaSubCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */