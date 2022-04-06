package us.myles.viaversion.libs.opennbt.conversion.builtin;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.LongTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class LongTagConverter implements TagConverter<LongTag, Long> {
  public Long convert(LongTag tag) {
    return tag.getValue();
  }
  
  public LongTag convert(String name, Long value) {
    return new LongTag(name, value.longValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\LongTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */