package us.myles.viaversion.libs.opennbt.conversion.builtin;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class DoubleTagConverter implements TagConverter<DoubleTag, Double> {
  public Double convert(DoubleTag tag) {
    return tag.getValue();
  }
  
  public DoubleTag convert(String name, Double value) {
    return new DoubleTag(name, value.doubleValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\DoubleTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */