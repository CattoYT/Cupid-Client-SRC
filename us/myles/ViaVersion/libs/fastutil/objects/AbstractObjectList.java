package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import us.myles.viaversion.libs.fastutil.Stack;

public abstract class AbstractObjectList<K> extends AbstractObjectCollection<K> implements ObjectList<K>, Stack<K> {
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
  
  public void add(int index, K k) {
    throw new UnsupportedOperationException();
  }
  
  public boolean add(K k) {
    add(size(), k);
    return true;
  }
  
  public K remove(int i) {
    throw new UnsupportedOperationException();
  }
  
  public K set(int index, K k) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(int index, Collection<? extends K> c) {
    ensureIndex(index);
    Iterator<? extends K> i = c.iterator();
    boolean retVal = i.hasNext();
    while (i.hasNext())
      add(index++, i.next()); 
    return retVal;
  }
  
  public boolean addAll(Collection<? extends K> c) {
    return addAll(size(), c);
  }
  
  public ObjectListIterator<K> iterator() {
    return listIterator();
  }
  
  public ObjectListIterator<K> listIterator() {
    return listIterator(0);
  }
  
  public ObjectListIterator<K> listIterator(final int index) {
    ensureIndex(index);
    return new ObjectListIterator<K>() {
        int pos = index;
        
        int last = -1;
        
        public boolean hasNext() {
          return (this.pos < AbstractObjectList.this.size());
        }
        
        public boolean hasPrevious() {
          return (this.pos > 0);
        }
        
        public K next() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          return AbstractObjectList.this.get(this.last = this.pos++);
        }
        
        public K previous() {
          if (!hasPrevious())
            throw new NoSuchElementException(); 
          return AbstractObjectList.this.get(this.last = --this.pos);
        }
        
        public int nextIndex() {
          return this.pos;
        }
        
        public int previousIndex() {
          return this.pos - 1;
        }
        
        public void add(K k) {
          AbstractObjectList.this.add(this.pos++, k);
          this.last = -1;
        }
        
        public void set(K k) {
          if (this.last == -1)
            throw new IllegalStateException(); 
          AbstractObjectList.this.set(this.last, k);
        }
        
        public void remove() {
          if (this.last == -1)
            throw new IllegalStateException(); 
          AbstractObjectList.this.remove(this.last);
          if (this.last < this.pos)
            this.pos--; 
          this.last = -1;
        }
      };
  }
  
  public boolean contains(Object k) {
    return (indexOf(k) >= 0);
  }
  
  public int indexOf(Object k) {
    ObjectListIterator<K> i = listIterator();
    while (i.hasNext()) {
      K e = i.next();
      if (Objects.equals(k, e))
        return i.previousIndex(); 
    } 
    return -1;
  }
  
  public int lastIndexOf(Object k) {
    ObjectListIterator<K> i = listIterator(size());
    while (i.hasPrevious()) {
      K e = i.previous();
      if (Objects.equals(k, e))
        return i.nextIndex(); 
    } 
    return -1;
  }
  
  public void size(int size) {
    int i = size();
    if (size > i) {
      while (i++ < size)
        add((K)null); 
    } else {
      while (i-- != size)
        remove(i); 
    } 
  }
  
  public ObjectList<K> subList(int from, int to) {
    ensureIndex(from);
    ensureIndex(to);
    if (from > to)
      throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")"); 
    return new ObjectSubList<>(this, from, to);
  }
  
  public void removeElements(int from, int to) {
    ensureIndex(to);
    ObjectListIterator<K> i = listIterator(from);
    int n = to - from;
    if (n < 0)
      throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")"); 
    while (n-- != 0) {
      i.next();
      i.remove();
    } 
  }
  
  public void addElements(int index, K[] a, int offset, int length) {
    ensureIndex(index);
    if (offset < 0)
      throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative"); 
    if (offset + length > a.length)
      throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")"); 
    while (length-- != 0)
      add(index++, a[offset++]); 
  }
  
  public void addElements(int index, K[] a) {
    addElements(index, a, 0, a.length);
  }
  
  public void getElements(int from, Object[] a, int offset, int length) {
    ObjectListIterator<K> i = listIterator(from);
    if (offset < 0)
      throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative"); 
    if (offset + length > a.length)
      throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")"); 
    if (from + length > size())
      throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + 
          size() + ")"); 
    while (length-- != 0)
      a[offset++] = i.next(); 
  }
  
  public void clear() {
    removeElements(0, size());
  }
  
  private boolean valEquals(Object a, Object b) {
    return (a == null) ? ((b == null)) : a.equals(b);
  }
  
  public int hashCode() {
    ObjectIterator<K> i = iterator();
    int h = 1, s = size();
    while (s-- != 0) {
      K k = i.next();
      h = 31 * h + ((k == null) ? 0 : k.hashCode());
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
    ListIterator<?> i1 = listIterator(), i2 = l.listIterator();
    while (s-- != 0) {
      if (!valEquals(i1.next(), i2.next()))
        return false; 
    } 
    return true;
  }
  
  public int compareTo(List<? extends K> l) {
    if (l == this)
      return 0; 
    if (l instanceof ObjectList) {
      ObjectListIterator<K> objectListIterator1 = listIterator(), objectListIterator2 = ((ObjectList)l).listIterator();
      while (objectListIterator1.hasNext() && objectListIterator2.hasNext()) {
        K e1 = objectListIterator1.next();
        K e2 = objectListIterator2.next();
        int r;
        if ((r = ((Comparable<K>)e1).compareTo(e2)) != 0)
          return r; 
      } 
      return objectListIterator2.hasNext() ? -1 : (objectListIterator1.hasNext() ? 1 : 0);
    } 
    ListIterator<? extends K> i1 = listIterator(), i2 = l.listIterator();
    while (i1.hasNext() && i2.hasNext()) {
      int r;
      if ((r = ((Comparable)i1.next()).compareTo(i2.next())) != 0)
        return r; 
    } 
    return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
  }
  
  public void push(K o) {
    add(o);
  }
  
  public K pop() {
    if (isEmpty())
      throw new NoSuchElementException(); 
    return remove(size() - 1);
  }
  
  public K top() {
    if (isEmpty())
      throw new NoSuchElementException(); 
    return get(size() - 1);
  }
  
  public K peek(int i) {
    return get(size() - 1 - i);
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    ObjectIterator<K> i = iterator();
    int n = size();
    boolean first = true;
    s.append("[");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      K k = i.next();
      if (this == k) {
        s.append("(this list)");
        continue;
      } 
      s.append(String.valueOf(k));
    } 
    s.append("]");
    return s.toString();
  }
  
  public static class ObjectSubList<K> extends AbstractObjectList<K> implements Serializable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected final ObjectList<K> l;
    
    protected final int from;
    
    protected int to;
    
    public ObjectSubList(ObjectList<K> l, int from, int to) {
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
    
    public boolean add(K k) {
      this.l.add(this.to, k);
      this.to++;
      assert assertRange();
      return true;
    }
    
    public void add(int index, K k) {
      ensureIndex(index);
      this.l.add(this.from + index, k);
      this.to++;
      assert assertRange();
    }
    
    public boolean addAll(int index, Collection<? extends K> c) {
      ensureIndex(index);
      this.to += c.size();
      return this.l.addAll(this.from + index, c);
    }
    
    public K get(int index) {
      ensureRestrictedIndex(index);
      return this.l.get(this.from + index);
    }
    
    public K remove(int index) {
      ensureRestrictedIndex(index);
      this.to--;
      return this.l.remove(this.from + index);
    }
    
    public K set(int index, K k) {
      ensureRestrictedIndex(index);
      return this.l.set(this.from + index, k);
    }
    
    public int size() {
      return this.to - this.from;
    }
    
    public void getElements(int from, Object[] a, int offset, int length) {
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
    
    public void addElements(int index, K[] a, int offset, int length) {
      ensureIndex(index);
      this.l.addElements(this.from + index, a, offset, length);
      this.to += length;
      assert assertRange();
    }
    
    public ObjectListIterator<K> listIterator(final int index) {
      ensureIndex(index);
      return new ObjectListIterator<K>() {
          int pos = index;
          
          int last = -1;
          
          public boolean hasNext() {
            return (this.pos < AbstractObjectList.ObjectSubList.this.size());
          }
          
          public boolean hasPrevious() {
            return (this.pos > 0);
          }
          
          public K next() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            return AbstractObjectList.ObjectSubList.this.l.get(AbstractObjectList.ObjectSubList.this.from + (this.last = this.pos++));
          }
          
          public K previous() {
            if (!hasPrevious())
              throw new NoSuchElementException(); 
            return AbstractObjectList.ObjectSubList.this.l.get(AbstractObjectList.ObjectSubList.this.from + (this.last = --this.pos));
          }
          
          public int nextIndex() {
            return this.pos;
          }
          
          public int previousIndex() {
            return this.pos - 1;
          }
          
          public void add(K k) {
            if (this.last == -1)
              throw new IllegalStateException(); 
            AbstractObjectList.ObjectSubList.this.add(this.pos++, k);
            this.last = -1;
            assert AbstractObjectList.ObjectSubList.this.assertRange();
          }
          
          public void set(K k) {
            if (this.last == -1)
              throw new IllegalStateException(); 
            AbstractObjectList.ObjectSubList.this.set(this.last, k);
          }
          
          public void remove() {
            if (this.last == -1)
              throw new IllegalStateException(); 
            AbstractObjectList.ObjectSubList.this.remove(this.last);
            if (this.last < this.pos)
              this.pos--; 
            this.last = -1;
            assert AbstractObjectList.ObjectSubList.this.assertRange();
          }
        };
    }
    
    public ObjectList<K> subList(int from, int to) {
      ensureIndex(from);
      ensureIndex(to);
      if (from > to)
        throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")"); 
      return new ObjectSubList(this, from, to);
    }
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\AbstractObjectList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */