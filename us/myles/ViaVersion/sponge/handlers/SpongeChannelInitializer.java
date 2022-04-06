package us.myles.ViaVersion.sponge.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import java.lang.reflect.Method;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolPipeline;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;

public class SpongeChannelInitializer extends ChannelInitializer<Channel> {
  private final ChannelInitializer<Channel> original;
  
  private Method method;
  
  public SpongeChannelInitializer(ChannelInitializer<Channel> oldInit) {
    this.original = oldInit;
    try {
      this.method = ChannelInitializer.class.getDeclaredMethod("initChannel", new Class[] { Channel.class });
      this.method.setAccessible(true);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } 
  }
  
  protected void initChannel(Channel channel) throws Exception {
    if (ProtocolRegistry.SERVER_PROTOCOL != -1 && channel instanceof io.netty.channel.socket.SocketChannel) {
      UserConnection info = new UserConnection(channel);
      new ProtocolPipeline(info);
      this.method.invoke(this.original, new Object[] { channel });
      MessageToByteEncoder encoder = new SpongeEncodeHandler(info, (MessageToByteEncoder)channel.pipeline().get("encoder"));
      ByteToMessageDecoder decoder = new SpongeDecodeHandler(info, (ByteToMessageDecoder)channel.pipeline().get("decoder"));
      SpongePacketHandler chunkHandler = new SpongePacketHandler(info);
      channel.pipeline().replace("encoder", "encoder", (ChannelHandler)encoder);
      channel.pipeline().replace("decoder", "decoder", (ChannelHandler)decoder);
      channel.pipeline().addAfter("packet_handler", "viaversion_packet_handler", (ChannelHandler)chunkHandler);
    } else {
      this.method.invoke(this.original, new Object[] { channel });
    } 
  }
  
  public ChannelInitializer<Channel> getOriginal() {
    return this.original;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\handlers\SpongeChannelInitializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */