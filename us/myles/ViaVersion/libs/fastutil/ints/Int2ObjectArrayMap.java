package us.myles.viaversion.libs.fastutil.ints;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import us.myles.viaversion.libs.fastutil.objects.AbstractObjectCollection;
import us.myles.viaversion.libs.fastutil.objects.AbstractObjectSet;
import us.myles.viaversion.libs.fastutil.objects.ObjectArrays;
import us.myles.viaversion.libs.fastutil.objects.ObjectCollection;
import us.myles.viaversion.libs.fastutil.objects.ObjectIterator;
import us.myles.viaversion.libs.fastutil.objects.ObjectSet;

public class Int2ObjectArrayMap<V> extends AbstractInt2ObjectMap<V> implements Serializable, Cloneable {
  private static final long serialVersionUID = 1L;
  
  private transient int[] key;
  
  private transient Object[] value;
  
  private int size;
  
  public Int2ObjectArrayMap(int[] key, Object[] value) {
    this.key = key;
    this.value = value;
    this.size = key.length;
    if (key.length != value.length)
      throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")"); 
  }
  
  public Int2ObjectArrayMap() {
    this.key = IntArrays.EMPTY_ARRAY;
    this.value = ObjectArrays.EMPTY_ARRAY;
  }
  
  public Int2ObjectArrayMap(int capacity) {
    this.key = new int[capacity];
    this.value = new Object[capacity];
  }
  
  public Int2ObjectArrayMap(Int2ObjectMap<V> m) {
    this(m.size());
    putAll(m);
  }
  
  public Int2ObjectArrayMap(Map<? extends Integer, ? extends V> m) {
    this(m.size());
    putAll(m);
  }
  
  public Int2ObjectArrayMap(int[] key, Object[] value, int size) {
    this.key = key;
    this.value = value;
    this.size = size;
    if (key.length != value.length)
      throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")"); 
    if (size > key.length)
      throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")"); 
  }
  
  private final class EntrySet extends AbstractObjectSet<Int2ObjectMap.Entry<V>> implements Int2ObjectMap.FastEntrySet<V> {
    private EntrySet() {}
    
    public ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
      return new ObjectIterator<Int2ObjectMap.Entry<V>>() {
          int curr = -1;
          
          int next = 0;
          
          public boolean hasNext() {
            return (this.next < Int2ObjectArrayMap.this.size);
          }
          
          public Int2ObjectMap.Entry<V> next() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            return new AbstractInt2ObjectMap.BasicEntry<>(Int2ObjectArrayMap.this.key[this.curr = this.next], (V)Int2ObjectArrayMap.this.value[this.next++]);
          }
          
          public void remove() {
            if (this.curr == -1)
              throw new IllegalStateException(); 
            this.curr = -1;
            int tail = Int2ObjectArrayMap.this.size-- - this.next--;
            System.arraycopy(Int2ObjectArrayMap.this.key, this.next + 1, Int2ObjectArrayMap.this.key, this.next, tail);
            System.arraycopy(Int2ObjectArrayMap.this.value, this.next + 1, Int2ObjectArrayMap.this.value, this.next, tail);
            Int2ObjectArrayMap.this.value[Int2ObjectArrayMap.this.size] = null;
          }
        };
    }
    
    public ObjectIterator<Int2ObjectMap.Entry<V>> fastIterator() {
      return new ObjectIterator<Int2ObjectMap.Entry<V>>() {
          int next = 0;
          
          int curr = -1;
          
          final AbstractInt2ObjectMap.BasicEntry<V> entry = new AbstractInt2ObjectMap.BasicEntry<>();
          
          public boolean hasNext() {
            return (this.next < Int2ObjectArrayMap.this.size);
          }
          
          public Int2ObjectMap.Entry<V> next() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            this.entry.key = Int2ObjectArrayMap.this.key[this.curr = this.next];
            this.entry.value = (V)Int2ObjectArrayMap.this.value[this.next++];
            return this.entry;
          }
          
          public void remove() {
            if (this.curr == -1)
              throw new IllegalStateException(); 
            this.curr = -1;
            int tail = Int2ObjectArrayMap.this.size-- - this.next--;
            System.arraycopy(Int2ObjectArrayMap.this.key, this.next + 1, Int2ObjectArrayMap.this.key, this.next, tail);
            System.arraycopy(Int2ObjectArrayMap.this.value, this.next + 1, Int2ObjectArrayMap.this.value, this.next, tail);
            Int2ObjectArrayMap.this.value[Int2ObjectArrayMap.this.size] = null;
          }
        };
    }
    
    public int size() {
      return Int2ObjectArrayMap.this.size;
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      if (e.getKey() == null || !(e.getKey() instanceof Integer))
        return false; 
      int k = ((Integer)e.getKey()).intValue();
      return (Int2ObjectArrayMap.this.containsKey(k) && 
        Objects.equals(Int2ObjectArrayMap.this.get(k), e.getValue()));
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      if (e.getKey() == null || !(e.getKey() instanceof Integer))
        return false; 
      int k = ((Integer)e.getKey()).intValue();
      V v = (V)e.getValue();
      int oldPos = Int2ObjectArrayMap.this.findKey(k);
      if (oldPos == -1 || !Objects.equals(v, Int2ObjectArrayMap.this.value[oldPos]))
        return false; 
      int tail = Int2ObjectArrayMap.this.size - oldPos - 1;
      System.arraycopy(Int2ObjectArrayMap.this.key, oldPos + 1, Int2ObjectArrayMap.this.key, oldPos, tail);
      System.arraycopy(Int2ObjectArrayMap.this.value, oldPos + 1, Int2ObjectArrayMap.this.value, oldPos, tail);
      Int2ObjectArrayMap.this.size--;
      Int2ObjectArrayMap.this.value[Int2ObjectArrayMap.this.size] = null;
      return true;
    }
  }
  
  public Int2ObjectMap.FastEntrySet<V> int2ObjectEntrySet() {
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
  
  public V get(int k) {
    int[] key = this.key;
    for (int i = this.size; i-- != 0;) {
      if (key[i] == k)
        return (V)this.value[i]; 
    } 
    return this.defRetValue;
  }
  
  public int size() {
    return this.size;
  }
  
  public void clear() {
    for (int i = this.size; i-- != 0;)
      this.value[i] = null; 
    this.size = 0;
  }
  
  public boolean containsKey(int k) {
    return (findKey(k) != -1);
  }
  
  public boolean containsValue(Object v) {
    for (int i = this.size; i-- != 0;) {
      if (Objects.equals(this.value[i], v))
        return true; 
    } 
    return false;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  public V put(int k, V v) {
    int oldKey = findKey(k);
    if (oldKey != -1) {
      V oldValue = (V)this.value[oldKey];
      this.value[oldKey] = v;
      return oldValue;
    } 
    if (this.size == this.key.length) {
      int[] newKey = new int[(this.size == 0) ? 2 : (this.size * 2)];
      Object[] newValue = new Object[(this.size == 0) ? 2 : (this.size * 2)];
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
  
  public V remove(int k) {
    int oldPos = findKey(k);
    if (oldPos == -1)
      return this.defRetValue; 
    V oldValue = (V)this.value[oldPos];
    int tail = this.size - oldPos - 1;
    System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
    System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
    this.size--;
    this.value[this.size] = null;
    return oldValue;
  }
  
  public IntSet keySet() {
    return new AbstractIntSet() {
        public boolean contains(int k) {
          return (Int2ObjectArrayMap.this.findKey(k) != -1);
        }
        
        public boolean remove(int k) {
          int oldPos = Int2ObjectArrayMap.this.findKey(k);
          if (oldPos == -1)
            return false; 
          int tail = Int2ObjectArrayMap.this.size - oldPos - 1;
          System.arraycopy(Int2ObjectArrayMap.this.key, oldPos + 1, Int2ObjectArrayMap.this.key, oldPos, tail);
          System.arraycopy(Int2ObjectArrayMap.this.value, oldPos + 1, Int2ObjectArrayMap.this.value, oldPos, tail);
          Int2ObjectArrayMap.this.size--;
          return true;
        }
        
        public IntIterator iterator() {
          return new IntIterator() {
              int pos = 0;
              
              public boolean hasNext() {
                return (this.pos < Int2ObjectArrayMap.this.size);
              }
              
              public int nextInt() {
                if (!hasNext())
                  throw new NoSuchElementException(); 
                return Int2ObjectArrayMap.this.key[this.pos++];
              }
              
              public void remove() {
                if (this.pos == 0)
                  throw new IllegalStateException(); 
                int tail = Int2ObjectArrayMap.this.size - this.pos;
                System.arraycopy(Int2ObjectArrayMap.this.key, this.pos, Int2ObjectArrayMap.this.key, this.pos - 1, tail);
                System.arraycopy(Int2ObjectArrayMap.this.value, this.pos, Int2ObjectArrayMap.this.value, this.pos - 1, tail);
                Int2ObjectArrayMap.this.size--;
                this.pos--;
              }
            };
        }
        
        public int size() {
          return Int2ObjectArrayMap.this.size;
        }
        
        public void clear() {
          Int2ObjectArrayMap.this.clear();
        }
      };
  }
  
  public ObjectCollection<V> values() {
    return (ObjectCollection<V>)new AbstractObjectCollection<V>() {
        public boolean contains(Object v) {
          return Int2ObjectArrayMap.this.containsValue(v);
        }
        
        public ObjectIterator<V> iterator() {
          return new ObjectIterator<V>() {
              int pos = 0;
              
              public boolean hasNext() {
                return (this.pos < Int2ObjectArrayMap.this.size);
              }
              
              public V next() {
                if (!hasNext())
                  throw new NoSuchElementException(); 
                return (V)Int2ObjectArrayMap.this.value[this.pos++];
              }
              
              public void remove() {
                if (this.pos == 0)
                  throw new IllegalStateException(); 
                int tail = Int2ObjectArrayMap.this.size - this.pos;
                System.arraycopy(Int2ObjectArrayMap.this.key, this.pos, Int2ObjectArrayMap.this.key, this.pos - 1, tail);
                System.arraycopy(Int2ObjectArrayMap.this.value, this.pos, Int2ObjectArrayMap.this.value, this.pos - 1, tail);
                Int2ObjectArrayMap.this.size--;
              }
            };
        }
        
        public int size() {
          return Int2ObjectArrayMap.this.size;
        }
        
        public void clear() {
          Int2ObjectArrayMap.this.clear();
        }
      };
  }
  
  public Int2ObjectArrayMap<V> clone() {
    Int2ObjectArrayMap<V> c;
    try {
      c = (Int2ObjectArrayMap<V>)super.clone();
    } catch (CloneNotSupportedException cantHappen) {
      throw new InternalError();
    } 
    c.key = (int[])this.key.clone();
    c.value = (Object[])this.value.clone();
    return c;
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    for (int i = 0; i < this.size; i++) {
      s.writeInt(this.key[i]);
      s.writeObject(this.value[i]);
    } 
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.key = new int[this.size];
    this.value = new Object[this.size];
    for (int i = 0; i < this.size; i++) {
      this.key[i] = s.readInt();
      this.value[i] = s.readObject();
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\Int2ObjectArrayMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */