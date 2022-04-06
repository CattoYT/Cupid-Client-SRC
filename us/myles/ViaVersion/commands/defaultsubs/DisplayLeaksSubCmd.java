package us.myles.ViaVersion.commands.defaultsubs;

import io.netty.util.ResourceLeakDetector;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.command.ViaSubCommand;

public class DisplayLeaksSubCmd extends ViaSubCommand {
  public String name() {
    return "displayleaks";
  }
  
  public String description() {
    return "Try to hunt memory leaks!";
  }
  
  public boolean execute(ViaCommandSender sender, String[] args) {
    if (ResourceLeakDetector.getLevel() != ResourceLeakDetector.Level.ADVANCED) {
      ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
    } else {
      ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
    } 
    sendMessage(sender, "&6Leak detector is now %s", new Object[] { (ResourceLeakDetector.getLevel() == ResourceLeakDetector.Level.ADVANCED) ? "&aenabled" : "&cdisabled" });
    return true;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\commands\defaultsubs\DisplayLeaksSubCmd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */