package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import us.myles.viaversion.libs.fastutil.Function;

public final class Int2ObjectFunctions {
  public static class EmptyFunction<V> extends AbstractInt2ObjectFunction<V> implements Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public V get(int k) {
      return null;
    }
    
    public boolean containsKey(int k) {
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
      return Int2ObjectFunctions.EMPTY_FUNCTION;
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
      return Int2ObjectFunctions.EMPTY_FUNCTION;
    }
  }
  
  public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();
  
  public static class Singleton<V> extends AbstractInt2ObjectFunction<V> implements Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected final int key;
    
    protected final V value;
    
    protected Singleton(int key, V value) {
      this.key = key;
      this.value = value;
    }
    
    public boolean containsKey(int k) {
      return (this.key == k);
    }
    
    public V get(int k) {
      return (this.key == k) ? this.value : this.defRetValue;
    }
    
    public int size() {
      return 1;
    }
    
    public Object clone() {
      return this;
    }
  }
  
  public static <V> Int2ObjectFunction<V> singleton(int key, V value) {
    return new Singleton<>(key, value);
  }
  
  public static <V> Int2ObjectFunction<V> singleton(Integer key, V value) {
    return new Singleton<>(key.intValue(), value);
  }
  
  public static <V> Int2ObjectFunction<V> synchronize(Int2ObjectFunction<V> f) {
    return (Int2ObjectFunction<V>)new SynchronizedFunction(f);
  }
  
  public static <V> Int2ObjectFunction<V> synchronize(Int2ObjectFunction<V> f, Object sync) {
    return (Int2ObjectFunction<V>)new SynchronizedFunction(f, sync);
  }
  
  public static <V> Int2ObjectFunction<V> unmodifiable(Int2ObjectFunction<V> f) {
    return (Int2ObjectFunction<V>)new UnmodifiableFunction(f);
  }
  
  public static class PrimitiveFunction<V> implements Int2ObjectFunction<V> {
    protected final Function<? super Integer, ? extends V> function;
    
    protected PrimitiveFunction(Function<? super Integer, ? extends V> function) {
      this.function = function;
    }
    
    public boolean containsKey(int key) {
      return (this.function.apply(Integer.valueOf(key)) != null);
    }
    
    @Deprecated
    public boolean containsKey(Object key) {
      if (key == null)
        return false; 
      return (this.function.apply((Integer)key) != null);
    }
    
    public V get(int key) {
      V v = this.function.apply(Integer.valueOf(key));
      if (v == null)
        return null; 
      return v;
    }
    
    @Deprecated
    public V get(Object key) {
      if (key == null)
        return null; 
      return this.function.apply((Integer)key);
    }
    
    @Deprecated
    public V put(Integer key, V value) {
      throw new UnsupportedOperationException();
    }
  }
  
  public static <V> Int2ObjectFunction<V> primitive(Function<? super Integer, ? extends V> f) {
    Objects.requireNonNull(f);
    if (f instanceof Int2ObjectFunction)
      return (Int2ObjectFunction)f; 
    if (f instanceof IntFunction) {
      Objects.requireNonNull((IntFunction)f);
      return (IntFunction)f::apply;
    } 
    return new PrimitiveFunction<>(f);
  }
  
  public static class Int2ObjectFunctions {}
  
  public static class Int2ObjectFunctions {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\Int2ObjectFunctions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */