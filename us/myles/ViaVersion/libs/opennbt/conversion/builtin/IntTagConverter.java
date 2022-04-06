package us.myles.viaversion.libs.opennbt.conversion.builtin;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.IntTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class IntTagConverter implements TagConverter<IntTag, Integer> {
  public Integer convert(IntTag tag) {
    return tag.getValue();
  }
  
  public IntTag convert(String name, Integer value) {
    return new IntTag(name, value.intValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\IntTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */