package us.myles.ViaVersion.api.type.types;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.TypeConverter;

public class VarLongType extends Type<Long> implements TypeConverter<Long> {
  public VarLongType() {
    super("VarLong", Long.class);
  }
  
  public long readPrimitive(ByteBuf buffer) {
    long out = 0L;
    int bytes = 0;
    while (true) {
      byte in = buffer.readByte();
      out |= (in & Byte.MAX_VALUE) << bytes++ * 7;
      if (bytes > 10)
        throw new RuntimeException("VarLong too big"); 
      if ((in & 0x80) != 128)
        return out; 
    } 
  }
  
  public void writePrimitive(ByteBuf buffer, long object) {
    do {
      int part = (int)(object & 0x7FL);
      object >>>= 7L;
      if (object != 0L)
        part |= 0x80; 
      buffer.writeByte(part);
    } while (object != 0L);
  }
  
  @Deprecated
  public Long read(ByteBuf buffer) {
    return Long.valueOf(readPrimitive(buffer));
  }
  
  @Deprecated
  public void write(ByteBuf buffer, Long object) {
    writePrimitive(buffer, object.longValue());
  }
  
  public Long from(Object o) {
    if (o instanceof Number)
      return Long.valueOf(((Number)o).longValue()); 
    if (o instanceof Boolean)
      return Long.valueOf(((Boolean)o).booleanValue() ? 1L : 0L); 
    return (Long)o;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\VarLongType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */