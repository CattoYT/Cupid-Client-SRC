package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Objects;
import us.myles.viaversion.libs.fastutil.Function;

public final class Object2ObjectFunctions {
  public static class EmptyFunction<K, V> extends AbstractObject2ObjectFunction<K, V> implements Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public V get(Object k) {
      return null;
    }
    
    public boolean containsKey(Object k) {
      return false;
    }
    
    public V defaultReturnValue() {
      return null;
    }
    
    public void defaultReturnValue(V defRetValue) {
      throw new UnsupportedOperationException();
    }
    
    public int size() {
      return 0;
    }
    
    public void clear() {}
    
    public Object clone() {
      return Object2ObjectFunctions.EMPTY_FUNCTION;
    }
    
    public int hashCode() {
      return 0;
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Function))
        return false; 
      return (((Function)o).size() == 0);
    }
    
    public String toString() {
      return "{}";
    }
    
    private Object readResolve() {
      return Object2ObjectFunctions.EMPTY_FUNCTION;
    }
  }
  
  public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction<>();
  
  public static class Object2ObjectFunctions {}
  
  public static class Object2ObjectFunctions {}
  
  public static class Singleton<K, V> extends AbstractObject2ObjectFunction<K, V> implements Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected final K key;
    
    protected final V value;
    
    protected Singleton(K key, V value) {
      this.key = key;
      this.value = value;
    }
    
    public boolean containsKey(Object k) {
      return Objects.equals(this.key, k);
    }
    
    public V get(Object k) {
      return Objects.equals(this.key, k) ? this.value : this.defRetValue;
    }
    
    public int size() {
      return 1;
    }
    
    public Object clone() {
      return this;
    }
  }
  
  public static <K, V> Object2ObjectFunction<K, V> singleton(K key, V value) {
    return new Singleton<>(key, value);
  }
  
  public static <K, V> Object2ObjectFunction<K, V> synchronize(Object2ObjectFunction<K, V> f) {
    return (Object2ObjectFunction<K, V>)new SynchronizedFunction(f);
  }
  
  public static <K, V> Object2ObjectFunction<K, V> synchronize(Object2ObjectFunction<K, V> f, Object sync) {
    return (Object2ObjectFunction<K, V>)new SynchronizedFunction(f, sync);
  }
  
  public static <K, V> Object2ObjectFunction<K, V> unmodifiable(Object2ObjectFunction<K, V> f) {
    return (Object2ObjectFunction<K, V>)new UnmodifiableFunction(f);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\Object2ObjectFunctions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */