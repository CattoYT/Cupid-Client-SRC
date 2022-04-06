package us.myles.ViaVersion.bungee.providers;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.MovementTracker;

public class BungeeMovementTransmitter extends MovementTransmitterProvider {
  public Object getFlyingPacket() {
    return null;
  }
  
  public Object getGroundPacket() {
    return null;
  }
  
  public void sendPlayer(UserConnection userConnection) {
    if (userConnection.getProtocolInfo().getState() == State.PLAY) {
      PacketWrapper wrapper = new PacketWrapper(3, null, userConnection);
      wrapper.write(Type.BOOLEAN, Boolean.valueOf(((MovementTracker)userConnection.get(MovementTracker.class)).isGround()));
      try {
        wrapper.sendToServer(Protocol1_9To1_8.class);
      } catch (Exception e) {
        e.printStackTrace();
      } 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\providers\BungeeMovementTransmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */