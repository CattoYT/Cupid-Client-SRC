package us.myles.ViaVersion.api.type.types.version;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_8;
import us.myles.ViaVersion.api.type.types.minecraft.MetaTypeTemplate;

public class Metadata1_8Type extends MetaTypeTemplate {
  public Metadata read(ByteBuf buffer) throws Exception {
    byte item = buffer.readByte();
    if (item == Byte.MAX_VALUE)
      return null; 
    int typeID = (item & 0xE0) >> 5;
    MetaType1_8 type = MetaType1_8.byId(typeID);
    int id = item & 0x1F;
    return new Metadata(id, (MetaType)type, type.getType().read(buffer));
  }
  
  public void write(ByteBuf buffer, Metadata meta) throws Exception {
    byte item = (byte)(meta.getMetaType().getTypeID() << 5 | meta.getId() & 0x1F);
    buffer.writeByte(item);
    meta.getMetaType().getType().write(buffer, meta.getValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\version\Metadata1_8Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */