package us.myles.viaversion.libs.fastutil;

import java.util.Comparator;

public interface IndirectPriorityQueue<K> {
  void enqueue(int paramInt);
  
  int dequeue();
  
  default boolean isEmpty() {
    return (size() == 0);
  }
  
  int size();
  
  void clear();
  
  int first();
  
  default int last() {
    throw new UnsupportedOperationException();
  }
  
  default void changed() {
    changed(first());
  }
  
  Comparator<? super K> comparator();
  
  default void changed(int index) {
    throw new UnsupportedOperationException();
  }
  
  default void allChanged() {
    throw new UnsupportedOperationException();
  }
  
  default boolean contains(int index) {
    throw new UnsupportedOperationException();
  }
  
  default boolean remove(int index) {
    throw new UnsupportedOperationException();
  }
  
  default int front(int[] a) {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\IndirectPriorityQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */