package us.myles.viaversion.libs.fastutil.objects;

import us.myles.viaversion.libs.fastutil.Function;

@FunctionalInterface
public interface Object2ObjectFunction<K, V> extends Function<K, V> {
  default V put(K key, V value) {
    throw new UnsupportedOperationException();
  }
  
  V get(Object paramObject);
  
  default V remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  default void defaultReturnValue(V rv) {
    throw new UnsupportedOperationException();
  }
  
  default V defaultReturnValue() {
    return null;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\Object2ObjectFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */