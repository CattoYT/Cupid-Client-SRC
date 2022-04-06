package us.myles.viaversion.libs.fastutil.ints;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import us.myles.viaversion.libs.fastutil.Arrays;

public class IntArrayList extends AbstractIntList implements RandomAccess, Cloneable, Serializable {
  private static final long serialVersionUID = -7046029254386353130L;
  
  public static final int DEFAULT_INITIAL_CAPACITY = 10;
  
  protected transient int[] a;
  
  protected int size;
  
  protected IntArrayList(int[] a, boolean dummy) {
    this.a = a;
  }
  
  public IntArrayList(int capacity) {
    if (capacity < 0)
      throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative"); 
    if (capacity == 0) {
      this.a = IntArrays.EMPTY_ARRAY;
    } else {
      this.a = new int[capacity];
    } 
  }
  
  public IntArrayList() {
    this.a = IntArrays.DEFAULT_EMPTY_ARRAY;
  }
  
  public IntArrayList(Collection<? extends Integer> c) {
    this(c.size());
    this.size = IntIterators.unwrap(IntIterators.asIntIterator(c.iterator()), this.a);
  }
  
  public IntArrayList(IntCollection c) {
    this(c.size());
    this.size = IntIterators.unwrap(c.iterator(), this.a);
  }
  
  public IntArrayList(IntList l) {
    this(l.size());
    l.getElements(0, this.a, 0, this.size = l.size());
  }
  
  public IntArrayList(int[] a) {
    this(a, 0, a.length);
  }
  
  public IntArrayList(int[] a, int offset, int length) {
    this(length);
    System.arraycopy(a, offset, this.a, 0, length);
    this.size = length;
  }
  
  public IntArrayList(Iterator<? extends Integer> i) {
    this();
    while (i.hasNext())
      add(((Integer)i.next()).intValue()); 
  }
  
  public IntArrayList(IntIterator i) {
    this();
    while (i.hasNext())
      add(i.nextInt()); 
  }
  
  public int[] elements() {
    return this.a;
  }
  
  public static IntArrayList wrap(int[] a, int length) {
    if (length > a.length)
      throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")"); 
    IntArrayList l = new IntArrayList(a, false);
    l.size = length;
    return l;
  }
  
  public static IntArrayList wrap(int[] a) {
    return wrap(a, a.length);
  }
  
  public void ensureCapacity(int capacity) {
    if (capacity <= this.a.length || (this.a == IntArrays.DEFAULT_EMPTY_ARRAY && capacity <= 10))
      return; 
    this.a = IntArrays.ensureCapacity(this.a, capacity, this.size);
    assert this.size <= this.a.length;
  }
  
  private void grow(int capacity) {
    if (capacity <= this.a.length)
      return; 
    if (this.a != IntArrays.DEFAULT_EMPTY_ARRAY) {
      capacity = (int)Math.max(
          Math.min(this.a.length + (this.a.length >> 1), 2147483639L), capacity);
    } else if (capacity < 10) {
      capacity = 10;
    } 
    this.a = IntArrays.forceCapacity(this.a, capacity, this.size);
    assert this.size <= this.a.length;
  }
  
  public void add(int index, int k) {
    ensureIndex(index);
    grow(this.size + 1);
    if (index != this.size)
      System.arraycopy(this.a, index, this.a, index + 1, this.size - index); 
    this.a[index] = k;
    this.size++;
    assert this.size <= this.a.length;
  }
  
  public boolean add(int k) {
    grow(this.size + 1);
    this.a[this.size++] = k;
    assert this.size <= this.a.length;
    return true;
  }
  
  public int getInt(int index) {
    if (index >= this.size)
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
    return this.a[index];
  }
  
  public int indexOf(int k) {
    for (int i = 0; i < this.size; i++) {
      if (k == this.a[i])
        return i; 
    } 
    return -1;
  }
  
  public int lastIndexOf(int k) {
    for (int i = this.size; i-- != 0;) {
      if (k == this.a[i])
        return i; 
    } 
    return -1;
  }
  
  public int removeInt(int index) {
    if (index >= this.size)
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
    int old = this.a[index];
    this.size--;
    if (index != this.size)
      System.arraycopy(this.a, index + 1, this.a, index, this.size - index); 
    assert this.size <= this.a.length;
    return old;
  }
  
  public boolean rem(int k) {
    int index = indexOf(k);
    if (index == -1)
      return false; 
    removeInt(index);
    assert this.size <= this.a.length;
    return true;
  }
  
  public int set(int index, int k) {
    if (index >= this.size)
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")"); 
    int old = this.a[index];
    this.a[index] = k;
    return old;
  }
  
  public void clear() {
    this.size = 0;
    assert this.size <= this.a.length;
  }
  
  public int size() {
    return this.size;
  }
  
  public void size(int size) {
    if (size > this.a.length)
      this.a = IntArrays.forceCapacity(this.a, size, this.size); 
    if (size > this.size)
      Arrays.fill(this.a, this.size, size, 0); 
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
    int[] t = new int[Math.max(n, this.size)];
    System.arraycopy(this.a, 0, t, 0, this.size);
    this.a = t;
    assert this.size <= this.a.length;
  }
  
  public void getElements(int from, int[] a, int offset, int length) {
    IntArrays.ensureOffsetLength(a, offset, length);
    System.arraycopy(this.a, from, a, offset, length);
  }
  
  public void removeElements(int from, int to) {
    Arrays.ensureFromTo(this.size, from, to);
    System.arraycopy(this.a, to, this.a, from, this.size - to);
    this.size -= to - from;
  }
  
  public void addElements(int index, int[] a, int offset, int length) {
    ensureIndex(index);
    IntArrays.ensureOffsetLength(a, offset, length);
    grow(this.size + length);
    System.arraycopy(this.a, index, this.a, index + length, this.size - index);
    System.arraycopy(a, offset, this.a, index, length);
    this.size += length;
  }
  
  public void setElements(int index, int[] a, int offset, int length) {
    ensureIndex(index);
    IntArrays.ensureOffsetLength(a, offset, length);
    if (index + length > this.size)
      throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size + ")"); 
    System.arraycopy(a, offset, this.a, index, length);
  }
  
  public int[] toArray(int[] a) {
    if (a == null || a.length < this.size)
      a = new int[this.size]; 
    System.arraycopy(this.a, 0, a, 0, this.size);
    return a;
  }
  
  public boolean addAll(int index, IntCollection c) {
    ensureIndex(index);
    int n = c.size();
    if (n == 0)
      return false; 
    grow(this.size + n);
    if (index != this.size)
      System.arraycopy(this.a, index, this.a, index + n, this.size - index); 
    IntIterator i = c.iterator();
    this.size += n;
    while (n-- != 0)
      this.a[index++] = i.nextInt(); 
    assert this.size <= this.a.length;
    return true;
  }
  
  public boolean addAll(int index, IntList l) {
    ensureIndex(index);
    int n = l.size();
    if (n == 0)
      return false; 
    grow(this.size + n);
    if (index != this.size)
      System.arraycopy(this.a, index, this.a, index + n, this.size - index); 
    l.getElements(0, this.a, index, n);
    this.size += n;
    assert this.size <= this.a.length;
    return true;
  }
  
  public boolean removeAll(IntCollection c) {
    int[] a = this.a;
    int j = 0;
    for (int i = 0; i < this.size; i++) {
      if (!c.contains(a[i]))
        a[j++] = a[i]; 
    } 
    boolean modified = (this.size != j);
    this.size = j;
    return modified;
  }
  
  public boolean removeAll(Collection<?> c) {
    int[] a = this.a;
    int j = 0;
    for (int i = 0; i < this.size; i++) {
      if (!c.contains(Integer.valueOf(a[i])))
        a[j++] = a[i]; 
    } 
    boolean modified = (this.size != j);
    this.size = j;
    return modified;
  }
  
  public IntListIterator listIterator(final int index) {
    ensureIndex(index);
    return new IntListIterator() {
        int pos = index;
        
        int last = -1;
        
        public boolean hasNext() {
          return (this.pos < IntArrayList.this.size);
        }
        
        public boolean hasPrevious() {
          return (this.pos > 0);
        }
        
        public int nextInt() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          return IntArrayList.this.a[this.last = this.pos++];
        }
        
        public int previousInt() {
          if (!hasPrevious())
            throw new NoSuchElementException(); 
          return IntArrayList.this.a[this.last = --this.pos];
        }
        
        public int nextIndex() {
          return this.pos;
        }
        
        public int previousIndex() {
          return this.pos - 1;
        }
        
        public void add(int k) {
          IntArrayList.this.add(this.pos++, k);
          this.last = -1;
        }
        
        public void set(int k) {
          if (this.last == -1)
            throw new IllegalStateException(); 
          IntArrayList.this.set(this.last, k);
        }
        
        public void remove() {
          if (this.last == -1)
            throw new IllegalStateException(); 
          IntArrayList.this.removeInt(this.last);
          if (this.last < this.pos)
            this.pos--; 
          this.last = -1;
        }
      };
  }
  
  public void sort(IntComparator comp) {
    if (comp == null) {
      IntArrays.stableSort(this.a, 0, this.size);
    } else {
      IntArrays.stableSort(this.a, 0, this.size, comp);
    } 
  }
  
  public void unstableSort(IntComparator comp) {
    if (comp == null) {
      IntArrays.unstableSort(this.a, 0, this.size);
    } else {
      IntArrays.unstableSort(this.a, 0, this.size, comp);
    } 
  }
  
  public IntArrayList clone() {
    IntArrayList c = new IntArrayList(this.size);
    System.arraycopy(this.a, 0, c.a, 0, this.size);
    c.size = this.size;
    return c;
  }
  
  public boolean equals(IntArrayList l) {
    if (l == this)
      return true; 
    int s = size();
    if (s != l.size())
      return false; 
    int[] a1 = this.a;
    int[] a2 = l.a;
    while (s-- != 0) {
      if (a1[s] != a2[s])
        return false; 
    } 
    return true;
  }
  
  public int compareTo(IntArrayList l) {
    int s1 = size(), s2 = l.size();
    int[] a1 = this.a, a2 = l.a;
    int i;
    for (i = 0; i < s1 && i < s2; i++) {
      int e1 = a1[i];
      int e2 = a2[i];
      int r;
      if ((r = Integer.compare(e1, e2)) != 0)
        return r; 
    } 
    return (i < s2) ? -1 : ((i < s1) ? 1 : 0);
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    for (int i = 0; i < this.size; i++)
      s.writeInt(this.a[i]); 
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.a = new int[this.size];
    for (int i = 0; i < this.size; i++)
      this.a[i] = s.readInt(); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */