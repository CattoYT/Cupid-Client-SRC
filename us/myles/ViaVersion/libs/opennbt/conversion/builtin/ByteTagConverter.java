package us.myles.viaversion.libs.opennbt.conversion.builtin;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.ByteTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class ByteTagConverter implements TagConverter<ByteTag, Byte> {
  public Byte convert(ByteTag tag) {
    return tag.getValue();
  }
  
  public ByteTag convert(String name, Byte value) {
    return new ByteTag(name, value.byteValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\ByteTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */