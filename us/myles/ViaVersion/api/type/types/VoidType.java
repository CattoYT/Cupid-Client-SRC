package us.myles.ViaVersion.api.type.types;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.TypeConverter;

public class VoidType extends Type<Void> implements TypeConverter<Void> {
  public VoidType() {
    super(Void.class);
  }
  
  public Void read(ByteBuf buffer) {
    return null;
  }
  
  public void write(ByteBuf buffer, Void object) {}
  
  public Void from(Object o) {
    return null;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\VoidType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */