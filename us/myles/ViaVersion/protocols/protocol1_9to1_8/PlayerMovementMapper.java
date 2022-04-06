package us.myles.ViaVersion.protocols.protocol1_9to1_8;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.MovementTracker;

public class PlayerMovementMapper implements PacketHandler {
  public void handle(PacketWrapper wrapper) throws Exception {
    MovementTracker tracker = (MovementTracker)wrapper.user().get(MovementTracker.class);
    tracker.incrementIdlePacket();
    if (wrapper.is(Type.BOOLEAN, 0))
      tracker.setGround(((Boolean)wrapper.get(Type.BOOLEAN, 0)).booleanValue()); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\PlayerMovementMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */