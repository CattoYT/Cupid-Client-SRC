package us.myles.ViaVersion.sponge.commands;

import java.util.UUID;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Identifiable;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.viaversion.libs.bungeecordchat.api.chat.TextComponent;
import us.myles.viaversion.libs.bungeecordchat.chat.ComponentSerializer;

public class SpongeCommandSender implements ViaCommandSender {
  private final CommandSource source;
  
  public SpongeCommandSender(CommandSource source) {
    this.source = source;
  }
  
  public boolean hasPermission(String permission) {
    return this.source.hasPermission(permission);
  }
  
  public void sendMessage(String msg) {
    this.source.sendMessage(TextSerializers.JSON
        .deserialize(
          ComponentSerializer.toString(
            TextComponent.fromLegacyText(msg))));
  }
  
  public UUID getUUID() {
    if (this.source instanceof Identifiable)
      return ((Identifiable)this.source).getUniqueId(); 
    return UUID.fromString(getName());
  }
  
  public String getName() {
    return this.source.getName();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\commands\SpongeCommandSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */