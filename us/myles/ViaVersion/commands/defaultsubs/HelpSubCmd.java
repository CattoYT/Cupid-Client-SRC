package us.myles.ViaVersion.commands.defaultsubs;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.command.ViaSubCommand;

public class HelpSubCmd extends ViaSubCommand {
  public String name() {
    return "help";
  }
  
  public String description() {
    return "You are looking at it right now!";
  }
  
  public boolean execute(ViaCommandSender sender, String[] args) {
    Via.getManager().getCommandHandler().showHelp(sender);
    return true;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\commands\defaultsubs\HelpSubCmd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */