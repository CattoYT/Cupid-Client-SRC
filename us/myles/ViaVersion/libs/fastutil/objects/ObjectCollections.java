package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public final class ObjectCollections {
  public static abstract class EmptyCollection<K> extends AbstractObjectCollection<K> {
    public boolean contains(Object k) {
      return false;
    }
    
    public Object[] toArray() {
      return ObjectArrays.EMPTY_ARRAY;
    }
    
    public ObjectBidirectionalIterator<K> iterator() {
      return ObjectIterators.EMPTY_ITERATOR;
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
    
    public boolean addAll(Collection<? extends K> c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException();
    }
  }
  
  public static <K> ObjectCollection<K> synchronize(ObjectCollection<K> c) {
    return (ObjectCollection<K>)new SynchronizedCollection(c);
  }
  
  public static <K> ObjectCollection<K> synchronize(ObjectCollection<K> c, Object sync) {
    return (ObjectCollection<K>)new SynchronizedCollection(c, sync);
  }
  
  public static <K> ObjectCollection<K> unmodifiable(ObjectCollection<K> c) {
    return (ObjectCollection<K>)new UnmodifiableCollection(c);
  }
  
  public static class IterableCollection<K> extends AbstractObjectCollection<K> implements Serializable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected final ObjectIterable<K> iterable;
    
    protected IterableCollection(ObjectIterable<K> iterable) {
      if (iterable == null)
        throw new NullPointerException(); 
      this.iterable = iterable;
    }
    
    public int size() {
      int c = 0;
      ObjectIterator<K> iterator = iterator();
      while (iterator.hasNext()) {
        iterator.next();
        c++;
      } 
      return c;
    }
    
    public boolean isEmpty() {
      return !this.iterable.iterator().hasNext();
    }
    
    public ObjectIterator<K> iterator() {
      return this.iterable.iterator();
    }
  }
  
  public static <K> ObjectCollection<K> asCollection(ObjectIterable<K> iterable) {
    if (iterable instanceof ObjectCollection)
      return (ObjectCollection<K>)iterable; 
    return new IterableCollection<>(iterable);
  }
  
  public static class ObjectCollections {}
  
  public static class ObjectCollections {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectCollections.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */