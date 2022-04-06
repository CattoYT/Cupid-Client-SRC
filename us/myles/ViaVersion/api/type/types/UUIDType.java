package us.myles.ViaVersion.api.type.types;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import us.myles.ViaVersion.api.type.Type;

public class UUIDType extends Type<UUID> {
  public UUIDType() {
    super(UUID.class);
  }
  
  public UUID read(ByteBuf buffer) {
    return new UUID(buffer.readLong(), buffer.readLong());
  }
  
  public void write(ByteBuf buffer, UUID object) {
    buffer.writeLong(object.getMostSignificantBits());
    buffer.writeLong(object.getLeastSignificantBits());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\UUIDType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */