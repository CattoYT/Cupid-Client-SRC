package us.myles.viaversion.libs.opennbt.conversion.builtin;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class IntArrayTagConverter implements TagConverter<IntArrayTag, int[]> {
  public int[] convert(IntArrayTag tag) {
    return tag.getValue();
  }
  
  public IntArrayTag convert(String name, int[] value) {
    return new IntArrayTag(name, value);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\IntArrayTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */