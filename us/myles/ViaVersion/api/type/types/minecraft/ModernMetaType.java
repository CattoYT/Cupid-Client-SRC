package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;

public abstract class ModernMetaType extends MetaTypeTemplate {
  public Metadata read(ByteBuf buffer) throws Exception {
    short index = buffer.readUnsignedByte();
    if (index == 255)
      return null; 
    MetaType type = getType(buffer.readByte());
    return new Metadata(index, type, type.getType().read(buffer));
  }
  
  public void write(ByteBuf buffer, Metadata object) throws Exception {
    if (object == null) {
      buffer.writeByte(255);
    } else {
      buffer.writeByte(object.getId());
      MetaType type = object.getMetaType();
      buffer.writeByte(type.getTypeID());
      type.getType().write(buffer, object.getValue());
    } 
  }
  
  protected abstract MetaType getType(int paramInt);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\ModernMetaType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */