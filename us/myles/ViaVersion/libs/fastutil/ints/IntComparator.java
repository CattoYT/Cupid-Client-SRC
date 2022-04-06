package us.myles.viaversion.libs.fastutil.ints;

import java.lang.invoke.SerializedLambda;
import java.util.Comparator;

@FunctionalInterface
public interface IntComparator extends Comparator<Integer> {
  default IntComparator reversed() {
    return IntComparators.oppositeComparator(this);
  }
  
  @Deprecated
  default int compare(Integer ok1, Integer ok2) {
    return compare(ok1.intValue(), ok2.intValue());
  }
  
  default IntComparator thenComparing(IntComparator second) {
    return (k1, k2) -> {
        int comp = compare(k1, k2);
        return (comp == 0) ? second.compare(k1, k2) : comp;
      };
  }
  
  default Comparator<Integer> thenComparing(Comparator<? super Integer> second) {
    if (second instanceof IntComparator)
      return thenComparing((IntComparator)second); 
    return super.thenComparing(second);
  }
  
  int compare(int paramInt1, int paramInt2);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */