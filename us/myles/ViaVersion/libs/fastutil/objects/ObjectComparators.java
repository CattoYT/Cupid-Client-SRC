package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;

public final class ObjectComparators {
  protected static class NaturalImplicitComparator implements Comparator, Serializable {
    private static final long serialVersionUID = 1L;
    
    public final int compare(Object a, Object b) {
      return ((Comparable<Object>)a).compareTo(b);
    }
    
    public Comparator reversed() {
      return ObjectComparators.OPPOSITE_COMPARATOR;
    }
    
    private Object readResolve() {
      return ObjectComparators.NATURAL_COMPARATOR;
    }
  }
  
  public static final Comparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
  
  protected static class OppositeImplicitComparator implements Comparator, Serializable {
    private static final long serialVersionUID = 1L;
    
    public final int compare(Object a, Object b) {
      return ((Comparable<Object>)b).compareTo(a);
    }
    
    public Comparator reversed() {
      return ObjectComparators.NATURAL_COMPARATOR;
    }
    
    private Object readResolve() {
      return ObjectComparators.OPPOSITE_COMPARATOR;
    }
  }
  
  public static final Comparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();
  
  protected static class OppositeComparator<K> implements Comparator<K>, Serializable {
    private static final long serialVersionUID = 1L;
    
    final Comparator<K> comparator;
    
    protected OppositeComparator(Comparator<K> c) {
      this.comparator = c;
    }
    
    public final int compare(K a, K b) {
      return this.comparator.compare(b, a);
    }
    
    public final Comparator<K> reversed() {
      return this.comparator;
    }
  }
  
  public static <K> Comparator<K> oppositeComparator(Comparator<K> c) {
    if (c instanceof OppositeComparator)
      return ((OppositeComparator)c).comparator; 
    return new OppositeComparator<>(c);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectComparators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */