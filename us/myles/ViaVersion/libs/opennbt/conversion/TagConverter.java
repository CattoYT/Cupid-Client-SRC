package us.myles.viaversion.libs.opennbt.conversion;

public interface TagConverter<T extends us.myles.viaversion.libs.opennbt.tag.builtin.Tag, V> {
  V convert(T paramT);
  
  T convert(String paramString, V paramV);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\TagConverter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */