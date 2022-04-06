package us.myles.ViaVersion.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import java.util.UUID;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.viaversion.libs.bungeecordchat.api.chat.TextComponent;
import us.myles.viaversion.libs.bungeecordchat.chat.ComponentSerializer;

public class VelocityCommandSender implements ViaCommandSender {
  private final CommandSource source;
  
  public VelocityCommandSender(CommandSource source) {
    this.source = source;
  }
  
  public boolean hasPermission(String permission) {
    return this.source.hasPermission(permission);
  }
  
  public void sendMessage(String msg) {
    this.source.sendMessage(GsonComponentSerializer.INSTANCE
        .deserialize(
          ComponentSerializer.toString(TextComponent.fromLegacyText(msg))));
  }
  
  public UUID getUUID() {
    if (this.source instanceof Player)
      return ((Player)this.source).getUniqueId(); 
    return UUID.fromString(getName());
  }
  
  public String getName() {
    if (this.source instanceof Player)
      return ((Player)this.source).getUsername(); 
    return "?";
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\command\VelocityCommandSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */