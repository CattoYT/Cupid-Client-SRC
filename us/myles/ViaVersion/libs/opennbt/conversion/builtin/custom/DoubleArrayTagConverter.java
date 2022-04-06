package us.myles.viaversion.libs.opennbt.conversion.builtin.custom;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;
import us.myles.viaversion.libs.opennbt.tag.builtin.custom.DoubleArrayTag;

public class DoubleArrayTagConverter implements TagConverter<DoubleArrayTag, double[]> {
  public double[] convert(DoubleArrayTag tag) {
    return tag.getValue();
  }
  
  public DoubleArrayTag convert(String name, double[] value) {
    return new DoubleArrayTag(name, value);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\custom\DoubleArrayTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */