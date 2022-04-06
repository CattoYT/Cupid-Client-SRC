package us.myles.viaversion.libs.fastutil.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import us.myles.viaversion.libs.fastutil.Arrays;

public class ObjectArrayList<K> extends AbstractObjectList<K> implements RandomAccess, Cloneable, Serializable {
  private static final long serialVersionUID = -7046029254386353131L;
  
  public static final int DEFAULT_INITIAL_CAPACITY = 10;
  
  protected final boolean wrapped;
  
  protected transient K[] a;
  
  protected int size;
  
  protected ObjectArrayList(K[] a, boolean dummy) {
    this.a = a;
    this.wrapped = true;
  }
  
  public ObjectArrayList(int capacity) {
    if (capacity < 0)
      throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative"); 
    if (capacity == 0) {
      this.a = (K[])ObjectArrays.EMPTY_ARRAY;
    } else {
      this.a = (K[])new Object[capacity];
    } 
    this.wrapped = false;
  }
  
  public ObjectArrayList() {
    this.a = (K[])ObjectArrays.DEFAULT_EMPTY_ARRAY;
    this.wrapped = false;
  }
  
  public ObjectArrayList(Collection<? extends K> c) {
    this(c.size());
    this.size = ObjectIterators.unwrap(c.iterator(), this.a);
  }
  
  public ObjectArrayList(ObjectCollection<? extends K> c) {
    this(c.size());
    this.size = ObjectIterators.unwrap(c.iterator(), this.a);
  }
  
  public ObjectArrayList(ObjectList<? extends K> l) {
    this(l.size());
    l.getElements(0, (Object[])this.a, 0, this.size = l.size());
  }
  
  public ObjectArrayList(K[] a) {
    this(a, 0, a.length);
  }
  
  public ObjectArrayList(K[] a, int offset, int length) {
    this(length);
    System.arraycopy(a, offset, this.a, 0, length);
    this.size = length;
  }
  
  public ObjectArrayList(Iterator<? extends K> i) {
    this();
    while (i.hasNext())
      add(i.next()); 
  }
  
  public ObjectArrayList(ObjectIterator<? extends K> i) {
    this();
    while (i.hasNext())
      add(i.next()); 
  }
  
  public K[] elements() {
    return this.a;
  }
  
  public static <K> ObjectArrayList<K> wrap(K[] a, int length) {
    if (length > a.length)
      throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")"); 
    ObjectArrayList<K> l = new ObjectArrayList<>(a, false);
    l.size = length;
    return l;
  }
  
  public static <K> ObjectArrayList<K> wrap(K[] a) {
    return wrap(a, a.length);
  }
  
  public void ensureCapacity(int capacity) {
    if (capacity <= this.a.length || (this.a == ObjectArrays.DEFAULT_EMPTY_ARRAY && capacity <= 10))
      return; 
    if (this.wrapped) {
      this.a = ObjectArrays.ensureCapacity(this.a, capacity, this.size);
    } else if (capacity > this.a.length) {
      Object[] t = new Object[capacity];
      System.arraycopy(this.a, 0, t, 0, this.size);
      this.a = (K[])t;
    } 
    assert this.size <= this.a.length;
  }
  
  private void grow(int capacity) {
    if (capacity <= this.a.length)
      return; 
    if (this.a != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
      capacity = (int)Math.max(
          Math.min(this.a.length + (this.a.length >> 1), 2147483639L), capacity);
    } else if (capacity < 10) {
      capacity = 10;
    } 
    if (this.wrapped) {
      this.a = ObjectArrays.forceCapacity(this.a, capacity, this.size);
    } else {
      Object[] t = new Object[capacity];
      System.arraycopy(this.a, 0, t, 0, this.size);
      this.a = (K[])t;
    } 
    assert this.size <= this.a.length;
  }
  
  public void add(int index, K k) {
    ensureIndex(index);
    grow(this.size + 1);
    if (index != this.size)
      System.arraycopy(this.a, index, this.a, index + 1, this.size - index); 
    this.a[index] = k;
    this.size++;
    assert this.size <= this.a.length;
  }
  
  public boolean add(K k) {
    grow(this.size + 1);
    this.a[this.size++] = k;
    assert this.size <= this.a.length;
    return true;
  }
  
  public K get(int index) {
    if (index >= this.size)
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
    return this.a[index];
  }
  
  public int indexOf(Object k) {
    for (int i = 0; i < this.size; i++) {
      if (Objects.equals(k, this.a[i]))
        return i; 
    } 
    return -1;
  }
  
  public int lastIndexOf(Object k) {
    for (int i = this.size; i-- != 0;) {
      if (Objects.equals(k, this.a[i]))
        return i; 
    } 
    return -1;
  }
  
  public K remove(int index) {
    if (index >= this.size)
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
    K old = this.a[index];
    this.size--;
    if (index != this.size)
      System.arraycopy(this.a, index + 1, this.a, index, this.size - index); 
    this.a[this.size] = null;
    assert this.size <= this.a.length;
    return old;
  }
  
  public boolean remove(Object k) {
    int index = indexOf(k);
    if (index == -1)
      return false; 
    remove(index);
    assert this.size <= this.a.length;
    return true;
  }
  
  public K set(int index, K k) {
    if (index >= this.size)
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
    K old = this.a[index];
    this.a[index] = k;
    return old;
  }
  
  public void clear() {
    Arrays.fill((Object[])this.a, 0, this.size, (Object)null);
    this.size = 0;
    assert this.size <= this.a.length;
  }
  
  public int size() {
    return this.size;
  }
  
  public void size(int size) {
    if (size > this.a.length)
      this.a = ObjectArrays.forceCapacity(this.a, size, this.size); 
    if (size > this.size) {
      Arrays.fill((Object[])this.a, this.size, size, (Object)null);
    } else {
      Arrays.fill((Object[])this.a, size, this.size, (Object)null);
    } 
    this.size = size;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  public void trim() {
    trim(0);
  }
  
  public void trim(int n) {
    if (n >= this.a.length || this.size == this.a.length)
      return; 
    K[] t = (K[])new Object[Math.max(n, this.size)];
    System.arraycopy(this.a, 0, t, 0, this.size);
    this.a = t;
    assert this.size <= this.a.length;
  }
  
  public void getElements(int from, Object[] a, int offset, int length) {
    ObjectArrays.ensureOffsetLength(a, offset, length);
    System.arraycopy(this.a, from, a, offset, length);
  }
  
  public void removeElements(int from, int to) {
    Arrays.ensureFromTo(this.size, from, to);
    System.arraycopy(this.a, to, this.a, from, this.size - to);
    this.size -= to - from;
    int i = to - from;
    while (i-- != 0)
      this.a[this.size + i] = null; 
  }
  
  public void addElements(int index, K[] a, int offset, int length) {
    ensureIndex(index);
    ObjectArrays.ensureOffsetLength(a, offset, length);
    grow(this.size + length);
    System.arraycopy(this.a, index, this.a, index + length, this.size - index);
    System.arraycopy(a, offset, this.a, index, length);
    this.size += length;
  }
  
  public void setElements(int index, K[] a, int offset, int length) {
    ensureIndex(index);
    ObjectArrays.ensureOffsetLength(a, offset, length);
    if (index + length > this.size)
      throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size + ")"); 
    System.arraycopy(a, offset, this.a, index, length);
  }
  
  public boolean removeAll(Collection<?> c) {
    K[] arrayOfK = this.a;
    int j = 0;
    for (int i = 0; i < this.size; i++) {
      if (!c.contains(arrayOfK[i]))
        arrayOfK[j++] = arrayOfK[i]; 
    } 
    Arrays.fill((Object[])arrayOfK, j, this.size, (Object)null);
    boolean modified = (this.size != j);
    this.size = j;
    return modified;
  }
  
  public ObjectListIterator<K> listIterator(final int index) {
    ensureIndex(index);
    return new ObjectListIterator<K>() {
        int pos = index;
        
        int last = -1;
        
        public boolean hasNext() {
          return (this.pos < ObjectArrayList.this.size);
        }
        
        public boolean hasPrevious() {
          return (this.pos > 0);
        }
        
        public K next() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          return ObjectArrayList.this.a[this.last = this.pos++];
        }
        
        public K previous() {
          if (!hasPrevious())
            throw new NoSuchElementException(); 
          return ObjectArrayList.this.a[this.last = --this.pos];
        }
        
        public int nextIndex() {
          return this.pos;
        }
        
        public int previousIndex() {
          return this.pos - 1;
        }
        
        public void add(K k) {
          ObjectArrayList.this.add(this.pos++, k);
          this.last = -1;
        }
        
        public void set(K k) {
          if (this.last == -1)
            throw new IllegalStateException(); 
          ObjectArrayList.this.set(this.last, k);
        }
        
        public void remove() {
          if (this.last == -1)
            throw new IllegalStateException(); 
          ObjectArrayList.this.remove(this.last);
          if (this.last < this.pos)
            this.pos--; 
          this.last = -1;
        }
      };
  }
  
  public void sort(Comparator<? super K> comp) {
    if (comp == null) {
      ObjectArrays.stableSort(this.a, 0, this.size);
    } else {
      ObjectArrays.stableSort(this.a, 0, this.size, (Comparator)comp);
    } 
  }
  
  public void unstableSort(Comparator<? super K> comp) {
    if (comp == null) {
      ObjectArrays.unstableSort(this.a, 0, this.size);
    } else {
      ObjectArrays.unstableSort(this.a, 0, this.size, (Comparator)comp);
    } 
  }
  
  public ObjectArrayList<K> clone() {
    ObjectArrayList<K> c = new ObjectArrayList(this.size);
    System.arraycopy(this.a, 0, c.a, 0, this.size);
    c.size = this.size;
    return c;
  }
  
  private boolean valEquals(K a, K b) {
    return (a == null) ? ((b == null)) : a.equals(b);
  }
  
  public boolean equals(ObjectArrayList<K> l) {
    if (l == this)
      return true; 
    int s = size();
    if (s != l.size())
      return false; 
    K[] a1 = this.a;
    K[] a2 = l.a;
    while (s-- != 0) {
      if (!valEquals(a1[s], a2[s]))
        return false; 
    } 
    return true;
  }
  
  public int compareTo(ObjectArrayList<? extends K> l) {
    int s1 = size(), s2 = l.size();
    K[] a1 = this.a, a2 = l.a;
    int i;
    for (i = 0; i < s1 && i < s2; i++) {
      K e1 = a1[i];
      K e2 = a2[i];
      int r;
      if ((r = ((Comparable<K>)e1).compareTo(e2)) != 0)
        return r; 
    } 
    return (i < s2) ? -1 : ((i < s1) ? 1 : 0);
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    for (int i = 0; i < this.size; i++)
      s.writeObject(this.a[i]); 
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.a = (K[])new Object[this.size];
    for (int i = 0; i < this.size; i++)
      this.a[i] = (K)s.readObject(); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */