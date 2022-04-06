package us.myles.ViaVersion.bukkit.commands;

import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.command.ViaCommandSender;

public class BukkitCommandSender implements ViaCommandSender {
  private final CommandSender sender;
  
  public BukkitCommandSender(CommandSender sender) {
    this.sender = sender;
  }
  
  public boolean hasPermission(String permission) {
    return this.sender.hasPermission(permission);
  }
  
  public void sendMessage(String msg) {
    this.sender.sendMessage(msg);
  }
  
  public UUID getUUID() {
    if (this.sender instanceof Player)
      return ((Player)this.sender).getUniqueId(); 
    return UUID.fromString(getName());
  }
  
  public String getName() {
    return this.sender.getName();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\commands\BukkitCommandSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */