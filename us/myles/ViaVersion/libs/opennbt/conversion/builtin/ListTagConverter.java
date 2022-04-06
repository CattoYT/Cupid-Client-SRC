package us.myles.viaversion.libs.opennbt.conversion.builtin;

import java.util.ArrayList;
import java.util.List;
import us.myles.viaversion.libs.opennbt.conversion.ConverterRegistry;
import us.myles.viaversion.libs.opennbt.conversion.TagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.ListTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class ListTagConverter implements TagConverter<ListTag, List> {
  public List convert(ListTag tag) {
    List<Object> ret = new ArrayList();
    List<? extends Tag> tags = tag.getValue();
    for (Tag t : tags)
      ret.add(ConverterRegistry.convertToValue(t)); 
    return ret;
  }
  
  public ListTag convert(String name, List value) {
    List<Tag> tags = new ArrayList<>();
    for (Object o : value)
      tags.add(ConverterRegistry.convertToTag("", o)); 
    return new ListTag(name, tags);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\builtin\ListTagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */