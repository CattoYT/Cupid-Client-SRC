package us.myles.viaversion.libs.opennbt.conversion.builtin;

import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.ShortTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class ShortTagConverter implements TagConverter<ShortTag, Short> {
  public Short convert(ShortTag tag) {
    return tag.getValue();
  }
  
  public ShortTag convert(String name, Short value) {
    return new ShortTag(name, value.shortValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\ShortTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */