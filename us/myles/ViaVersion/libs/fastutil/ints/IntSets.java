package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class IntSets {
  public static class EmptySet extends IntCollections.EmptyCollection implements IntSet, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public boolean remove(int ok) {
      throw new UnsupportedOperationException();
    }
    
    public Object clone() {
      return IntSets.EMPTY_SET;
    }
    
    public boolean equals(Object o) {
      return (o instanceof Set && ((Set)o).isEmpty());
    }
    
    @Deprecated
    public boolean rem(int k) {
      return super.rem(k);
    }
    
    private Object readResolve() {
      return IntSets.EMPTY_SET;
    }
  }
  
  public static final EmptySet EMPTY_SET = new EmptySet();
  
  public static class IntSets {}
  
  public static class IntSets {}
  
  public static class Singleton extends AbstractIntSet implements Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected final int element;
    
    protected Singleton(int element) {
      this.element = element;
    }
    
    public boolean contains(int k) {
      return (k == this.element);
    }
    
    public boolean remove(int k) {
      throw new UnsupportedOperationException();
    }
    
    public IntListIterator iterator() {
      return IntIterators.singleton(this.element);
    }
    
    public int size() {
      return 1;
    }
    
    public boolean addAll(Collection<? extends Integer> c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(IntCollection c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(IntCollection c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(IntCollection c) {
      throw new UnsupportedOperationException();
    }
    
    public Object clone() {
      return this;
    }
  }
  
  public static IntSet singleton(int element) {
    return new Singleton(element);
  }
  
  public static IntSet singleton(Integer element) {
    return new Singleton(element.intValue());
  }
  
  public static IntSet synchronize(IntSet s) {
    return (IntSet)new SynchronizedSet(s);
  }
  
  public static IntSet synchronize(IntSet s, Object sync) {
    return (IntSet)new SynchronizedSet(s, sync);
  }
  
  public static IntSet unmodifiable(IntSet s) {
    return (IntSet)new UnmodifiableSet(s);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntSets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */