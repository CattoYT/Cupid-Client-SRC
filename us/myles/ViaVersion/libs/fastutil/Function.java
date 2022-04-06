package us.myles.viaversion.libs.fastutil;

import java.util.function.Function;

@FunctionalInterface
public interface Function<K, V> extends Function<K, V> {
  default V apply(K key) {
    return get(key);
  }
  
  default V put(K key, V value) {
    throw new UnsupportedOperationException();
  }
  
  V get(Object paramObject);
  
  default boolean containsKey(Object key) {
    return true;
  }
  
  default V remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  default int size() {
    return -1;
  }
  
  default void clear() {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\Function.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */