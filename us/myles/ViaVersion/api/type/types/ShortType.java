package us.myles.ViaVersion.api.type.types;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.TypeConverter;

public class ShortType extends Type<Short> implements TypeConverter<Short> {
  public ShortType() {
    super(Short.class);
  }
  
  public short readPrimitive(ByteBuf buffer) {
    return buffer.readShort();
  }
  
  public void writePrimitive(ByteBuf buffer, short object) {
    buffer.writeShort(object);
  }
  
  @Deprecated
  public Short read(ByteBuf buffer) {
    return Short.valueOf(buffer.readShort());
  }
  
  @Deprecated
  public void write(ByteBuf buffer, Short object) {
    buffer.writeShort(object.shortValue());
  }
  
  public Short from(Object o) {
    if (o instanceof Number)
      return Short.valueOf(((Number)o).shortValue()); 
    if (o instanceof Boolean)
      return Short.valueOf(((Boolean)o).booleanValue() ? 1 : 0); 
    return (Short)o;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\ShortType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */