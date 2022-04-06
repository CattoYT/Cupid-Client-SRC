package us.myles.viaversion.libs.fastutil.ints;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import us.myles.viaversion.libs.fastutil.objects.AbstractObjectSet;
import us.myles.viaversion.libs.fastutil.objects.ObjectIterator;
import us.myles.viaversion.libs.fastutil.objects.ObjectSet;

public class Int2IntArrayMap extends AbstractInt2IntMap implements Serializable, Cloneable {
  private static final long serialVersionUID = 1L;
  
  private transient int[] key;
  
  private transient int[] value;
  
  private int size;
  
  public Int2IntArrayMap(int[] key, int[] value) {
    this.key = key;
    this.value = value;
    this.size = key.length;
    if (key.length != value.length)
      throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")"); 
  }
  
  public Int2IntArrayMap() {
    this.key = IntArrays.EMPTY_ARRAY;
    this.value = IntArrays.EMPTY_ARRAY;
  }
  
  public Int2IntArrayMap(int capacity) {
    this.key = new int[capacity];
    this.value = new int[capacity];
  }
  
  public Int2IntArrayMap(Int2IntMap m) {
    this(m.size());
    putAll(m);
  }
  
  public Int2IntArrayMap(Map<? extends Integer, ? extends Integer> m) {
    this(m.size());
    putAll(m);
  }
  
  public Int2IntArrayMap(int[] key, int[] value, int size) {
    this.key = key;
    this.value = value;
    this.size = size;
    if (key.length != value.length)
      throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")"); 
    if (size > key.length)
      throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")"); 
  }
  
  private final class EntrySet extends AbstractObjectSet<Int2IntMap.Entry> implements Int2IntMap.FastEntrySet {
    private EntrySet() {}
    
    public ObjectIterator<Int2IntMap.Entry> iterator() {
      return new ObjectIterator<Int2IntMap.Entry>() {
          int curr = -1;
          
          int next = 0;
          
          public boolean hasNext() {
            return (this.next < Int2IntArrayMap.this.size);
          }
          
          public Int2IntMap.Entry next() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            return new AbstractInt2IntMap.BasicEntry(Int2IntArrayMap.this.key[this.curr = this.next], Int2IntArrayMap.this.value[this.next++]);
          }
          
          public void remove() {
            if (this.curr == -1)
              throw new IllegalStateException(); 
            this.curr = -1;
            int tail = Int2IntArrayMap.this.size-- - this.next--;
            System.arraycopy(Int2IntArrayMap.this.key, this.next + 1, Int2IntArrayMap.this.key, this.next, tail);
            System.arraycopy(Int2IntArrayMap.this.value, this.next + 1, Int2IntArrayMap.this.value, this.next, tail);
          }
        };
    }
    
    public ObjectIterator<Int2IntMap.Entry> fastIterator() {
      return new ObjectIterator<Int2IntMap.Entry>() {
          int next = 0;
          
          int curr = -1;
          
          final AbstractInt2IntMap.BasicEntry entry = new AbstractInt2IntMap.BasicEntry();
          
          public boolean hasNext() {
            return (this.next < Int2IntArrayMap.this.size);
          }
          
          public Int2IntMap.Entry next() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            this.entry.key = Int2IntArrayMap.this.key[this.curr = this.next];
            this.entry.value = Int2IntArrayMap.this.value[this.next++];
            return this.entry;
          }
          
          public void remove() {
            if (this.curr == -1)
              throw new IllegalStateException(); 
            this.curr = -1;
            int tail = Int2IntArrayMap.this.size-- - this.next--;
            System.arraycopy(Int2IntArrayMap.this.key, this.next + 1, Int2IntArrayMap.this.key, this.next, tail);
            System.arraycopy(Int2IntArrayMap.this.value, this.next + 1, Int2IntArrayMap.this.value, this.next, tail);
          }
        };
    }
    
    public int size() {
      return Int2IntArrayMap.this.size;
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      if (e.getKey() == null || !(e.getKey() instanceof Integer))
        return false; 
      if (e.getValue() == null || !(e.getValue() instanceof Integer))
        return false; 
      int k = ((Integer)e.getKey()).intValue();
      return (Int2IntArrayMap.this.containsKey(k) && Int2IntArrayMap.this
        .get(k) == ((Integer)e.getValue()).intValue());
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      if (e.getKey() == null || !(e.getKey() instanceof Integer))
        return false; 
      if (e.getValue() == null || !(e.getValue() instanceof Integer))
        return false; 
      int k = ((Integer)e.getKey()).intValue();
      int v = ((Integer)e.getValue()).intValue();
      int oldPos = Int2IntArrayMap.this.findKey(k);
      if (oldPos == -1 || v != Int2IntArrayMap.this.value[oldPos])
        return false; 
      int tail = Int2IntArrayMap.this.size - oldPos - 1;
      System.arraycopy(Int2IntArrayMap.this.key, oldPos + 1, Int2IntArrayMap.this.key, oldPos, tail);
      System.arraycopy(Int2IntArrayMap.this.value, oldPos + 1, Int2IntArrayMap.this.value, oldPos, tail);
      Int2IntArrayMap.this.size--;
      return true;
    }
  }
  
  public Int2IntMap.FastEntrySet int2IntEntrySet() {
    return new EntrySet();
  }
  
  private int findKey(int k) {
    int[] key = this.key;
    for (int i = this.size; i-- != 0;) {
      if (key[i] == k)
        return i; 
    } 
    return -1;
  }
  
  public int get(int k) {
    int[] key = this.key;
    for (int i = this.size; i-- != 0;) {
      if (key[i] == k)
        return this.value[i]; 
    } 
    return this.defRetValue;
  }
  
  public int size() {
    return this.size;
  }
  
  public void clear() {
    this.size = 0;
  }
  
  public boolean containsKey(int k) {
    return (findKey(k) != -1);
  }
  
  public boolean containsValue(int v) {
    for (int i = this.size; i-- != 0;) {
      if (this.value[i] == v)
        return true; 
    } 
    return false;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  public int put(int k, int v) {
    int oldKey = findKey(k);
    if (oldKey != -1) {
      int oldValue = this.value[oldKey];
      this.value[oldKey] = v;
      return oldValue;
    } 
    if (this.size == this.key.length) {
      int[] newKey = new int[(this.size == 0) ? 2 : (this.size * 2)];
      int[] newValue = new int[(this.size == 0) ? 2 : (this.size * 2)];
      for (int i = this.size; i-- != 0; ) {
        newKey[i] = this.key[i];
        newValue[i] = this.value[i];
      } 
      this.key = newKey;
      this.value = newValue;
    } 
    this.key[this.size] = k;
    this.value[this.size] = v;
    this.size++;
    return this.defRetValue;
  }
  
  public int remove(int k) {
    int oldPos = findKey(k);
    if (oldPos == -1)
      return this.defRetValue; 
    int oldValue = this.value[oldPos];
    int tail = this.size - oldPos - 1;
    System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
    System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
    this.size--;
    return oldValue;
  }
  
  public IntSet keySet() {
    return new AbstractIntSet() {
        public boolean contains(int k) {
          return (Int2IntArrayMap.this.findKey(k) != -1);
        }
        
        public boolean remove(int k) {
          int oldPos = Int2IntArrayMap.this.findKey(k);
          if (oldPos == -1)
            return false; 
          int tail = Int2IntArrayMap.this.size - oldPos - 1;
          System.arraycopy(Int2IntArrayMap.this.key, oldPos + 1, Int2IntArrayMap.this.key, oldPos, tail);
          System.arraycopy(Int2IntArrayMap.this.value, oldPos + 1, Int2IntArrayMap.this.value, oldPos, tail);
          Int2IntArrayMap.this.size--;
          return true;
        }
        
        public IntIterator iterator() {
          return new IntIterator() {
              int pos = 0;
              
              public boolean hasNext() {
                return (this.pos < Int2IntArrayMap.this.size);
              }
              
              public int nextInt() {
                if (!hasNext())
                  throw new NoSuchElementException(); 
                return Int2IntArrayMap.this.key[this.pos++];
              }
              
              public void remove() {
                if (this.pos == 0)
                  throw new IllegalStateException(); 
                int tail = Int2IntArrayMap.this.size - this.pos;
                System.arraycopy(Int2IntArrayMap.this.key, this.pos, Int2IntArrayMap.this.key, this.pos - 1, tail);
                System.arraycopy(Int2IntArrayMap.this.value, this.pos, Int2IntArrayMap.this.value, this.pos - 1, tail);
                Int2IntArrayMap.this.size--;
                this.pos--;
              }
            };
        }
        
        public int size() {
          return Int2IntArrayMap.this.size;
        }
        
        public void clear() {
          Int2IntArrayMap.this.clear();
        }
      };
  }
  
  public IntCollection values() {
    return new AbstractIntCollection() {
        public boolean contains(int v) {
          return Int2IntArrayMap.this.containsValue(v);
        }
        
        public IntIterator iterator() {
          return new IntIterator() {
              int pos = 0;
              
              public boolean hasNext() {
                return (this.pos < Int2IntArrayMap.this.size);
              }
              
              public int nextInt() {
                if (!hasNext())
                  throw new NoSuchElementException(); 
                return Int2IntArrayMap.this.value[this.pos++];
              }
              
              public void remove() {
                if (this.pos == 0)
                  throw new IllegalStateException(); 
                int tail = Int2IntArrayMap.this.size - this.pos;
                System.arraycopy(Int2IntArrayMap.this.key, this.pos, Int2IntArrayMap.this.key, this.pos - 1, tail);
                System.arraycopy(Int2IntArrayMap.this.value, this.pos, Int2IntArrayMap.this.value, this.pos - 1, tail);
                Int2IntArrayMap.this.size--;
              }
            };
        }
        
        public int size() {
          return Int2IntArrayMap.this.size;
        }
        
        public void clear() {
          Int2IntArrayMap.this.clear();
        }
      };
  }
  
  public Int2IntArrayMap clone() {
    Int2IntArrayMap c;
    try {
      c = (Int2IntArrayMap)super.clone();
    } catch (CloneNotSupportedException cantHappen) {
      throw new InternalError();
    } 
    c.key = (int[])this.key.clone();
    c.value = (int[])this.value.clone();
    return c;
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    for (int i = 0; i < this.size; i++) {
      s.writeInt(this.key[i]);
      s.writeInt(this.value[i]);
    } 
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.key = new int[this.size];
    this.value = new int[this.size];
    for (int i = 0; i < this.size; i++) {
      this.key[i] = s.readInt();
      this.value[i] = s.readInt();
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\Int2IntArrayMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */