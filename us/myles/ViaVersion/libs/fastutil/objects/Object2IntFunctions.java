package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import us.myles.viaversion.libs.fastutil.Function;

public final class Object2IntFunctions {
  public static class EmptyFunction<K> extends AbstractObject2IntFunction<K> implements Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public int getInt(Object k) {
      return 0;
    }
    
    public boolean containsKey(Object k) {
      return false;
    }
    
    public int defaultReturnValue() {
      return 0;
    }
    
    public void defaultReturnValue(int defRetValue) {
      throw new UnsupportedOperationException();
    }
    
    public int size() {
      return 0;
    }
    
    public void clear() {}
    
    public Object clone() {
      return Object2IntFunctions.EMPTY_FUNCTION;
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
      return Object2IntFunctions.EMPTY_FUNCTION;
    }
  }
  
  public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();
  
  public static class Singleton<K> extends AbstractObject2IntFunction<K> implements Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected final K key;
    
    protected final int value;
    
    protected Singleton(K key, int value) {
      this.key = key;
      this.value = value;
    }
    
    public boolean containsKey(Object k) {
      return Objects.equals(this.key, k);
    }
    
    public int getInt(Object k) {
      return Objects.equals(this.key, k) ? this.value : this.defRetValue;
    }
    
    public int size() {
      return 1;
    }
    
    public Object clone() {
      return this;
    }
  }
  
  public static <K> Object2IntFunction<K> singleton(K key, int value) {
    return new Singleton<>(key, value);
  }
  
  public static <K> Object2IntFunction<K> singleton(K key, Integer value) {
    return new Singleton<>(key, value.intValue());
  }
  
  public static <K> Object2IntFunction<K> synchronize(Object2IntFunction<K> f) {
    return (Object2IntFunction<K>)new SynchronizedFunction(f);
  }
  
  public static <K> Object2IntFunction<K> synchronize(Object2IntFunction<K> f, Object sync) {
    return (Object2IntFunction<K>)new SynchronizedFunction(f, sync);
  }
  
  public static <K> Object2IntFunction<K> unmodifiable(Object2IntFunction<K> f) {
    return (Object2IntFunction<K>)new UnmodifiableFunction(f);
  }
  
  public static class PrimitiveFunction<K> implements Object2IntFunction<K> {
    protected final Function<? super K, ? extends Integer> function;
    
    protected PrimitiveFunction(Function<? super K, ? extends Integer> function) {
      this.function = function;
    }
    
    public boolean containsKey(Object key) {
      return (this.function.apply((K)key) != null);
    }
    
    public int getInt(Object key) {
      Integer v = this.function.apply((K)key);
      if (v == null)
        return defaultReturnValue(); 
      return v.intValue();
    }
    
    @Deprecated
    public Integer get(Object key) {
      return this.function.apply((K)key);
    }
    
    @Deprecated
    public Integer put(K key, Integer value) {
      throw new UnsupportedOperationException();
    }
  }
  
  public static <K> Object2IntFunction<K> primitive(Function<? super K, ? extends Integer> f) {
    Objects.requireNonNull(f);
    if (f instanceof Object2IntFunction)
      return (Object2IntFunction)f; 
    if (f instanceof ToIntFunction)
      return key -> ((ToIntFunction<Object>)f).applyAsInt(key); 
    return new PrimitiveFunction<>(f);
  }
  
  public static class Object2IntFunctions {}
  
  public static class Object2IntFunctions {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\Object2IntFunctions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */