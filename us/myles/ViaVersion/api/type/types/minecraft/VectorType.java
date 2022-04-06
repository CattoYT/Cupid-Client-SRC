package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.Vector;
import us.myles.ViaVersion.api.type.Type;

public class VectorType extends Type<Vector> {
  public VectorType() {
    super(Vector.class);
  }
  
  public Vector read(ByteBuf buffer) throws Exception {
    int x = ((Integer)Type.INT.read(buffer)).intValue();
    int y = ((Integer)Type.INT.read(buffer)).intValue();
    int z = ((Integer)Type.INT.read(buffer)).intValue();
    return new Vector(x, y, z);
  }
  
  public void write(ByteBuf buffer, Vector object) throws Exception {
    Type.INT.write(buffer, Integer.valueOf(object.getBlockX()));
    Type.INT.write(buffer, Integer.valueOf(object.getBlockY()));
    Type.INT.write(buffer, Integer.valueOf(object.getBlockZ()));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\VectorType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */