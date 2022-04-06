package us.myles.ViaVersion.api.type;

import io.netty.buffer.ByteBuf;

public interface ByteBufWriter<T> {
  void write(ByteBuf paramByteBuf, T paramT) throws Exception;
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\ByteBufWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */