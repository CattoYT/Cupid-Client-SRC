package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;

public class OptionalVarIntType extends Type<Integer> {
  public OptionalVarIntType() {
    super(Integer.class);
  }
  
  public Integer read(ByteBuf buffer) throws Exception {
    int read = Type.VAR_INT.readPrimitive(buffer);
    if (read == 0)
      return null; 
    return Integer.valueOf(read - 1);
  }
  
  public void write(ByteBuf buffer, Integer object) throws Exception {
    if (object == null) {
      Type.VAR_INT.writePrimitive(buffer, 0);
    } else {
      Type.VAR_INT.writePrimitive(buffer, object.intValue() + 1);
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\OptionalVarIntType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */