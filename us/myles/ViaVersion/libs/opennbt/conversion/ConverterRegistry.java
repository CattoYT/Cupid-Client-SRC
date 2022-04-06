package us.myles.viaversion.libs.opennbt.conversion;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import us.myles.viaversion.libs.opennbt.conversion.builtin.ByteArrayTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.ByteTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.CompoundTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.DoubleTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.FloatTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.IntArrayTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.IntTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.ListTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.LongArrayTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.LongTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.ShortTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.StringTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.custom.DoubleArrayTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.custom.FloatArrayTagConverter;
import us.myles.viaversion.libs.opennbt.conversion.builtin.custom.ShortArrayTagConverter;
import us.myles.viaversion.libs.opennbt.tag.builtin.ByteArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ByteTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.FloatTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.IntTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ListTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.LongTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ShortTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;
import us.myles.viaversion.libs.opennbt.tag.builtin.custom.DoubleArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.custom.FloatArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.custom.ShortArrayTag;

public class ConverterRegistry {
  private static final Map<Class<? extends Tag>, TagConverter<? extends Tag, ?>> tagToConverter = new HashMap<>();
  
  private static final Map<Class<?>, TagConverter<? extends Tag, ?>> typeToConverter = new HashMap<>();
  
  static {
    register(ByteTag.class, Byte.class, (TagConverter<ByteTag, Byte>)new ByteTagConverter());
    register(ShortTag.class, Short.class, (TagConverter<ShortTag, Short>)new ShortTagConverter());
    register(IntTag.class, Integer.class, (TagConverter<IntTag, Integer>)new IntTagConverter());
    register(LongTag.class, Long.class, (TagConverter<LongTag, Long>)new LongTagConverter());
    register(FloatTag.class, Float.class, (TagConverter<FloatTag, Float>)new FloatTagConverter());
    register(DoubleTag.class, Double.class, (TagConverter<DoubleTag, Double>)new DoubleTagConverter());
    register(ByteArrayTag.class, (Class)byte[].class, (TagConverter<ByteArrayTag, byte>)new ByteArrayTagConverter());
    register(StringTag.class, String.class, (TagConverter<StringTag, String>)new StringTagConverter());
    register(ListTag.class, List.class, (TagConverter<ListTag, List>)new ListTagConverter());
    register(CompoundTag.class, Map.class, (TagConverter<CompoundTag, Map>)new CompoundTagConverter());
    register(IntArrayTag.class, (Class)int[].class, (TagConverter<IntArrayTag, int>)new IntArrayTagConverter());
    register(LongArrayTag.class, (Class)long[].class, (TagConverter<LongArrayTag, long>)new LongArrayTagConverter());
    register(DoubleArrayTag.class, (Class)double[].class, (TagConverter<DoubleArrayTag, double>)new DoubleArrayTagConverter());
    register(FloatArrayTag.class, (Class)float[].class, (TagConverter<FloatArrayTag, float>)new FloatArrayTagConverter());
    register(ShortArrayTag.class, (Class)short[].class, (TagConverter<ShortArrayTag, short>)new ShortArrayTagConverter());
  }
  
  public static <T extends Tag, V> void register(Class<T> tag, Class<V> type, TagConverter<T, V> converter) throws ConverterRegisterException {
    if (tagToConverter.containsKey(tag))
      throw new ConverterRegisterException("Type conversion to tag " + tag.getName() + " is already registered."); 
    if (typeToConverter.containsKey(type))
      throw new ConverterRegisterException("Tag conversion to type " + type.getName() + " is already registered."); 
    tagToConverter.put(tag, converter);
    typeToConverter.put(type, converter);
  }
  
  public static <T extends Tag, V> void unregister(Class<T> tag, Class<V> type) {
    tagToConverter.remove(tag);
    typeToConverter.remove(type);
  }
  
  public static <T extends Tag, V> V convertToValue(T tag) throws ConversionException {
    if (tag == null || tag.getValue() == null)
      return null; 
    if (!tagToConverter.containsKey(tag.getClass()))
      throw new ConversionException("Tag type " + tag.getClass().getName() + " has no converter."); 
    TagConverter<T, ?> converter = (TagConverter<T, ?>)tagToConverter.get(tag.getClass());
    return (V)converter.convert(tag);
  }
  
  public static <V, T extends Tag> T convertToTag(String name, V value) throws ConversionException {
    if (value == null)
      return null; 
    TagConverter<T, V> converter = (TagConverter<T, V>)typeToConverter.get(value.getClass());
    if (converter == null)
      for (Class<?> clazz : getAllClasses(value.getClass())) {
        if (typeToConverter.containsKey(clazz))
          try {
            converter = (TagConverter<T, V>)typeToConverter.get(clazz);
            break;
          } catch (ClassCastException classCastException) {} 
      }  
    if (converter == null)
      throw new ConversionException("Value type " + value.getClass().getName() + " has no converter."); 
    return converter.convert(name, value);
  }
  
  private static Set<Class<?>> getAllClasses(Class<?> clazz) {
    Set<Class<?>> ret = new LinkedHashSet<>();
    Class<?> c = clazz;
    while (c != null) {
      ret.add(c);
      ret.addAll(getAllSuperInterfaces(c));
      c = c.getSuperclass();
    } 
    if (ret.contains(Serializable.class)) {
      ret.remove(Serializable.class);
      ret.add(Serializable.class);
    } 
    return ret;
  }
  
  private static Set<Class<?>> getAllSuperInterfaces(Class<?> clazz) {
    Set<Class<?>> ret = new HashSet<>();
    for (Class<?> c : clazz.getInterfaces()) {
      ret.add(c);
      ret.addAll(getAllSuperInterfaces(c));
    } 
    return ret;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\ConverterRegistry.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */