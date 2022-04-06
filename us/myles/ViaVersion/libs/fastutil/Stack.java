package us.myles.viaversion.libs.fastutil;

public interface Stack<K> {
  void push(K paramK);
  
  K pop();
  
  boolean isEmpty();
  
  default K top() {
    return peek(0);
  }
  
  default K peek(int i) {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\Stack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */