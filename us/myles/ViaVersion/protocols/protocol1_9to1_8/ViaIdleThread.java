package us.myles.ViaVersion.protocols.protocol1_9to1_8;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.MovementTracker;

public class ViaIdleThread implements Runnable {
  public void run() {
    for (UserConnection info : Via.getManager().getConnections()) {
      ProtocolInfo protocolInfo = info.getProtocolInfo();
      if (protocolInfo == null || !protocolInfo.getPipeline().contains(Protocol1_9To1_8.class))
        continue; 
      MovementTracker movementTracker = (MovementTracker)info.get(MovementTracker.class);
      if (movementTracker == null)
        continue; 
      long nextIdleUpdate = movementTracker.getNextIdlePacket();
      if (nextIdleUpdate <= System.currentTimeMillis() && info.getChannel().isOpen())
        ((MovementTransmitterProvider)Via.getManager().getProviders().get(MovementTransmitterProvider.class)).sendPlayer(info); 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\ViaIdleThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */