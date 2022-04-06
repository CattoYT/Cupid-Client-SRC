package us.myles.ViaVersion.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BungeeCommand extends Command implements TabExecutor {
  private final BungeeCommandHandler handler;
  
  public BungeeCommand(BungeeCommandHandler handler) {
    super("viaversion", "", new String[] { "viaver", "vvbungee" });
    this.handler = handler;
  }
  
  public void execute(CommandSender commandSender, String[] strings) {
    this.handler.onCommand(new BungeeCommandSender(commandSender), strings);
  }
  
  public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
    return this.handler.onTabComplete(new BungeeCommandSender(commandSender), strings);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\commands\BungeeCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */