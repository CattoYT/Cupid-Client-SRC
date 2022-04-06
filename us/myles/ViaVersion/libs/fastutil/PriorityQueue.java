package us.myles.viaversion.libs.fastutil;

import java.util.Comparator;

public interface PriorityQueue<K> {
  void enqueue(K paramK);
  
  K dequeue();
  
  default boolean isEmpty() {
    return (size() == 0);
  }
  
  int size();
  
  void clear();
  
  K first();
  
  default K last() {
    throw new UnsupportedOperationException();
  }
  
  default void changed() {
    throw new UnsupportedOperationException();
  }
  
  Comparator<? super K> comparator();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\PriorityQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */