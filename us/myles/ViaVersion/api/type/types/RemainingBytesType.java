package us.myles.ViaVersion.api.type.types;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;

public class RemainingBytesType extends Type<byte[]> {
  public RemainingBytesType() {
    super(byte[].class);
  }
  
  public byte[] read(ByteBuf buffer) {
    byte[] array = new byte[buffer.readableBytes()];
    buffer.readBytes(array);
    return array;
  }
  
  public void write(ByteBuf buffer, byte[] object) {
    buffer.writeBytes(object);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\RemainingBytesType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */