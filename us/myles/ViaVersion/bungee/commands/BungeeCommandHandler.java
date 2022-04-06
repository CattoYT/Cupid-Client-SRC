package us.myles.ViaVersion.bungee.commands;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.command.ViaSubCommand;
import us.myles.ViaVersion.bungee.commands.subs.ProbeSubCmd;
import us.myles.ViaVersion.commands.ViaCommandHandler;

public class BungeeCommandHandler extends ViaCommandHandler {
  public BungeeCommandHandler() {
    try {
      registerSubCommand((ViaSubCommand)new ProbeSubCmd());
    } catch (Exception e) {
      Via.getPlatform().getLogger().severe("Failed to register Bungee subcommands");
      e.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\commands\BungeeCommandHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */