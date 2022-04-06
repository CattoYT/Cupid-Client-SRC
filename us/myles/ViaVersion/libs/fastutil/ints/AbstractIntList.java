package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractIntList extends AbstractIntCollection implements IntList, IntStack {
  protected void ensureIndex(int index) {
    if (index < 0)
      throw new IndexOutOfBoundsException("Index (" + index + ") is negative"); 
    if (index > size())
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + size() + ")"); 
  }
  
  protected void ensureRestrictedIndex(int index) {
    if (index < 0)
      throw new IndexOutOfBoundsException("Index (" + index + ") is negative"); 
    if (index >= size())
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + 
          size() + ")"); 
  }
  
  public void add(int index, int k) {
    throw new UnsupportedOperationException();
  }
  
  public boolean add(int k) {
    add(size(), k);
    return true;
  }
  
  public int removeInt(int i) {
    throw new UnsupportedOperationException();
  }
  
  public int set(int index, int k) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(int index, Collection<? extends Integer> c) {
    ensureIndex(index);
    Iterator<? extends Integer> i = c.iterator();
    boolean retVal = i.hasNext();
    while (i.hasNext())
      add(index++, ((Integer)i.next()).intValue()); 
    return retVal;
  }
  
  public boolean addAll(Collection<? extends Integer> c) {
    return addAll(size(), c);
  }
  
  public IntListIterator iterator() {
    return listIterator();
  }
  
  public IntListIterator listIterator() {
    return listIterator(0);
  }
  
  public IntListIterator listIterator(final int index) {
    ensureIndex(index);
    return new IntListIterator() {
        int pos = index;
        
        int last = -1;
        
        public boolean hasNext() {
          return (this.pos < AbstractIntList.this.size());
        }
        
        public boolean hasPrevious() {
          return (this.pos > 0);
        }
        
        public int nextInt() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          return AbstractIntList.this.getInt(this.last = this.pos++);
        }
        
        public int previousInt() {
          if (!hasPrevious())
            throw new NoSuchElementException(); 
          return AbstractIntList.this.getInt(this.last = --this.pos);
        }
        
        public int nextIndex() {
          return this.pos;
        }
        
        public int previousIndex() {
          return this.pos - 1;
        }
        
        public void add(int k) {
          AbstractIntList.this.add(this.pos++, k);
          this.last = -1;
        }
        
        public void set(int k) {
          if (this.last == -1)
            throw new IllegalStateException(); 
          AbstractIntList.this.set(this.last, k);
        }
        
        public void remove() {
          if (this.last == -1)
            throw new IllegalStateException(); 
          AbstractIntList.this.removeInt(this.last);
          if (this.last < this.pos)
            this.pos--; 
          this.last = -1;
        }
      };
  }
  
  public boolean contains(int k) {
    return (indexOf(k) >= 0);
  }
  
  public int indexOf(int k) {
    IntListIterator i = listIterator();
    while (i.hasNext()) {
      int e = i.nextInt();
      if (k == e)
        return i.previousIndex(); 
    } 
    return -1;
  }
  
  public int lastIndexOf(int k) {
    IntListIterator i = listIterator(size());
    while (i.hasPrevious()) {
      int e = i.previousInt();
      if (k == e)
        return i.nextIndex(); 
    } 
    return -1;
  }
  
  public void size(int size) {
    int i = size();
    if (size > i) {
      while (i++ < size)
        add(0); 
    } else {
      while (i-- != size)
        removeInt(i); 
    } 
  }
  
  public IntList subList(int from, int to) {
    ensureIndex(from);
    ensureIndex(to);
    if (from > to)
      throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")"); 
    return new IntSubList(this, from, to);
  }
  
  public void removeElements(int from, int to) {
    ensureIndex(to);
    IntListIterator i = listIterator(from);
    int n = to - from;
    if (n < 0)
      throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")"); 
    while (n-- != 0) {
      i.nextInt();
      i.remove();
    } 
  }
  
  public void addElements(int index, int[] a, int offset, int length) {
    ensureIndex(index);
    if (offset < 0)
      throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative"); 
    if (offset + length > a.length)
      throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")"); 
    while (length-- != 0)
      add(index++, a[offset++]); 
  }
  
  public void addElements(int index, int[] a) {
    addElements(index, a, 0, a.length);
  }
  
  public void getElements(int from, int[] a, int offset, int length) {
    IntListIterator i = listIterator(from);
    if (offset < 0)
      throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative"); 
    if (offset + length > a.length)
      throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")"); 
    if (from + length > size())
      throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + 
          size() + ")"); 
    while (length-- != 0)
      a[offset++] = i.nextInt(); 
  }
  
  public void clear() {
    removeElements(0, size());
  }
  
  private boolean valEquals(Object a, Object b) {
    return (a == null) ? ((b == null)) : a.equals(b);
  }
  
  public int hashCode() {
    IntIterator i = iterator();
    int h = 1, s = size();
    while (s-- != 0) {
      int k = i.nextInt();
      h = 31 * h + k;
    } 
    return h;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof List))
      return false; 
    List<?> l = (List)o;
    int s = size();
    if (s != l.size())
      return false; 
    if (l instanceof IntList) {
      IntListIterator intListIterator1 = listIterator(), intListIterator2 = ((IntList)l).listIterator();
      while (s-- != 0) {
        if (intListIterator1.nextInt() != intListIterator2.nextInt())
          return false; 
      } 
      return true;
    } 
    ListIterator<?> i1 = listIterator(), i2 = l.listIterator();
    while (s-- != 0) {
      if (!valEquals(i1.next(), i2.next()))
        return false; 
    } 
    return true;
  }
  
  public int compareTo(List<? extends Integer> l) {
    if (l == this)
      return 0; 
    if (l instanceof IntList) {
      IntListIterator intListIterator1 = listIterator(), intListIterator2 = ((IntList)l).listIterator();
      while (intListIterator1.hasNext() && intListIterator2.hasNext()) {
        int e1 = intListIterator1.nextInt();
        int e2 = intListIterator2.nextInt();
        int r;
        if ((r = Integer.compare(e1, e2)) != 0)
          return r; 
      } 
      return intListIterator2.hasNext() ? -1 : (intListIterator1.hasNext() ? 1 : 0);
    } 
    ListIterator<? extends Integer> i1 = listIterator(), i2 = l.listIterator();
    while (i1.hasNext() && i2.hasNext()) {
      int r;
      if ((r = ((Comparable)i1.next()).compareTo(i2.next())) != 0)
        return r; 
    } 
    return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
  }
  
  public void push(int o) {
    add(o);
  }
  
  public int popInt() {
    if (isEmpty())
      throw new NoSuchElementException(); 
    return removeInt(size() - 1);
  }
  
  public int topInt() {
    if (isEmpty())
      throw new NoSuchElementException(); 
    return getInt(size() - 1);
  }
  
  public int peekInt(int i) {
    return getInt(size() - 1 - i);
  }
  
  public boolean rem(int k) {
    int index = indexOf(k);
    if (index == -1)
      return false; 
    removeInt(index);
    return true;
  }
  
  public boolean addAll(int index, IntCollection c) {
    ensureIndex(index);
    IntIterator i = c.iterator();
    boolean retVal = i.hasNext();
    while (i.hasNext())
      add(index++, i.nextInt()); 
    return retVal;
  }
  
  public boolean addAll(int index, IntList l) {
    return addAll(index, l);
  }
  
  public boolean addAll(IntCollection c) {
    return addAll(size(), c);
  }
  
  public boolean addAll(IntList l) {
    return addAll(size(), l);
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    IntIterator i = iterator();
    int n = size();
    boolean first = true;
    s.append("[");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      int k = i.nextInt();
      s.append(String.valueOf(k));
    } 
    s.append("]");
    return s.toString();
  }
  
  public static class IntSubList extends AbstractIntList implements Serializable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected final IntList l;
    
    protected final int from;
    
    protected int to;
    
    public IntSubList(IntList l, int from, int to) {
      this.l = l;
      this.from = from;
      this.to = to;
    }
    
    private boolean assertRange() {
      assert this.from <= this.l.size();
      assert this.to <= this.l.size();
      assert this.to >= this.from;
      return true;
    }
    
    public boolean add(int k) {
      this.l.add(this.to, k);
      this.to++;
      assert assertRange();
      return true;
    }
    
    public void add(int index, int k) {
      ensureIndex(index);
      this.l.add(this.from + index, k);
      this.to++;
      assert assertRange();
    }
    
    public boolean addAll(int index, Collection<? extends Integer> c) {
      ensureIndex(index);
      this.to += c.size();
      return this.l.addAll(this.from + index, c);
    }
    
    public int getInt(int index) {
      ensureRestrictedIndex(index);
      return this.l.getInt(this.from + index);
    }
    
    public int removeInt(int index) {
      ensureRestrictedIndex(index);
      this.to--;
      return this.l.removeInt(this.from + index);
    }
    
    public int set(int index, int k) {
      ensureRestrictedIndex(index);
      return this.l.set(this.from + index, k);
    }
    
    public int size() {
      return this.to - this.from;
    }
    
    public void getElements(int from, int[] a, int offset, int length) {
      ensureIndex(from);
      if (from + length > size())
        throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + 
            size() + ")"); 
      this.l.getElements(this.from + from, a, offset, length);
    }
    
    public void removeElements(int from, int to) {
      ensureIndex(from);
      ensureIndex(to);
      this.l.removeElements(this.from + from, this.from + to);
      this.to -= to - from;
      assert assertRange();
    }
    
    public void addElements(int index, int[] a, int offset, int length) {
      ensureIndex(index);
      this.l.addElements(this.from + index, a, offset, length);
      this.to += length;
      assert assertRange();
    }
    
    public IntListIterator listIterator(final int index) {
      ensureIndex(index);
      return new IntListIterator() {
          int pos = index;
          
          int last = -1;
          
          public boolean hasNext() {
            return (this.pos < AbstractIntList.IntSubList.this.size());
          }
          
          public boolean hasPrevious() {
            return (this.pos > 0);
          }
          
          public int nextInt() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            return AbstractIntList.IntSubList.this.l.getInt(AbstractIntList.IntSubList.this.from + (this.last = this.pos++));
          }
          
          public int previousInt() {
            if (!hasPrevious())
              throw new NoSuchElementException(); 
            return AbstractIntList.IntSubList.this.l.getInt(AbstractIntList.IntSubList.this.from + (this.last = --this.pos));
          }
          
          public int nextIndex() {
            return this.pos;
          }
          
          public int previousIndex() {
            return this.pos - 1;
          }
          
          public void add(int k) {
            if (this.last == -1)
              throw new IllegalStateException(); 
            AbstractIntList.IntSubList.this.add(this.pos++, k);
            this.last = -1;
            assert AbstractIntList.IntSubList.this.assertRange();
          }
          
          public void set(int k) {
            if (this.last == -1)
              throw new IllegalStateException(); 
            AbstractIntList.IntSubList.this.set(this.last, k);
          }
          
          public void remove() {
            if (this.last == -1)
              throw new IllegalStateException(); 
            AbstractIntList.IntSubList.this.removeInt(this.last);
            if (this.last < this.pos)
              this.pos--; 
            this.last = -1;
            assert AbstractIntList.IntSubList.this.assertRange();
          }
        };
    }
    
    public IntList subList(int from, int to) {
      ensureIndex(from);
      ensureIndex(to);
      if (from > to)
        throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")"); 
      return new IntSubList(this, from, to);
    }
    
    public boolean rem(int k) {
      int index = indexOf(k);
      if (index == -1)
        return false; 
      this.to--;
      this.l.removeInt(this.from + index);
      assert assertRange();
      return true;
    }
    
    public boolean addAll(int index, IntCollection c) {
      ensureIndex(index);
      return super.addAll(index, c);
    }
    
    public boolean addAll(int index, IntList l) {
      ensureIndex(index);
      return super.addAll(index, l);
    }
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\AbstractIntList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */