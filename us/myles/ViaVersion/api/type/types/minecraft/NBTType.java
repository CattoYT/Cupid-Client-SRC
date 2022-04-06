package us.myles.ViaVersion.api.type.types.minecraft;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import us.myles.ViaVersion.api.type.Type;
import us.myles.viaversion.libs.opennbt.NBTIO;
import us.myles.viaversion.libs.opennbt.tag.TagRegistry;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class NBTType extends Type<CompoundTag> {
  static {
    TagRegistry.unregister(60);
    TagRegistry.unregister(61);
    TagRegistry.unregister(65);
  }
  
  public NBTType() {
    super(CompoundTag.class);
  }
  
  public CompoundTag read(ByteBuf buffer) throws Exception {
    Preconditions.checkArgument((buffer.readableBytes() <= 2097152), "Cannot read NBT (got %s bytes)", new Object[] { Integer.valueOf(buffer.readableBytes()) });
    int readerIndex = buffer.readerIndex();
    byte b = buffer.readByte();
    if (b == 0)
      return null; 
    buffer.readerIndex(readerIndex);
    return (CompoundTag)NBTIO.readTag((DataInput)new ByteBufInputStream(buffer));
  }
  
  public void write(ByteBuf buffer, CompoundTag object) throws Exception {
    if (object == null) {
      buffer.writeByte(0);
    } else {
      ByteBufOutputStream bytebufStream = new ByteBufOutputStream(buffer);
      NBTIO.writeTag((DataOutput)bytebufStream, (Tag)object);
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\NBTType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */