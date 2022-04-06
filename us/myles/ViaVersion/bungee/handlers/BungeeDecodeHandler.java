package us.myles.ViaVersion.bungee.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.exception.CancelDecoderException;

@Sharable
public class BungeeDecodeHandler extends MessageToMessageDecoder<ByteBuf> {
  private final UserConnection info;
  
  public BungeeDecodeHandler(UserConnection info) {
    this.info = info;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
    if (!this.info.checkIncomingPacket())
      throw CancelDecoderException.generate(null); 
    if (!this.info.shouldTransformPacket()) {
      out.add(bytebuf.retain());
      return;
    } 
    ByteBuf transformedBuf = ctx.alloc().buffer().writeBytes(bytebuf);
    try {
      this.info.transformIncoming(transformedBuf, CancelDecoderException::generate);
      out.add(transformedBuf.retain());
    } finally {
      transformedBuf.release();
    } 
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (cause instanceof us.myles.ViaVersion.exception.CancelCodecException)
      return; 
    super.exceptionCaught(ctx, cause);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\handlers\BungeeDecodeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */