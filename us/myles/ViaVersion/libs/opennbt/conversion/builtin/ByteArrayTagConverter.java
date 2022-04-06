package us.myles.viaversion.libs.opennbt.conversion.builtin;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.ByteArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class ByteArrayTagConverter implements TagConverter<ByteArrayTag, byte[]> {
  public byte[] convert(ByteArrayTag tag) {
    return tag.getValue();
  }
  
  public ByteArrayTag convert(String name, byte[] value) {
    return new ByteArrayTag(name, value);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\ByteArrayTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */