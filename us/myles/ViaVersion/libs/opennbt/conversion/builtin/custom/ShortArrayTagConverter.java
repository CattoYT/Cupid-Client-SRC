package us.myles.viaversion.libs.opennbt.conversion.builtin.custom;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;
import us.myles.viaversion.libs.opennbt.tag.builtin.custom.ShortArrayTag;

public class ShortArrayTagConverter implements TagConverter<ShortArrayTag, short[]> {
  public short[] convert(ShortArrayTag tag) {
    return tag.getValue();
  }
  
  public ShortArrayTag convert(String name, short[] value) {
    return new ShortArrayTag(name, value);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\custom\ShortArrayTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */