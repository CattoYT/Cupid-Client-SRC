package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public final class ObjectSets {
  public static class EmptySet<K> extends ObjectCollections.EmptyCollection<K> implements ObjectSet<K>, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public boolean remove(Object ok) {
      throw new UnsupportedOperationException();
    }
    
    public Object clone() {
      return ObjectSets.EMPTY_SET;
    }
    
    public boolean equals(Object o) {
      return (o instanceof Set && ((Set)o).isEmpty());
    }
    
    private Object readResolve() {
      return ObjectSets.EMPTY_SET;
    }
  }
  
  public static final EmptySet EMPTY_SET = new EmptySet();
  
  public static <K> ObjectSet<K> emptySet() {
    return EMPTY_SET;
  }
  
  public static class Singleton<K> extends AbstractObjectSet<K> implements Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected final K element;
    
    protected Singleton(K element) {
      this.element = element;
    }
    
    public boolean contains(Object k) {
      return Objects.equals(k, this.element);
    }
    
    public boolean remove(Object k) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectListIterator<K> iterator() {
      return ObjectIterators.singleton(this.element);
    }
    
    public int size() {
      return 1;
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
    
    public Object clone() {
      return this;
    }
  }
  
  public static <K> ObjectSet<K> singleton(K element) {
    return new Singleton<>(element);
  }
  
  public static <K> ObjectSet<K> synchronize(ObjectSet<K> s) {
    return (ObjectSet<K>)new SynchronizedSet(s);
  }
  
  public static <K> ObjectSet<K> synchronize(ObjectSet<K> s, Object sync) {
    return (ObjectSet<K>)new SynchronizedSet(s, sync);
  }
  
  public static <K> ObjectSet<K> unmodifiable(ObjectSet<K> s) {
    return (ObjectSet<K>)new UnmodifiableSet(s);
  }
  
  public static class ObjectSets {}
  
  public static class ObjectSets {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectSets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */