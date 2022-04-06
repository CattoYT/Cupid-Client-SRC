package us.myles.viaversion.libs.fastutil.objects;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface Object2ObjectMap<K, V> extends Object2ObjectFunction<K, V>, Map<K, V> {
  public static interface Entry<K, V> extends Map.Entry<K, V> {}
  
  public static interface FastEntrySet<K, V> extends ObjectSet<Entry<K, V>> {
    ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator();
    
    default void fastForEach(Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
      forEach(consumer);
    }
  }
  
  default void clear() {
    throw new UnsupportedOperationException();
  }
  
  default ObjectSet<Map.Entry<K, V>> entrySet() {
    return (ObjectSet)object2ObjectEntrySet();
  }
  
  default V put(K key, V value) {
    return super.put(key, value);
  }
  
  default V remove(Object key) {
    return super.remove(key);
  }
  
  int size();
  
  void defaultReturnValue(V paramV);
  
  V defaultReturnValue();
  
  ObjectSet<Entry<K, V>> object2ObjectEntrySet();
  
  ObjectSet<K> keySet();
  
  ObjectCollection<V> values();
  
  boolean containsKey(Object paramObject);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\Object2ObjectMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */