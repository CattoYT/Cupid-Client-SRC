package us.myles.ViaVersion.bungee.commands;

import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.myles.ViaVersion.api.command.ViaCommandSender;

public class BungeeCommandSender implements ViaCommandSender {
  private final CommandSender sender;
  
  public BungeeCommandSender(CommandSender sender) {
    this.sender = sender;
  }
  
  public boolean hasPermission(String permission) {
    return this.sender.hasPermission(permission);
  }
  
  public void sendMessage(String msg) {
    this.sender.sendMessage(msg);
  }
  
  public UUID getUUID() {
    if (this.sender instanceof ProxiedPlayer)
      return ((ProxiedPlayer)this.sender).getUniqueId(); 
    return UUID.fromString(getName());
  }
  
  public String getName() {
    return this.sender.getName();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\commands\BungeeCommandSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */