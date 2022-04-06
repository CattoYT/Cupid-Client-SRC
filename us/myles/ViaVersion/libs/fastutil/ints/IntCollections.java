package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import us.myles.viaversion.libs.fastutil.objects.ObjectArrays;

public final class IntCollections {
  public static abstract class EmptyCollection extends AbstractIntCollection {
    public boolean contains(int k) {
      return false;
    }
    
    public Object[] toArray() {
      return ObjectArrays.EMPTY_ARRAY;
    }
    
    public IntBidirectionalIterator iterator() {
      return IntIterators.EMPTY_ITERATOR;
    }
    
    public int size() {
      return 0;
    }
    
    public void clear() {}
    
    public int hashCode() {
      return 0;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof Collection))
        return false; 
      return ((Collection)o).isEmpty();
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
  }
  
  public static IntCollection synchronize(IntCollection c) {
    return (IntCollection)new SynchronizedCollection(c);
  }
  
  public static IntCollection synchronize(IntCollection c, Object sync) {
    return (IntCollection)new SynchronizedCollection(c, sync);
  }
  
  public static IntCollection unmodifiable(IntCollection c) {
    return (IntCollection)new UnmodifiableCollection(c);
  }
  
  public static class IterableCollection extends AbstractIntCollection implements Serializable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected final IntIterable iterable;
    
    protected IterableCollection(IntIterable iterable) {
      if (iterable == null)
        throw new NullPointerException(); 
      this.iterable = iterable;
    }
    
    public int size() {
      int c = 0;
      IntIterator iterator = iterator();
      while (iterator.hasNext()) {
        iterator.nextInt();
        c++;
      } 
      return c;
    }
    
    public boolean isEmpty() {
      return !this.iterable.iterator().hasNext();
    }
    
    public IntIterator iterator() {
      return this.iterable.iterator();
    }
  }
  
  public static IntCollection asCollection(IntIterable iterable) {
    if (iterable instanceof IntCollection)
      return (IntCollection)iterable; 
    return new IterableCollection(iterable);
  }
  
  public static class IntCollections {}
  
  public static class IntCollections {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntCollections.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */