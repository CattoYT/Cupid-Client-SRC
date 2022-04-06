package us.myles.ViaVersion.api.type.types;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.TypeConverter;

public class LongType extends Type<Long> implements TypeConverter<Long> {
  public LongType() {
    super(Long.class);
  }
  
  public Long read(ByteBuf buffer) {
    return Long.valueOf(buffer.readLong());
  }
  
  public void write(ByteBuf buffer, Long object) {
    buffer.writeLong(object.longValue());
  }
  
  public Long from(Object o) {
    if (o instanceof Number)
      return Long.valueOf(((Number)o).longValue()); 
    if (o instanceof Boolean)
      return Long.valueOf(((Boolean)o).booleanValue() ? 1L : 0L); 
    return (Long)o;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\LongType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */