package us.myles.viaversion.libs.fastutil.ints;

import java.util.function.IntUnaryOperator;
import us.myles.viaversion.libs.fastutil.Function;

@FunctionalInterface
public interface Int2IntFunction extends Function<Integer, Integer>, IntUnaryOperator {
  default int applyAsInt(int operand) {
    return get(operand);
  }
  
  default int put(int key, int value) {
    throw new UnsupportedOperationException();
  }
  
  default int remove(int key) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  default Integer put(Integer key, Integer value) {
    int k = key.intValue();
    boolean containsKey = containsKey(k);
    int v = put(k, value.intValue());
    return containsKey ? Integer.valueOf(v) : null;
  }
  
  @Deprecated
  default Integer get(Object key) {
    if (key == null)
      return null; 
    int k = ((Integer)key).intValue();
    int v = get(k);
    return (v != defaultReturnValue() || containsKey(k)) ? Integer.valueOf(v) : null;
  }
  
  @Deprecated
  default Integer remove(Object key) {
    if (key == null)
      return null; 
    int k = ((Integer)key).intValue();
    return containsKey(k) ? Integer.valueOf(remove(k)) : null;
  }
  
  default boolean containsKey(int key) {
    return true;
  }
  
  @Deprecated
  default boolean containsKey(Object key) {
    return (key == null) ? false : containsKey(((Integer)key).intValue());
  }
  
  default void defaultReturnValue(int rv) {
    throw new UnsupportedOperationException();
  }
  
  default int defaultReturnValue() {
    return 0;
  }
  
  int get(int paramInt);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\Int2IntFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */