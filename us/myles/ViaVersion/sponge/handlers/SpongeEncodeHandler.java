package us.myles.ViaVersion.sponge.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.lang.reflect.InvocationTargetException;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.exception.CancelEncoderException;
import us.myles.ViaVersion.handlers.ChannelHandlerContextWrapper;
import us.myles.ViaVersion.handlers.ViaHandler;
import us.myles.ViaVersion.util.PipelineUtil;

public class SpongeEncodeHandler extends MessageToByteEncoder<Object> implements ViaHandler {
  private final UserConnection info;
  
  private final MessageToByteEncoder<?> minecraftEncoder;
  
  public SpongeEncodeHandler(UserConnection info, MessageToByteEncoder<?> minecraftEncoder) {
    this.info = info;
    this.minecraftEncoder = minecraftEncoder;
  }
  
  protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf bytebuf) throws Exception {
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
    if (cause instanceof us.myles.ViaVersion.exception.CancelCodecException)
      return; 
    super.exceptionCaught(ctx, cause);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\handlers\SpongeEncodeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */