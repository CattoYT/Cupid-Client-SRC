package us.myles.ViaVersion.bungee.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.bungee.util.BungeePipelineUtil;
import us.myles.ViaVersion.exception.CancelEncoderException;

@Sharable
public class BungeeEncodeHandler extends MessageToMessageEncoder<ByteBuf> {
  private final UserConnection info;
  
  private boolean handledCompression;
  
  public BungeeEncodeHandler(UserConnection info) {
    this.info = info;
  }
  
  protected void encode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
    if (!this.info.checkOutgoingPacket())
      throw CancelEncoderException.generate(null); 
    if (!this.info.shouldTransformPacket()) {
      out.add(bytebuf.retain());
      return;
    } 
    ByteBuf transformedBuf = ctx.alloc().buffer().writeBytes(bytebuf);
    try {
      boolean needsCompress = handleCompressionOrder(ctx, transformedBuf);
      this.info.transformOutgoing(transformedBuf, CancelEncoderException::generate);
      if (needsCompress)
        recompress(ctx, transformedBuf); 
      out.add(transformedBuf.retain());
    } finally {
      transformedBuf.release();
    } 
  }
  
  private boolean handleCompressionOrder(ChannelHandlerContext ctx, ByteBuf buf) {
    boolean needsCompress = false;
    if (!this.handledCompression && 
      ctx.pipeline().names().indexOf("compress") > ctx.pipeline().names().indexOf("via-encoder")) {
      ByteBuf decompressed = BungeePipelineUtil.decompress(ctx, buf);
      try {
        buf.clear().writeBytes(decompressed);
      } finally {
        decompressed.release();
      } 
      ChannelHandler dec = ctx.pipeline().get("via-decoder");
      ChannelHandler enc = ctx.pipeline().get("via-encoder");
      ctx.pipeline().remove(dec);
      ctx.pipeline().remove(enc);
      ctx.pipeline().addAfter("decompress", "via-decoder", dec);
      ctx.pipeline().addAfter("compress", "via-encoder", enc);
      needsCompress = true;
      this.handledCompression = true;
    } 
    return needsCompress;
  }
  
  private void recompress(ChannelHandlerContext ctx, ByteBuf buf) {
    ByteBuf compressed = BungeePipelineUtil.compress(ctx, buf);
    try {
      buf.clear().writeBytes(compressed);
    } finally {
      compressed.release();
    } 
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (cause instanceof us.myles.ViaVersion.exception.CancelCodecException)
      return; 
    super.exceptionCaught(ctx, cause);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\handlers\BungeeEncodeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */