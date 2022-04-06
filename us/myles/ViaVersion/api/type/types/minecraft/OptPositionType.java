package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.type.Type;

public class OptPositionType extends Type<Position> {
  public OptPositionType() {
    super(Position.class);
  }
  
  public Position read(ByteBuf buffer) throws Exception {
    boolean present = buffer.readBoolean();
    if (!present)
      return null; 
    return (Position)Type.POSITION.read(buffer);
  }
  
  public void write(ByteBuf buffer, Position object) throws Exception {
    buffer.writeBoolean((object != null));
    if (object != null)
      Type.POSITION.write(buffer, object); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\OptPositionType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */