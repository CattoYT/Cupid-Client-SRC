package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import us.myles.ViaVersion.api.type.Type;

public class OptUUIDType extends Type<UUID> {
  public OptUUIDType() {
    super(UUID.class);
  }
  
  public UUID read(ByteBuf buffer) {
    boolean present = buffer.readBoolean();
    if (!present)
      return null; 
    return new UUID(buffer.readLong(), buffer.readLong());
  }
  
  public void write(ByteBuf buffer, UUID object) {
    if (object == null) {
      buffer.writeBoolean(false);
    } else {
      buffer.writeBoolean(true);
      buffer.writeLong(object.getMostSignificantBits());
      buffer.writeLong(object.getLeastSignificantBits());
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\OptUUIDType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */