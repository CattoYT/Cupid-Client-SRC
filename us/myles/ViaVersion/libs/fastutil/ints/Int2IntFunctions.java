package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import us.myles.viaversion.libs.fastutil.Function;

public final class Int2IntFunctions {
  public static class EmptyFunction extends AbstractInt2IntFunction implements Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public int get(int k) {
      return 0;
    }
    
    public boolean containsKey(int k) {
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
      return Int2IntFunctions.EMPTY_FUNCTION;
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
      return Int2IntFunctions.EMPTY_FUNCTION;
    }
  }
  
  public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();
  
  public static class Singleton extends AbstractInt2IntFunction implements Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected final int key;
    
    protected final int value;
    
    protected Singleton(int key, int value) {
      this.key = key;
      this.value = value;
    }
    
    public boolean containsKey(int k) {
      return (this.key == k);
    }
    
    public int get(int k) {
      return (this.key == k) ? this.value : this.defRetValue;
    }
    
    public int size() {
      return 1;
    }
    
    public Object clone() {
      return this;
    }
  }
  
  public static Int2IntFunction singleton(int key, int value) {
    return new Singleton(key, value);
  }
  
  public static Int2IntFunction singleton(Integer key, Integer value) {
    return new Singleton(key.intValue(), value.intValue());
  }
  
  public static Int2IntFunction synchronize(Int2IntFunction f) {
    return (Int2IntFunction)new SynchronizedFunction(f);
  }
  
  public static Int2IntFunction synchronize(Int2IntFunction f, Object sync) {
    return (Int2IntFunction)new SynchronizedFunction(f, sync);
  }
  
  public static Int2IntFunction unmodifiable(Int2IntFunction f) {
    return (Int2IntFunction)new UnmodifiableFunction(f);
  }
  
  public static class PrimitiveFunction implements Int2IntFunction {
    protected final Function<? super Integer, ? extends Integer> function;
    
    protected PrimitiveFunction(Function<? super Integer, ? extends Integer> function) {
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
    
    public int get(int key) {
      Integer v = this.function.apply(Integer.valueOf(key));
      if (v == null)
        return defaultReturnValue(); 
      return v.intValue();
    }
    
    @Deprecated
    public Integer get(Object key) {
      if (key == null)
        return null; 
      return this.function.apply((Integer)key);
    }
    
    @Deprecated
    public Integer put(Integer key, Integer value) {
      throw new UnsupportedOperationException();
    }
  }
  
  public static Int2IntFunction primitive(Function<? super Integer, ? extends Integer> f) {
    Objects.requireNonNull(f);
    if (f instanceof Int2IntFunction)
      return (Int2IntFunction)f; 
    if (f instanceof IntUnaryOperator) {
      Objects.requireNonNull((IntUnaryOperator)f);
      return (IntUnaryOperator)f::applyAsInt;
    } 
    return new PrimitiveFunction(f);
  }
  
  public static class Int2IntFunctions {}
  
  public static class Int2IntFunctions {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\Int2IntFunctions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */