package us.myles.viaversion.libs.fastutil.objects;

import java.util.ListIterator;

public interface ObjectListIterator<K> extends ObjectBidirectionalIterator<K>, ListIterator<K> {
  default void set(K k) {
    throw new UnsupportedOperationException();
  }
  
  default void add(K k) {
    throw new UnsupportedOperationException();
  }
  
  default void remove() {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */