package us.myles.ViaVersion.bukkit.classgenerator;

import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.bukkit.handlers.BukkitDecodeHandler;
import us.myles.ViaVersion.bukkit.handlers.BukkitEncodeHandler;

public class BasicHandlerConstructor implements HandlerConstructor {
  public BukkitEncodeHandler newEncodeHandler(UserConnection info, MessageToByteEncoder minecraftEncoder) {
    return new BukkitEncodeHandler(info, minecraftEncoder);
  }
  
  public BukkitDecodeHandler newDecodeHandler(UserConnection info, ByteToMessageDecoder minecraftDecoder) {
    return new BukkitDecodeHandler(info, minecraftDecoder);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\classgenerator\BasicHandlerConstructor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */