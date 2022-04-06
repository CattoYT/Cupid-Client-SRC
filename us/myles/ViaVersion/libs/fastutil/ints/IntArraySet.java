package us.myles.viaversion.libs.fastutil.ints;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntArraySet extends AbstractIntSet implements Serializable, Cloneable {
  private static final long serialVersionUID = 1L;
  
  private transient int[] a;
  
  private int size;
  
  public IntArraySet(int[] a) {
    this.a = a;
    this.size = a.length;
  }
  
  public IntArraySet() {
    this.a = IntArrays.EMPTY_ARRAY;
  }
  
  public IntArraySet(int capacity) {
    this.a = new int[capacity];
  }
  
  public IntArraySet(IntCollection c) {
    this(c.size());
    addAll(c);
  }
  
  public IntArraySet(Collection<? extends Integer> c) {
    this(c.size());
    addAll(c);
  }
  
  public IntArraySet(int[] a, int size) {
    this.a = a;
    this.size = size;
    if (size > a.length)
      throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")"); 
  }
  
  private int findKey(int o) {
    for (int i = this.size; i-- != 0;) {
      if (this.a[i] == o)
        return i; 
    } 
    return -1;
  }
  
  public IntIterator iterator() {
    return new IntIterator() {
        int next = 0;
        
        public boolean hasNext() {
          return (this.next < IntArraySet.this.size);
        }
        
        public int nextInt() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          return IntArraySet.this.a[this.next++];
        }
        
        public void remove() {
          int tail = IntArraySet.this.size-- - this.next--;
          System.arraycopy(IntArraySet.this.a, this.next + 1, IntArraySet.this.a, this.next, tail);
        }
      };
  }
  
  public boolean contains(int k) {
    return (findKey(k) != -1);
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean remove(int k) {
    int pos = findKey(k);
    if (pos == -1)
      return false; 
    int tail = this.size - pos - 1;
    for (int i = 0; i < tail; i++)
      this.a[pos + i] = this.a[pos + i + 1]; 
    this.size--;
    return true;
  }
  
  public boolean add(int k) {
    int pos = findKey(k);
    if (pos != -1)
      return false; 
    if (this.size == this.a.length) {
      int[] b = new int[(this.size == 0) ? 2 : (this.size * 2)];
      for (int i = this.size; i-- != 0;)
        b[i] = this.a[i]; 
      this.a = b;
    } 
    this.a[this.size++] = k;
    return true;
  }
  
  public void clear() {
    this.size = 0;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  public IntArraySet clone() {
    IntArraySet c;
    try {
      c = (IntArraySet)super.clone();
    } catch (CloneNotSupportedException cantHappen) {
      throw new InternalError();
    } 
    c.a = (int[])this.a.clone();
    return c;
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


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntArraySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */