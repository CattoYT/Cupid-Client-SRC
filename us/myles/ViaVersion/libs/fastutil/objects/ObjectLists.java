package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.RandomAccess;

public final class ObjectLists {
  public static <K> ObjectList<K> shuffle(ObjectList<K> l, Random random) {
    for (int i = l.size(); i-- != 0; ) {
      int p = random.nextInt(i + 1);
      K t = l.get(i);
      l.set(i, l.get(p));
      l.set(p, t);
    } 
    return l;
  }
  
  public static class EmptyList<K> extends ObjectCollections.EmptyCollection<K> implements ObjectList<K>, RandomAccess, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public K get(int i) {
      throw new IndexOutOfBoundsException();
    }
    
    public boolean remove(Object k) {
      throw new UnsupportedOperationException();
    }
    
    public K remove(int i) {
      throw new UnsupportedOperationException();
    }
    
    public void add(int index, K k) {
      throw new UnsupportedOperationException();
    }
    
    public K set(int index, K k) {
      throw new UnsupportedOperationException();
    }
    
    public int indexOf(Object k) {
      return -1;
    }
    
    public int lastIndexOf(Object k) {
      return -1;
    }
    
    public boolean addAll(int i, Collection<? extends K> c) {
      throw new UnsupportedOperationException();
    }
    
    public void sort(Comparator<? super K> comparator) {}
    
    public void unstableSort(Comparator<? super K> comparator) {}
    
    public ObjectListIterator<K> listIterator() {
      return ObjectIterators.EMPTY_ITERATOR;
    }
    
    public ObjectListIterator<K> iterator() {
      return ObjectIterators.EMPTY_ITERATOR;
    }
    
    public ObjectListIterator<K> listIterator(int i) {
      if (i == 0)
        return ObjectIterators.EMPTY_ITERATOR; 
      throw new IndexOutOfBoundsException(String.valueOf(i));
    }
    
    public ObjectList<K> subList(int from, int to) {
      if (from == 0 && to == 0)
        return this; 
      throw new IndexOutOfBoundsException();
    }
    
    public void getElements(int from, Object[] a, int offset, int length) {
      if (from == 0 && length == 0 && offset >= 0 && offset <= a.length)
        return; 
      throw new IndexOutOfBoundsException();
    }
    
    public void removeElements(int from, int to) {
      throw new UnsupportedOperationException();
    }
    
    public void addElements(int index, K[] a, int offset, int length) {
      throw new UnsupportedOperationException();
    }
    
    public void addElements(int index, K[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(K[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(int index, K[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(int index, K[] a, int offset, int length) {
      throw new UnsupportedOperationException();
    }
    
    public void size(int s) {
      throw new UnsupportedOperationException();
    }
    
    public int compareTo(List<? extends K> o) {
      if (o == this)
        return 0; 
      return o.isEmpty() ? 0 : -1;
    }
    
    public Object clone() {
      return ObjectLists.EMPTY_LIST;
    }
    
    public int hashCode() {
      return 1;
    }
    
    public boolean equals(Object o) {
      return (o instanceof List && ((List)o).isEmpty());
    }
    
    public String toString() {
      return "[]";
    }
    
    private Object readResolve() {
      return ObjectLists.EMPTY_LIST;
    }
  }
  
  public static final EmptyList EMPTY_LIST = new EmptyList();
  
  public static <K> ObjectList<K> emptyList() {
    return EMPTY_LIST;
  }
  
  public static class Singleton<K> extends AbstractObjectList<K> implements RandomAccess, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    private final K element;
    
    protected Singleton(K element) {
      this.element = element;
    }
    
    public K get(int i) {
      if (i == 0)
        return this.element; 
      throw new IndexOutOfBoundsException();
    }
    
    public boolean remove(Object k) {
      throw new UnsupportedOperationException();
    }
    
    public K remove(int i) {
      throw new UnsupportedOperationException();
    }
    
    public boolean contains(Object k) {
      return Objects.equals(k, this.element);
    }
    
    public Object[] toArray() {
      Object[] a = new Object[1];
      a[0] = this.element;
      return a;
    }
    
    public ObjectListIterator<K> listIterator() {
      return ObjectIterators.singleton(this.element);
    }
    
    public ObjectListIterator<K> iterator() {
      return listIterator();
    }
    
    public ObjectListIterator<K> listIterator(int i) {
      if (i > 1 || i < 0)
        throw new IndexOutOfBoundsException(); 
      ObjectListIterator<K> l = listIterator();
      if (i == 1)
        l.next(); 
      return l;
    }
    
    public ObjectList<K> subList(int from, int to) {
      ensureIndex(from);
      ensureIndex(to);
      if (from > to)
        throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")"); 
      if (from != 0 || to != 1)
        return ObjectLists.EMPTY_LIST; 
      return this;
    }
    
    public boolean addAll(int i, Collection<? extends K> c) {
      throw new UnsupportedOperationException();
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
    
    public void sort(Comparator<? super K> comparator) {}
    
    public void unstableSort(Comparator<? super K> comparator) {}
    
    public void removeElements(int from, int to) {
      throw new UnsupportedOperationException();
    }
    
    public void addElements(int index, K[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void addElements(int index, K[] a, int offset, int length) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(K[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(int index, K[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(int index, K[] a, int offset, int length) {
      throw new UnsupportedOperationException();
    }
    
    public int size() {
      return 1;
    }
    
    public void size(int size) {
      throw new UnsupportedOperationException();
    }
    
    public void clear() {
      throw new UnsupportedOperationException();
    }
    
    public Object clone() {
      return this;
    }
  }
  
  public static <K> ObjectList<K> singleton(K element) {
    return new Singleton<>(element);
  }
  
  public static <K> ObjectList<K> synchronize(ObjectList<K> l) {
    return (l instanceof RandomAccess) ? (ObjectList<K>)new SynchronizedRandomAccessList(l) : (ObjectList<K>)new SynchronizedList(l);
  }
  
  public static <K> ObjectList<K> synchronize(ObjectList<K> l, Object sync) {
    return (l instanceof RandomAccess) ? 
      (ObjectList<K>)new SynchronizedRandomAccessList(l, sync) : 
      (ObjectList<K>)new SynchronizedList(l, sync);
  }
  
  public static <K> ObjectList<K> unmodifiable(ObjectList<K> l) {
    return (l instanceof RandomAccess) ? (ObjectList<K>)new UnmodifiableRandomAccessList(l) : (ObjectList<K>)new UnmodifiableList(l);
  }
  
  public static class ObjectLists {}
  
  public static class ObjectLists {}
  
  public static class ObjectLists {}
  
  public static class ObjectLists {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectLists.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */