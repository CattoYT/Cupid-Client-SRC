package us.myles.ViaVersion.api.type.types;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.TypeConverter;

public class BooleanType extends Type<Boolean> implements TypeConverter<Boolean> {
  public BooleanType() {
    super(Boolean.class);
  }
  
  public Boolean read(ByteBuf buffer) {
    return Boolean.valueOf(buffer.readBoolean());
  }
  
  public void write(ByteBuf buffer, Boolean object) {
    buffer.writeBoolean(object.booleanValue());
  }
  
  public Boolean from(Object o) {
    if (o instanceof Number)
      return Boolean.valueOf((((Number)o).intValue() == 1)); 
    return (Boolean)o;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\BooleanType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */