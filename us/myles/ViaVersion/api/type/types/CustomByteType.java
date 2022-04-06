package us.myles.ViaVersion.api.type.types;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.PartialType;

public class CustomByteType extends PartialType<byte[], Integer> {
  public CustomByteType(Integer param) {
    super(param, byte[].class);
  }
  
  public byte[] read(ByteBuf byteBuf, Integer integer) throws Exception {
    if (byteBuf.readableBytes() < integer.intValue())
      throw new RuntimeException("Readable bytes does not match expected!"); 
    byte[] byteArray = new byte[integer.intValue()];
    byteBuf.readBytes(byteArray);
    return byteArray;
  }
  
  public void write(ByteBuf byteBuf, Integer integer, byte[] bytes) throws Exception {
    byteBuf.writeBytes(bytes);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\CustomByteType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */