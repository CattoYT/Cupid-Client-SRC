package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.EulerAngle;
import us.myles.ViaVersion.api.type.Type;

public class EulerAngleType extends Type<EulerAngle> {
  public EulerAngleType() {
    super(EulerAngle.class);
  }
  
  public EulerAngle read(ByteBuf buffer) throws Exception {
    float x = Type.FLOAT.readPrimitive(buffer);
    float y = Type.FLOAT.readPrimitive(buffer);
    float z = Type.FLOAT.readPrimitive(buffer);
    return new EulerAngle(x, y, z);
  }
  
  public void write(ByteBuf buffer, EulerAngle object) throws Exception {
    Type.FLOAT.writePrimitive(buffer, object.getX());
    Type.FLOAT.writePrimitive(buffer, object.getY());
    Type.FLOAT.writePrimitive(buffer, object.getZ());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\EulerAngleType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */