package us.myles.viaversion.libs.fastutil.ints;

import java.util.Iterator;
import java.util.Set;

public interface IntSet extends IntCollection, Set<Integer> {
  @Deprecated
  default boolean remove(Object o) {
    return super.remove(o);
  }
  
  @Deprecated
  default boolean add(Integer o) {
    return super.add(o);
  }
  
  @Deprecated
  default boolean contains(Object o) {
    return super.contains(o);
  }
  
  @Deprecated
  default boolean rem(int k) {
    return remove(k);
  }
  
  IntIterator iterator();
  
  boolean remove(int paramInt);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */