package us.myles.viaversion.libs.fastutil.ints;

import us.myles.viaversion.libs.fastutil.Stack;

public interface IntStack extends Stack<Integer> {
  @Deprecated
  default void push(Integer o) {
    push(o.intValue());
  }
  
  @Deprecated
  default Integer pop() {
    return Integer.valueOf(popInt());
  }
  
  @Deprecated
  default Integer top() {
    return Integer.valueOf(topInt());
  }
  
  @Deprecated
  default Integer peek(int i) {
    return Integer.valueOf(peekInt(i));
  }
  
  void push(int paramInt);
  
  int popInt();
  
  int topInt();
  
  int peekInt(int paramInt);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */