package us.myles.ViaVersion.commands.defaultsubs;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.command.ViaSubCommand;

public class ReloadSubCmd extends ViaSubCommand {
  public String name() {
    return "reload";
  }
  
  public String description() {
    return "Reload the config from the disk";
  }
  
  public boolean execute(ViaCommandSender sender, String[] args) {
    Via.getPlatform().getConfigurationProvider().reloadConfig();
    sendMessage(sender, "&6Configuration successfully reloaded! Some features may need a restart.", new Object[0]);
    return true;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\commands\defaultsubs\ReloadSubCmd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */