package us.myles.viaversion.libs.opennbt.conversion.builtin;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class LongArrayTagConverter implements TagConverter<LongArrayTag, long[]> {
  public long[] convert(LongArrayTag tag) {
    return tag.getValue();
  }
  
  public LongArrayTag convert(String name, long[] value) {
    return new LongArrayTag(name, value);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\LongArrayTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */