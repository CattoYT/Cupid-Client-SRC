package us.myles.viaversion.libs.fastutil.ints;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface IntIterable extends Iterable<Integer> {
  default void forEach(IntConsumer action) {
    Objects.requireNonNull(action);
    for (IntIterator iterator = iterator(); iterator.hasNext();)
      action.accept(iterator.nextInt()); 
  }
  
  @Deprecated
  default void forEach(Consumer<? super Integer> action) {
    Objects.requireNonNull(action);
    forEach(action::accept);
  }
  
  IntIterator iterator();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntIterable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */