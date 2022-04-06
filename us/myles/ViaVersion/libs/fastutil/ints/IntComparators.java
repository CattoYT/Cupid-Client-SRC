package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Comparator;

public final class IntComparators {
  protected static class NaturalImplicitComparator implements IntComparator, Serializable {
    private static final long serialVersionUID = 1L;
    
    public final int compare(int a, int b) {
      return Integer.compare(a, b);
    }
    
    public IntComparator reversed() {
      return IntComparators.OPPOSITE_COMPARATOR;
    }
    
    private Object readResolve() {
      return IntComparators.NATURAL_COMPARATOR;
    }
  }
  
  public static final IntComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
  
  protected static class OppositeImplicitComparator implements IntComparator, Serializable {
    private static final long serialVersionUID = 1L;
    
    public final int compare(int a, int b) {
      return -Integer.compare(a, b);
    }
    
    public IntComparator reversed() {
      return IntComparators.NATURAL_COMPARATOR;
    }
    
    private Object readResolve() {
      return IntComparators.OPPOSITE_COMPARATOR;
    }
  }
  
  public static final IntComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();
  
  protected static class OppositeComparator implements IntComparator, Serializable {
    private static final long serialVersionUID = 1L;
    
    final IntComparator comparator;
    
    protected OppositeComparator(IntComparator c) {
      this.comparator = c;
    }
    
    public final int compare(int a, int b) {
      return this.comparator.compare(b, a);
    }
    
    public final IntComparator reversed() {
      return this.comparator;
    }
  }
  
  public static IntComparator oppositeComparator(IntComparator c) {
    if (c instanceof OppositeComparator)
      return ((OppositeComparator)c).comparator; 
    return new OppositeComparator(c);
  }
  
  public static IntComparator asIntComparator(final Comparator<? super Integer> c) {
    if (c == null || c instanceof IntComparator)
      return (IntComparator)c; 
    return new IntComparator() {
        public int compare(int x, int y) {
          return c.compare(Integer.valueOf(x), Integer.valueOf(y));
        }
        
        public int compare(Integer x, Integer y) {
          return c.compare(x, y);
        }
      };
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntComparators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */