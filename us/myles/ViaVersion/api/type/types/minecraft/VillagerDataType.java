package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.VillagerData;
import us.myles.ViaVersion.api.type.Type;

public class VillagerDataType extends Type<VillagerData> {
  public VillagerDataType() {
    super(VillagerData.class);
  }
  
  public VillagerData read(ByteBuf buffer) throws Exception {
    return new VillagerData(Type.VAR_INT.readPrimitive(buffer), Type.VAR_INT.readPrimitive(buffer), Type.VAR_INT.readPrimitive(buffer));
  }
  
  public void write(ByteBuf buffer, VillagerData object) throws Exception {
    Type.VAR_INT.writePrimitive(buffer, object.getType());
    Type.VAR_INT.writePrimitive(buffer, object.getProfession());
    Type.VAR_INT.writePrimitive(buffer, object.getLevel());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\VillagerDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */