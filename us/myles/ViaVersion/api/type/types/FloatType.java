package us.myles.ViaVersion.api.type.types;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.TypeConverter;

public class FloatType extends Type<Float> implements TypeConverter<Float> {
  public FloatType() {
    super(Float.class);
  }
  
  public float readPrimitive(ByteBuf buffer) {
    return buffer.readFloat();
  }
  
  public void writePrimitive(ByteBuf buffer, float object) {
    buffer.writeFloat(object);
  }
  
  @Deprecated
  public Float read(ByteBuf buffer) {
    return Float.valueOf(buffer.readFloat());
  }
  
  @Deprecated
  public void write(ByteBuf buffer, Float object) {
    buffer.writeFloat(object.floatValue());
  }
  
  public Float from(Object o) {
    if (o instanceof Number)
      return Float.valueOf(((Number)o).floatValue()); 
    if (o instanceof Boolean)
      return Float.valueOf(((Boolean)o).booleanValue() ? 1.0F : 0.0F); 
    return (Float)o;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\FloatType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */