package us.myles.ViaVersion.bukkit.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import us.myles.ViaVersion.api.data.UserConnection;

public class BukkitPacketHandler extends MessageToMessageEncoder {
  private final UserConnection info;
  
  public BukkitPacketHandler(UserConnection info) {
    this.info = info;
  }
  
  protected void encode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
    if (!(o instanceof io.netty.buffer.ByteBuf)) {
      this.info.setLastPacket(o);
      if (this.info.isActive() && 
        this.info.getProtocolInfo().getPipeline().filter(o, list))
        return; 
    } 
    list.add(o);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\handlers\BukkitPacketHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */