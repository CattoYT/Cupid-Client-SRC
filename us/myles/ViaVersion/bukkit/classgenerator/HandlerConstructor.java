package us.myles.ViaVersion.bukkit.classgenerator;

import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import us.myles.ViaVersion.api.data.UserConnection;

public interface HandlerConstructor {
  MessageToByteEncoder newEncodeHandler(UserConnection paramUserConnection, MessageToByteEncoder paramMessageToByteEncoder);
  
  ByteToMessageDecoder newDecodeHandler(UserConnection paramUserConnection, ByteToMessageDecoder paramByteToMessageDecoder);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\classgenerator\HandlerConstructor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */