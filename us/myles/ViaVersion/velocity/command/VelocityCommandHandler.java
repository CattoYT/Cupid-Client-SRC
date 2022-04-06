package us.myles.ViaVersion.velocity.command;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import java.util.List;
import us.myles.ViaVersion.api.command.ViaSubCommand;
import us.myles.ViaVersion.commands.ViaCommandHandler;
import us.myles.ViaVersion.velocity.command.subs.ProbeSubCmd;

public class VelocityCommandHandler extends ViaCommandHandler implements Command {
  public VelocityCommandHandler() {
    try {
      registerSubCommand((ViaSubCommand)new ProbeSubCmd());
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void execute(CommandSource source, String[] args) {
    onCommand(new VelocityCommandSender(source), args);
  }
  
  public List<String> suggest(CommandSource source, String[] currentArgs) {
    return onTabComplete(new VelocityCommandSender(source), currentArgs);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\command\VelocityCommandHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */