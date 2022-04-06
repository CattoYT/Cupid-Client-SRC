package us.myles.ViaVersion.bukkit.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import us.myles.ViaVersion.commands.ViaCommandHandler;

public class BukkitCommandHandler extends ViaCommandHandler implements CommandExecutor, TabExecutor {
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    return onCommand(new BukkitCommandSender(sender), args);
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    return onTabComplete(new BukkitCommandSender(sender), args);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\commands\BukkitCommandHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */