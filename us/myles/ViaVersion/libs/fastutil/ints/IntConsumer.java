package us.myles.viaversion.libs.fastutil.ints;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface IntConsumer extends Consumer<Integer>, IntConsumer {
  @Deprecated
  default void accept(Integer t) {
    accept(t.intValue());
  }
  
  default IntConsumer andThen(IntConsumer after) {
    Objects.requireNonNull(after);
    return t -> {
        accept(t);
        after.accept(t);
      };
  }
  
  @Deprecated
  default Consumer<Integer> andThen(Consumer<? super Integer> after) {
    return super.andThen(after);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */