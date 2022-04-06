package us.myles.ViaVersion.sponge.commands;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import us.myles.ViaVersion.commands.ViaCommandHandler;

public class SpongeCommandHandler extends ViaCommandHandler implements CommandCallable {
  public CommandResult process(CommandSource source, String arguments) throws CommandException {
    String[] args = (arguments.length() > 0) ? arguments.split(" ") : new String[0];
    onCommand(new SpongeCommandSender(source), args);
    return CommandResult.success();
  }
  
  public List<String> getSuggestions(CommandSource commandSource, String s, @Nullable Location<World> location) throws CommandException {
    return getSuggestions(commandSource, s);
  }
  
  public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
    String[] args = arguments.split(" ", -1);
    return onTabComplete(new SpongeCommandSender(source), args);
  }
  
  public boolean testPermission(CommandSource source) {
    return source.hasPermission("viaversion.admin");
  }
  
  public Optional<Text> getShortDescription(CommandSource source) {
    return (Optional)Optional.of(Text.of("Shows ViaVersion Version and more."));
  }
  
  public Optional<Text> getHelp(CommandSource source) {
    return Optional.empty();
  }
  
  public Text getUsage(CommandSource source) {
    return (Text)Text.of("Usage /viaversion");
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\commands\SpongeCommandHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */