package us.myles.ViaVersion.velocity.command.subs;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.command.ViaSubCommand;
import us.myles.ViaVersion.velocity.platform.VelocityViaConfig;
import us.myles.ViaVersion.velocity.service.ProtocolDetectorService;

public class ProbeSubCmd extends ViaSubCommand {
  public String name() {
    return "probe";
  }
  
  public String description() {
    return "Forces ViaVersion to scan server protocol versions " + (
      (((VelocityViaConfig)Via.getConfig()).getVelocityPingInterval() == -1) ? "" : "(Also happens at an interval)");
  }
  
  public boolean execute(ViaCommandSender sender, String[] args) {
    ProtocolDetectorService.getInstance().run();
    sendMessage(sender, "&6Started searching for protocol versions", new Object[0]);
    return true;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\command\subs\ProbeSubCmd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */