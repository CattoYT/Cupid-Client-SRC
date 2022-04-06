package us.myles.ViaVersion.commands.defaultsubs;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.command.ViaSubCommand;

public class DebugSubCmd extends ViaSubCommand {
  public String name() {
    return "debug";
  }
  
  public String description() {
    return "Toggle debug mode";
  }
  
  public boolean execute(ViaCommandSender sender, String[] args) {
    Via.getManager().setDebug(!Via.getManager().isDebug());
    sendMessage(sender, "&6Debug mode is now %s", new Object[] { Via.getManager().isDebug() ? "&aenabled" : "&cdisabled" });
    return true;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\commands\defaultsubs\DebugSubCmd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */