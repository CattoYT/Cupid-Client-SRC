package us.myles.ViaVersion.api.type.types;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;

public class VarIntArrayType extends Type<int[]> {
  public VarIntArrayType() {
    super(int[].class);
  }
  
  public int[] read(ByteBuf buffer) throws Exception {
    int length = Type.VAR_INT.readPrimitive(buffer);
    Preconditions.checkArgument(buffer.isReadable(length));
    int[] array = new int[length];
    for (int i = 0; i < array.length; i++)
      array[i] = Type.VAR_INT.readPrimitive(buffer); 
    return array;
  }
  
  public void write(ByteBuf buffer, int[] object) throws Exception {
    Type.VAR_INT.writePrimitive(buffer, object.length);
    for (int i : object)
      Type.VAR_INT.writePrimitive(buffer, i); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\VarIntArrayType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */