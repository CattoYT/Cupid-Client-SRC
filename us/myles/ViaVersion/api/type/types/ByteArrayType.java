package us.myles.ViaVersion.api.type.types;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;

public class ByteArrayType extends Type<byte[]> {
  public ByteArrayType() {
    super(byte[].class);
  }
  
  public void write(ByteBuf buffer, byte[] object) throws Exception {
    Type.VAR_INT.writePrimitive(buffer, object.length);
    buffer.writeBytes(object);
  }
  
  public byte[] read(ByteBuf buffer) throws Exception {
    int length = Type.VAR_INT.readPrimitive(buffer);
    Preconditions.checkArgument(buffer.isReadable(length), "Length is fewer than readable bytes");
    byte[] array = new byte[length];
    buffer.readBytes(array);
    return array;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\ByteArrayType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */