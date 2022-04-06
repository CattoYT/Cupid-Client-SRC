package us.myles.viaversion.libs.opennbt.conversion.builtin.custom;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;
import us.myles.viaversion.libs.opennbt.tag.builtin.custom.FloatArrayTag;

public class FloatArrayTagConverter implements TagConverter<FloatArrayTag, float[]> {
  public float[] convert(FloatArrayTag tag) {
    return tag.getValue();
  }
  
  public FloatArrayTag convert(String name, float[] value) {
    return new FloatArrayTag(name, value);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\custom\FloatArrayTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */