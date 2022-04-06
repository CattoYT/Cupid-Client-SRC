package us.myles.ViaVersion.protocols.protocol1_13to1_12_2;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage.TabCompleteTracker;

public class TabCompleteThread implements Runnable {
  public void run() {
    for (UserConnection info : Via.getManager().getConnections()) {
      if (info.getProtocolInfo() != null && 
        info.getProtocolInfo().getPipeline().contains(Protocol1_13To1_12_2.class) && 
        info.getChannel().isOpen())
        ((TabCompleteTracker)info.get(TabCompleteTracker.class)).sendPacketToServer(); 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\TabCompleteThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */