package us.myles.ViaVersion.bukkit.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.bukkit.util.NMSUtil;
import us.myles.ViaVersion.exception.CancelCodecException;
import us.myles.ViaVersion.exception.CancelEncoderException;
import us.myles.ViaVersion.exception.InformativeException;
import us.myles.ViaVersion.handlers.ChannelHandlerContextWrapper;
import us.myles.ViaVersion.handlers.ViaHandler;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.util.PipelineUtil;

public class BukkitEncodeHandler extends MessageToByteEncoder implements ViaHandler {
  private static Field versionField;
  
  private final UserConnection info;
  
  private final MessageToByteEncoder minecraftEncoder;
  
  static {
    try {
      versionField = NMSUtil.nms("PacketEncoder").getDeclaredField("version");
      versionField.setAccessible(true);
    } catch (Exception exception) {}
  }
  
  public BukkitEncodeHandler(UserConnection info, MessageToByteEncoder minecraftEncoder) {
    this.info = info;
    this.minecraftEncoder = minecraftEncoder;
  }
  
  protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf bytebuf) throws Exception {
    if (versionField != null)
      versionField.set(this.minecraftEncoder, versionField.get(this)); 
    if (!(o instanceof ByteBuf))
      try {
        PipelineUtil.callEncode(this.minecraftEncoder, (ChannelHandlerContext)new ChannelHandlerContextWrapper(ctx, this), o, bytebuf);
      } catch (InvocationTargetException e) {
        if (e.getCause() instanceof Exception)
          throw (Exception)e.getCause(); 
        if (e.getCause() instanceof Error)
          throw (Error)e.getCause(); 
      }  
    transform(bytebuf);
  }
  
  public void transform(ByteBuf bytebuf) throws Exception {
    if (!this.info.checkOutgoingPacket())
      throw CancelEncoderException.generate(null); 
    if (!this.info.shouldTransformPacket())
      return; 
    this.info.transformOutgoing(bytebuf, CancelEncoderException::generate);
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (PipelineUtil.containsCause(cause, CancelCodecException.class))
      return; 
    super.exceptionCaught(ctx, cause);
    if (!NMSUtil.isDebugPropertySet() && PipelineUtil.containsCause(cause, InformativeException.class) && (this.info
      .getProtocolInfo().getState() != State.HANDSHAKE || Via.getManager().isDebug()))
      cause.printStackTrace(); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\handlers\BukkitEncodeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */