package us.myles.ViaVersion.protocols.protocol1_9to1_8.providers;

import io.netty.channel.ChannelHandlerContext;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.platform.providers.Provider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.MovementTracker;
import us.myles.ViaVersion.util.PipelineUtil;

public abstract class MovementTransmitterProvider implements Provider {
  public abstract Object getFlyingPacket();
  
  public abstract Object getGroundPacket();
  
  public void sendPlayer(UserConnection userConnection) {
    ChannelHandlerContext context = PipelineUtil.getContextBefore("decoder", userConnection.getChannel().pipeline());
    if (context != null) {
      if (((MovementTracker)userConnection.get(MovementTracker.class)).isGround()) {
        context.fireChannelRead(getGroundPacket());
      } else {
        context.fireChannelRead(getFlyingPacket());
      } 
      ((MovementTracker)userConnection.get(MovementTracker.class)).incrementIdlePacket();
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\providers\MovementTransmitterProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */