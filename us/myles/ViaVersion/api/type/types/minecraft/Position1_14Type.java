package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.type.Type;

public class Position1_14Type extends Type<Position> {
  public Position1_14Type() {
    super(Position.class);
  }
  
  public Position read(ByteBuf buffer) {
    long val = buffer.readLong();
    long x = val >> 38L;
    long y = val << 52L >> 52L;
    long z = val << 26L >> 38L;
    return new Position((int)x, (short)(int)y, (int)z);
  }
  
  public void write(ByteBuf buffer, Position object) {
    buffer.writeLong((object.getX() & 0x3FFFFFFL) << 38L | (object
        .getY() & 0xFFF) | (object
        .getZ() & 0x3FFFFFFL) << 12L);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\Position1_14Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */