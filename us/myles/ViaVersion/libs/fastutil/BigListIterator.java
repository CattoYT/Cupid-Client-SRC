package us.myles.viaversion.libs.fastutil;

public interface BigListIterator<K> extends BidirectionalIterator<K> {
  long nextIndex();
  
  long previousIndex();
  
  default void set(K e) {
    throw new UnsupportedOperationException();
  }
  
  default void add(K e) {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\BigListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */