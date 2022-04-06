package us.myles.viaversion.libs.opennbt.conversion.builtin;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.FloatTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class FloatTagConverter implements TagConverter<FloatTag, Float> {
  public Float convert(FloatTag tag) {
    return tag.getValue();
  }
  
  public FloatTag convert(String name, Float value) {
    return new FloatTag(name, value.floatValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\FloatTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */