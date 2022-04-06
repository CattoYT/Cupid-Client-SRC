package us.myles.viaversion.libs.opennbt.tag;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
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

public class TagRegistry {
  private static final Map<Integer, Class<? extends Tag>> idToTag = new HashMap<>();
  
  private static final Map<Class<? extends Tag>, Integer> tagToId = new HashMap<>();
  
  static {
    register(1, (Class)ByteTag.class);
    register(2, (Class)ShortTag.class);
    register(3, (Class)IntTag.class);
    register(4, (Class)LongTag.class);
    register(5, (Class)FloatTag.class);
    register(6, (Class)DoubleTag.class);
    register(7, (Class)ByteArrayTag.class);
    register(8, (Class)StringTag.class);
    register(9, (Class)ListTag.class);
    register(10, (Class)CompoundTag.class);
    register(11, (Class)IntArrayTag.class);
    register(12, (Class)LongArrayTag.class);
    register(60, (Class)DoubleArrayTag.class);
    register(61, (Class)FloatArrayTag.class);
    register(65, (Class)ShortArrayTag.class);
  }
  
  public static void register(int id, Class<? extends Tag> tag) throws TagRegisterException {
    if (idToTag.containsKey(Integer.valueOf(id)))
      throw new TagRegisterException("Tag ID \"" + id + "\" is already in use."); 
    if (tagToId.containsKey(tag))
      throw new TagRegisterException("Tag \"" + tag.getSimpleName() + "\" is already registered."); 
    idToTag.put(Integer.valueOf(id), tag);
    tagToId.put(tag, Integer.valueOf(id));
  }
  
  public static void unregister(int id) {
    tagToId.remove(getClassFor(id));
    idToTag.remove(Integer.valueOf(id));
  }
  
  public static Class<? extends Tag> getClassFor(int id) {
    if (!idToTag.containsKey(Integer.valueOf(id)))
      return null; 
    return idToTag.get(Integer.valueOf(id));
  }
  
  public static int getIdFor(Class<? extends Tag> clazz) {
    if (!tagToId.containsKey(clazz))
      return -1; 
    return ((Integer)tagToId.get(clazz)).intValue();
  }
  
  public static Tag createInstance(int id, String tagName) throws TagCreateException {
    Class<? extends Tag> clazz = idToTag.get(Integer.valueOf(id));
    if (clazz == null)
      throw new TagCreateException("Could not find tag with ID \"" + id + "\"."); 
    try {
      Constructor<? extends Tag> constructor = clazz.getDeclaredConstructor(new Class[] { String.class });
      constructor.setAccessible(true);
      return constructor.newInstance(new Object[] { tagName });
    } catch (Exception e) {
      throw new TagCreateException("Failed to create instance of tag \"" + clazz.getSimpleName() + "\".", e);
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\tag\TagRegistry.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */