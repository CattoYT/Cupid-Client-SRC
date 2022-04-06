package us.myles.ViaVersion.velocity.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.exception.CancelDecoderException;
import us.myles.ViaVersion.util.PipelineUtil;

@Sharable
public class VelocityDecodeHandler extends MessageToMessageDecoder<ByteBuf> {
  private final UserConnection info;
  
  private boolean handledCompression;
  
  private boolean skipDoubleTransform;
  
  public VelocityDecodeHandler(UserConnection info) {
    this.info = info;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
    if (this.skipDoubleTransform) {
      this.skipDoubleTransform = false;
      out.add(bytebuf.retain());
      return;
    } 
    if (!this.info.checkIncomingPacket())
      throw CancelDecoderException.generate(null); 
    if (!this.info.shouldTransformPacket()) {
      out.add(bytebuf.retain());
      return;
    } 
    ByteBuf transformedBuf = ctx.alloc().buffer().writeBytes(bytebuf);
    try {
      boolean needsCompress = handleCompressionOrder(ctx, transformedBuf);
      this.info.transformIncoming(transformedBuf, CancelDecoderException::generate);
      if (needsCompress) {
        recompress(ctx, transformedBuf);
        this.skipDoubleTransform = true;
      } 
      out.add(transformedBuf.retain());
    } finally {
      transformedBuf.release();
    } 
  }
  
  private boolean handleCompressionOrder(ChannelHandlerContext ctx, ByteBuf buf) throws InvocationTargetException {
    if (this.handledCompression)
      return false; 
    int decoderIndex = ctx.pipeline().names().indexOf("compression-decoder");
    if (decoderIndex == -1)
      return false; 
    this.handledCompression = true;
    if (decoderIndex > ctx.pipeline().names().indexOf("via-decoder")) {
      ByteBuf decompressed = PipelineUtil.callDecode((MessageToMessageDecoder)ctx.pipeline().get("compression-decoder"), ctx, buf).get(0);
      try {
        buf.clear().writeBytes(decompressed);
      } finally {
        decompressed.release();
      } 
      ChannelHandler encoder = ctx.pipeline().get("via-encoder");
      ChannelHandler decoder = ctx.pipeline().get("via-decoder");
      ctx.pipeline().remove(encoder);
      ctx.pipeline().remove(decoder);
      ctx.pipeline().addAfter("compression-encoder", "via-encoder", encoder);
      ctx.pipeline().addAfter("compression-decoder", "via-decoder", decoder);
      return true;
    } 
    return false;
  }
  
  private void recompress(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
    ByteBuf compressed = ctx.alloc().buffer();
    try {
      PipelineUtil.callEncode((MessageToByteEncoder)ctx.pipeline().get("compression-encoder"), ctx, buf, compressed);
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


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\handlers\VelocityDecodeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */