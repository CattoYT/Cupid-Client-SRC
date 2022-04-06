package us.myles.ViaVersion.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface ViaHandler {
  void transform(ByteBuf paramByteBuf) throws Exception;
  
  void exceptionCaught(ChannelHandlerContext paramChannelHandlerContext, Throwable paramThrowable) throws Exception;
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\handlers\ViaHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */