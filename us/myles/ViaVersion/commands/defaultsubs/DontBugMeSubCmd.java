package us.myles.ViaVersion.commands.defaultsubs;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.command.ViaSubCommand;
import us.myles.ViaVersion.api.configuration.ConfigurationProvider;

public class DontBugMeSubCmd extends ViaSubCommand {
  public String name() {
    return "dontbugme";
  }
  
  public String description() {
    return "Toggle checking for updates";
  }
  
  public boolean execute(ViaCommandSender sender, String[] args) {
    ConfigurationProvider provider = Via.getPlatform().getConfigurationProvider();
    boolean newValue = !Via.getConfig().isCheckForUpdates();
    Via.getConfig().setCheckForUpdates(newValue);
    provider.saveConfig();
    sendMessage(sender, "&6We will %snotify you about updates.", new Object[] { newValue ? "&a" : "&cnot " });
    return true;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\commands\defaultsubs\DontBugMeSubCmd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */