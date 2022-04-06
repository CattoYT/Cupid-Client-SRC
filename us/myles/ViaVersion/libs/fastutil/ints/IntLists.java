package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.RandomAccess;

public final class IntLists {
  public static IntList shuffle(IntList l, Random random) {
    for (int i = l.size(); i-- != 0; ) {
      int p = random.nextInt(i + 1);
      int t = l.getInt(i);
      l.set(i, l.getInt(p));
      l.set(p, t);
    } 
    return l;
  }
  
  public static class EmptyList extends IntCollections.EmptyCollection implements IntList, RandomAccess, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public int getInt(int i) {
      throw new IndexOutOfBoundsException();
    }
    
    public boolean rem(int k) {
      throw new UnsupportedOperationException();
    }
    
    public int removeInt(int i) {
      throw new UnsupportedOperationException();
    }
    
    public void add(int index, int k) {
      throw new UnsupportedOperationException();
    }
    
    public int set(int index, int k) {
      throw new UnsupportedOperationException();
    }
    
    public int indexOf(int k) {
      return -1;
    }
    
    public int lastIndexOf(int k) {
      return -1;
    }
    
    public boolean addAll(int i, Collection<? extends Integer> c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(IntList c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(int i, IntCollection c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(int i, IntList c) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public void add(int index, Integer k) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public Integer get(int index) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public boolean add(Integer k) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public Integer set(int index, Integer k) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public Integer remove(int k) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public int indexOf(Object k) {
      return -1;
    }
    
    @Deprecated
    public int lastIndexOf(Object k) {
      return -1;
    }
    
    public void sort(IntComparator comparator) {}
    
    public void unstableSort(IntComparator comparator) {}
    
    @Deprecated
    public void sort(Comparator<? super Integer> comparator) {}
    
    @Deprecated
    public void unstableSort(Comparator<? super Integer> comparator) {}
    
    public IntListIterator listIterator() {
      return IntIterators.EMPTY_ITERATOR;
    }
    
    public IntListIterator iterator() {
      return IntIterators.EMPTY_ITERATOR;
    }
    
    public IntListIterator listIterator(int i) {
      if (i == 0)
        return IntIterators.EMPTY_ITERATOR; 
      throw new IndexOutOfBoundsException(String.valueOf(i));
    }
    
    public IntList subList(int from, int to) {
      if (from == 0 && to == 0)
        return this; 
      throw new IndexOutOfBoundsException();
    }
    
    public void getElements(int from, int[] a, int offset, int length) {
      if (from == 0 && length == 0 && offset >= 0 && offset <= a.length)
        return; 
      throw new IndexOutOfBoundsException();
    }
    
    public void removeElements(int from, int to) {
      throw new UnsupportedOperationException();
    }
    
    public void addElements(int index, int[] a, int offset, int length) {
      throw new UnsupportedOperationException();
    }
    
    public void addElements(int index, int[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(int[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(int index, int[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(int index, int[] a, int offset, int length) {
      throw new UnsupportedOperationException();
    }
    
    public void size(int s) {
      throw new UnsupportedOperationException();
    }
    
    public int compareTo(List<? extends Integer> o) {
      if (o == this)
        return 0; 
      return o.isEmpty() ? 0 : -1;
    }
    
    public Object clone() {
      return IntLists.EMPTY_LIST;
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
      return IntLists.EMPTY_LIST;
    }
  }
  
  public static final EmptyList EMPTY_LIST = new EmptyList();
  
  public static class IntLists {}
  
  public static class IntLists {}
  
  public static class IntLists {}
  
  public static class IntLists {}
  
  public static class Singleton extends AbstractIntList implements RandomAccess, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    private final int element;
    
    protected Singleton(int element) {
      this.element = element;
    }
    
    public int getInt(int i) {
      if (i == 0)
        return this.element; 
      throw new IndexOutOfBoundsException();
    }
    
    public boolean rem(int k) {
      throw new UnsupportedOperationException();
    }
    
    public int removeInt(int i) {
      throw new UnsupportedOperationException();
    }
    
    public boolean contains(int k) {
      return (k == this.element);
    }
    
    public int[] toIntArray() {
      int[] a = new int[1];
      a[0] = this.element;
      return a;
    }
    
    public IntListIterator listIterator() {
      return IntIterators.singleton(this.element);
    }
    
    public IntListIterator iterator() {
      return listIterator();
    }
    
    public IntListIterator listIterator(int i) {
      if (i > 1 || i < 0)
        throw new IndexOutOfBoundsException(); 
      IntListIterator l = listIterator();
      if (i == 1)
        l.nextInt(); 
      return l;
    }
    
    public IntList subList(int from, int to) {
      ensureIndex(from);
      ensureIndex(to);
      if (from > to)
        throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")"); 
      if (from != 0 || to != 1)
        return IntLists.EMPTY_LIST; 
      return this;
    }
    
    public boolean addAll(int i, Collection<? extends Integer> c) {
      throw new UnsupportedOperationException();
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
    
    public boolean addAll(IntList c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(int i, IntList c) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(int i, IntCollection c) {
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
    
    public void sort(IntComparator comparator) {}
    
    public void unstableSort(IntComparator comparator) {}
    
    @Deprecated
    public void sort(Comparator<? super Integer> comparator) {}
    
    @Deprecated
    public void unstableSort(Comparator<? super Integer> comparator) {}
    
    public void removeElements(int from, int to) {
      throw new UnsupportedOperationException();
    }
    
    public void addElements(int index, int[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void addElements(int index, int[] a, int offset, int length) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(int[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(int index, int[] a) {
      throw new UnsupportedOperationException();
    }
    
    public void setElements(int index, int[] a, int offset, int length) {
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
  
  public static IntList singleton(int element) {
    return new Singleton(element);
  }
  
  public static IntList singleton(Object element) {
    return new Singleton(((Integer)element).intValue());
  }
  
  public static IntList synchronize(IntList l) {
    return (l instanceof RandomAccess) ? (IntList)new SynchronizedRandomAccessList(l) : (IntList)new SynchronizedList(l);
  }
  
  public static IntList synchronize(IntList l, Object sync) {
    return (l instanceof RandomAccess) ? (IntList)new SynchronizedRandomAccessList(l, sync) : (IntList)new SynchronizedList(l, sync);
  }
  
  public static IntList unmodifiable(IntList l) {
    return (l instanceof RandomAccess) ? (IntList)new UnmodifiableRandomAccessList(l) : (IntList)new UnmodifiableList(l);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntLists.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */