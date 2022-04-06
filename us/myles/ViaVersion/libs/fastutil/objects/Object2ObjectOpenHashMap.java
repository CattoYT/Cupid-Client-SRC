package us.myles.viaversion.libs.fastutil.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import us.myles.viaversion.libs.fastutil.Hash;
import us.myles.viaversion.libs.fastutil.HashCommon;

public class Object2ObjectOpenHashMap<K, V> extends AbstractObject2ObjectMap<K, V> implements Serializable, Cloneable, Hash {
  private static final long serialVersionUID = 0L;
  
  private static final boolean ASSERTS = false;
  
  protected transient K[] key;
  
  protected transient V[] value;
  
  protected transient int mask;
  
  protected transient boolean containsNullKey;
  
  protected transient int n;
  
  protected transient int maxFill;
  
  protected final transient int minN;
  
  protected int size;
  
  protected final float f;
  
  protected transient Object2ObjectMap.FastEntrySet<K, V> entries;
  
  protected transient ObjectSet<K> keys;
  
  protected transient ObjectCollection<V> values;
  
  public Object2ObjectOpenHashMap(int expected, float f) {
    if (f <= 0.0F || f > 1.0F)
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1"); 
    if (expected < 0)
      throw new IllegalArgumentException("The expected number of elements must be nonnegative"); 
    this.f = f;
    this.minN = this.n = HashCommon.arraySize(expected, f);
    this.mask = this.n - 1;
    this.maxFill = HashCommon.maxFill(this.n, f);
    this.key = (K[])new Object[this.n + 1];
    this.value = (V[])new Object[this.n + 1];
  }
  
  public Object2ObjectOpenHashMap(int expected) {
    this(expected, 0.75F);
  }
  
  public Object2ObjectOpenHashMap() {
    this(16, 0.75F);
  }
  
  public Object2ObjectOpenHashMap(Map<? extends K, ? extends V> m, float f) {
    this(m.size(), f);
    putAll(m);
  }
  
  public Object2ObjectOpenHashMap(Map<? extends K, ? extends V> m) {
    this(m, 0.75F);
  }
  
  public Object2ObjectOpenHashMap(Object2ObjectMap<K, V> m, float f) {
    this(m.size(), f);
    putAll(m);
  }
  
  public Object2ObjectOpenHashMap(Object2ObjectMap<K, V> m) {
    this(m, 0.75F);
  }
  
  public Object2ObjectOpenHashMap(K[] k, V[] v, float f) {
    this(k.length, f);
    if (k.length != v.length)
      throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")"); 
    for (int i = 0; i < k.length; i++)
      put(k[i], v[i]); 
  }
  
  public Object2ObjectOpenHashMap(K[] k, V[] v) {
    this(k, v, 0.75F);
  }
  
  private int realSize() {
    return this.containsNullKey ? (this.size - 1) : this.size;
  }
  
  private void ensureCapacity(int capacity) {
    int needed = HashCommon.arraySize(capacity, this.f);
    if (needed > this.n)
      rehash(needed); 
  }
  
  private void tryCapacity(long capacity) {
    int needed = (int)Math.min(1073741824L, 
        Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil(((float)capacity / this.f)))));
    if (needed > this.n)
      rehash(needed); 
  }
  
  private V removeEntry(int pos) {
    V oldValue = this.value[pos];
    this.value[pos] = null;
    this.size--;
    shiftKeys(pos);
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return oldValue;
  }
  
  private V removeNullEntry() {
    this.containsNullKey = false;
    this.key[this.n] = null;
    V oldValue = this.value[this.n];
    this.value[this.n] = null;
    this.size--;
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return oldValue;
  }
  
  public void putAll(Map<? extends K, ? extends V> m) {
    if (this.f <= 0.5D) {
      ensureCapacity(m.size());
    } else {
      tryCapacity((size() + m.size()));
    } 
    super.putAll(m);
  }
  
  private int find(K k) {
    if (k == null)
      return this.containsNullKey ? this.n : -(this.n + 1); 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return -(pos + 1); 
    if (k.equals(curr))
      return pos; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return -(pos + 1); 
      if (k.equals(curr))
        return pos; 
    } 
  }
  
  private void insert(int pos, K k, V v) {
    if (pos == this.n)
      this.containsNullKey = true; 
    this.key[pos] = k;
    this.value[pos] = v;
    if (this.size++ >= this.maxFill)
      rehash(HashCommon.arraySize(this.size + 1, this.f)); 
  }
  
  public V put(K k, V v) {
    int pos = find(k);
    if (pos < 0) {
      insert(-pos - 1, k, v);
      return this.defRetValue;
    } 
    V oldValue = this.value[pos];
    this.value[pos] = v;
    return oldValue;
  }
  
  protected final void shiftKeys(int pos) {
    K[] key = this.key;
    while (true) {
      K curr;
      int last;
      pos = (last = pos) + 1 & this.mask;
      while (true) {
        if ((curr = key[pos]) == null) {
          key[last] = null;
          this.value[last] = null;
          return;
        } 
        int slot = HashCommon.mix(curr.hashCode()) & this.mask;
        if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
          break; 
        pos = pos + 1 & this.mask;
      } 
      key[last] = curr;
      this.value[last] = this.value[pos];
    } 
  }
  
  public V remove(Object k) {
    if (k == null) {
      if (this.containsNullKey)
        return removeNullEntry(); 
      return this.defRetValue;
    } 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return this.defRetValue; 
    if (k.equals(curr))
      return removeEntry(pos); 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return this.defRetValue; 
      if (k.equals(curr))
        return removeEntry(pos); 
    } 
  }
  
  public V get(Object k) {
    if (k == null)
      return this.containsNullKey ? this.value[this.n] : this.defRetValue; 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return this.defRetValue; 
    if (k.equals(curr))
      return this.value[pos]; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return this.defRetValue; 
      if (k.equals(curr))
        return this.value[pos]; 
    } 
  }
  
  public boolean containsKey(Object k) {
    if (k == null)
      return this.containsNullKey; 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return false; 
    if (k.equals(curr))
      return true; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return false; 
      if (k.equals(curr))
        return true; 
    } 
  }
  
  public boolean containsValue(Object v) {
    V[] value = this.value;
    K[] key = this.key;
    if (this.containsNullKey && Objects.equals(value[this.n], v))
      return true; 
    for (int i = this.n; i-- != 0;) {
      if (key[i] != null && Objects.equals(value[i], v))
        return true; 
    } 
    return false;
  }
  
  public void clear() {
    if (this.size == 0)
      return; 
    this.size = 0;
    this.containsNullKey = false;
    Arrays.fill((Object[])this.key, (Object)null);
    Arrays.fill((Object[])this.value, (Object)null);
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  final class MapEntry implements Object2ObjectMap.Entry<K, V>, Map.Entry<K, V> {
    int index;
    
    MapEntry(int index) {
      this.index = index;
    }
    
    MapEntry() {}
    
    public K getKey() {
      return Object2ObjectOpenHashMap.this.key[this.index];
    }
    
    public V getValue() {
      return Object2ObjectOpenHashMap.this.value[this.index];
    }
    
    public V setValue(V v) {
      V oldValue = Object2ObjectOpenHashMap.this.value[this.index];
      Object2ObjectOpenHashMap.this.value[this.index] = v;
      return oldValue;
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<K, V> e = (Map.Entry<K, V>)o;
      return (Objects.equals(Object2ObjectOpenHashMap.this.key[this.index], e.getKey()) && 
        Objects.equals(Object2ObjectOpenHashMap.this.value[this.index], e.getValue()));
    }
    
    public int hashCode() {
      return ((Object2ObjectOpenHashMap.this.key[this.index] == null) ? 0 : Object2ObjectOpenHashMap.this.key[this.index].hashCode()) ^ (
        (Object2ObjectOpenHashMap.this.value[this.index] == null) ? 0 : Object2ObjectOpenHashMap.this.value[this.index].hashCode());
    }
    
    public String toString() {
      return (new StringBuilder()).append(Object2ObjectOpenHashMap.this.key[this.index]).append("=>").append(Object2ObjectOpenHashMap.this.value[this.index]).toString();
    }
  }
  
  private class MapIterator {
    int pos = Object2ObjectOpenHashMap.this.n;
    
    int last = -1;
    
    int c = Object2ObjectOpenHashMap.this.size;
    
    boolean mustReturnNullKey = Object2ObjectOpenHashMap.this.containsNullKey;
    
    ObjectArrayList<K> wrapped;
    
    public boolean hasNext() {
      return (this.c != 0);
    }
    
    public int nextEntry() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.c--;
      if (this.mustReturnNullKey) {
        this.mustReturnNullKey = false;
        return this.last = Object2ObjectOpenHashMap.this.n;
      } 
      K[] key = Object2ObjectOpenHashMap.this.key;
      while (true) {
        if (--this.pos < 0) {
          this.last = Integer.MIN_VALUE;
          K k = this.wrapped.get(-this.pos - 1);
          int p = HashCommon.mix(k.hashCode()) & Object2ObjectOpenHashMap.this.mask;
          while (!k.equals(key[p]))
            p = p + 1 & Object2ObjectOpenHashMap.this.mask; 
          return p;
        } 
        if (key[this.pos] != null)
          return this.last = this.pos; 
      } 
    }
    
    private void shiftKeys(int pos) {
      K[] key = Object2ObjectOpenHashMap.this.key;
      while (true) {
        K curr;
        int last;
        pos = (last = pos) + 1 & Object2ObjectOpenHashMap.this.mask;
        while (true) {
          if ((curr = key[pos]) == null) {
            key[last] = null;
            Object2ObjectOpenHashMap.this.value[last] = null;
            return;
          } 
          int slot = HashCommon.mix(curr.hashCode()) & Object2ObjectOpenHashMap.this.mask;
          if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
            break; 
          pos = pos + 1 & Object2ObjectOpenHashMap.this.mask;
        } 
        if (pos < last) {
          if (this.wrapped == null)
            this.wrapped = new ObjectArrayList<>(2); 
          this.wrapped.add(key[pos]);
        } 
        key[last] = curr;
        Object2ObjectOpenHashMap.this.value[last] = Object2ObjectOpenHashMap.this.value[pos];
      } 
    }
    
    public void remove() {
      if (this.last == -1)
        throw new IllegalStateException(); 
      if (this.last == Object2ObjectOpenHashMap.this.n) {
        Object2ObjectOpenHashMap.this.containsNullKey = false;
        Object2ObjectOpenHashMap.this.key[Object2ObjectOpenHashMap.this.n] = null;
        Object2ObjectOpenHashMap.this.value[Object2ObjectOpenHashMap.this.n] = null;
      } else if (this.pos >= 0) {
        shiftKeys(this.last);
      } else {
        Object2ObjectOpenHashMap.this.remove(this.wrapped.set(-this.pos - 1, null));
        this.last = -1;
        return;
      } 
      Object2ObjectOpenHashMap.this.size--;
      this.last = -1;
    }
    
    public int skip(int n) {
      int i = n;
      while (i-- != 0 && hasNext())
        nextEntry(); 
      return n - i - 1;
    }
    
    private MapIterator() {}
  }
  
  private class EntryIterator extends MapIterator implements ObjectIterator<Object2ObjectMap.Entry<K, V>> {
    private Object2ObjectOpenHashMap<K, V>.MapEntry entry;
    
    private EntryIterator() {}
    
    public Object2ObjectOpenHashMap<K, V>.MapEntry next() {
      return this.entry = new Object2ObjectOpenHashMap.MapEntry(nextEntry());
    }
    
    public void remove() {
      super.remove();
      this.entry.index = -1;
    }
  }
  
  private class FastEntryIterator extends MapIterator implements ObjectIterator<Object2ObjectMap.Entry<K, V>> {
    private final Object2ObjectOpenHashMap<K, V>.MapEntry entry;
    
    private FastEntryIterator() {
      this.entry = new Object2ObjectOpenHashMap.MapEntry();
    }
    
    public Object2ObjectOpenHashMap<K, V>.MapEntry next() {
      this.entry.index = nextEntry();
      return this.entry;
    }
  }
  
  private final class MapEntrySet extends AbstractObjectSet<Object2ObjectMap.Entry<K, V>> implements Object2ObjectMap.FastEntrySet<K, V> {
    private MapEntrySet() {}
    
    public ObjectIterator<Object2ObjectMap.Entry<K, V>> iterator() {
      return new Object2ObjectOpenHashMap.EntryIterator();
    }
    
    public ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator() {
      return new Object2ObjectOpenHashMap.FastEntryIterator();
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      K k = (K)e.getKey();
      V v = (V)e.getValue();
      if (k == null)
        return (Object2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Object2ObjectOpenHashMap.this.value[Object2ObjectOpenHashMap.this.n], v)); 
      K[] key = Object2ObjectOpenHashMap.this.key;
      K curr;
      int pos;
      if ((curr = key[pos = HashCommon.mix(k.hashCode()) & Object2ObjectOpenHashMap.this.mask]) == null)
        return false; 
      if (k.equals(curr))
        return Objects.equals(Object2ObjectOpenHashMap.this.value[pos], v); 
      while (true) {
        if ((curr = key[pos = pos + 1 & Object2ObjectOpenHashMap.this.mask]) == null)
          return false; 
        if (k.equals(curr))
          return Objects.equals(Object2ObjectOpenHashMap.this.value[pos], v); 
      } 
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      K k = (K)e.getKey();
      V v = (V)e.getValue();
      if (k == null) {
        if (Object2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Object2ObjectOpenHashMap.this.value[Object2ObjectOpenHashMap.this.n], v)) {
          Object2ObjectOpenHashMap.this.removeNullEntry();
          return true;
        } 
        return false;
      } 
      K[] key = Object2ObjectOpenHashMap.this.key;
      K curr;
      int pos;
      if ((curr = key[pos = HashCommon.mix(k.hashCode()) & Object2ObjectOpenHashMap.this.mask]) == null)
        return false; 
      if (curr.equals(k)) {
        if (Objects.equals(Object2ObjectOpenHashMap.this.value[pos], v)) {
          Object2ObjectOpenHashMap.this.removeEntry(pos);
          return true;
        } 
        return false;
      } 
      while (true) {
        if ((curr = key[pos = pos + 1 & Object2ObjectOpenHashMap.this.mask]) == null)
          return false; 
        if (curr.equals(k) && 
          Objects.equals(Object2ObjectOpenHashMap.this.value[pos], v)) {
          Object2ObjectOpenHashMap.this.removeEntry(pos);
          return true;
        } 
      } 
    }
    
    public int size() {
      return Object2ObjectOpenHashMap.this.size;
    }
    
    public void clear() {
      Object2ObjectOpenHashMap.this.clear();
    }
    
    public void forEach(Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
      if (Object2ObjectOpenHashMap.this.containsNullKey)
        consumer.accept(new AbstractObject2ObjectMap.BasicEntry<>(Object2ObjectOpenHashMap.this.key[Object2ObjectOpenHashMap.this.n], Object2ObjectOpenHashMap.this.value[Object2ObjectOpenHashMap.this.n])); 
      for (int pos = Object2ObjectOpenHashMap.this.n; pos-- != 0;) {
        if (Object2ObjectOpenHashMap.this.key[pos] != null)
          consumer.accept(new AbstractObject2ObjectMap.BasicEntry<>(Object2ObjectOpenHashMap.this.key[pos], Object2ObjectOpenHashMap.this.value[pos])); 
      } 
    }
    
    public void fastForEach(Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
      AbstractObject2ObjectMap.BasicEntry<K, V> entry = new AbstractObject2ObjectMap.BasicEntry<>();
      if (Object2ObjectOpenHashMap.this.containsNullKey) {
        entry.key = Object2ObjectOpenHashMap.this.key[Object2ObjectOpenHashMap.this.n];
        entry.value = Object2ObjectOpenHashMap.this.value[Object2ObjectOpenHashMap.this.n];
        consumer.accept(entry);
      } 
      for (int pos = Object2ObjectOpenHashMap.this.n; pos-- != 0;) {
        if (Object2ObjectOpenHashMap.this.key[pos] != null) {
          entry.key = Object2ObjectOpenHashMap.this.key[pos];
          entry.value = Object2ObjectOpenHashMap.this.value[pos];
          consumer.accept(entry);
        } 
      } 
    }
  }
  
  public Object2ObjectMap.FastEntrySet<K, V> object2ObjectEntrySet() {
    if (this.entries == null)
      this.entries = new MapEntrySet(); 
    return this.entries;
  }
  
  private final class KeyIterator extends MapIterator implements ObjectIterator<K> {
    public K next() {
      return Object2ObjectOpenHashMap.this.key[nextEntry()];
    }
  }
  
  private final class KeySet extends AbstractObjectSet<K> {
    private KeySet() {}
    
    public ObjectIterator<K> iterator() {
      return new Object2ObjectOpenHashMap.KeyIterator();
    }
    
    public void forEach(Consumer<? super K> consumer) {
      if (Object2ObjectOpenHashMap.this.containsNullKey)
        consumer.accept(Object2ObjectOpenHashMap.this.key[Object2ObjectOpenHashMap.this.n]); 
      for (int pos = Object2ObjectOpenHashMap.this.n; pos-- != 0; ) {
        K k = Object2ObjectOpenHashMap.this.key[pos];
        if (k != null)
          consumer.accept(k); 
      } 
    }
    
    public int size() {
      return Object2ObjectOpenHashMap.this.size;
    }
    
    public boolean contains(Object k) {
      return Object2ObjectOpenHashMap.this.containsKey(k);
    }
    
    public boolean remove(Object k) {
      int oldSize = Object2ObjectOpenHashMap.this.size;
      Object2ObjectOpenHashMap.this.remove(k);
      return (Object2ObjectOpenHashMap.this.size != oldSize);
    }
    
    public void clear() {
      Object2ObjectOpenHashMap.this.clear();
    }
  }
  
  public ObjectSet<K> keySet() {
    if (this.keys == null)
      this.keys = new KeySet(); 
    return this.keys;
  }
  
  private final class ValueIterator extends MapIterator implements ObjectIterator<V> {
    public V next() {
      return Object2ObjectOpenHashMap.this.value[nextEntry()];
    }
  }
  
  public ObjectCollection<V> values() {
    if (this.values == null)
      this.values = new AbstractObjectCollection<V>() {
          public ObjectIterator<V> iterator() {
            return new Object2ObjectOpenHashMap.ValueIterator();
          }
          
          public int size() {
            return Object2ObjectOpenHashMap.this.size;
          }
          
          public boolean contains(Object v) {
            return Object2ObjectOpenHashMap.this.containsValue(v);
          }
          
          public void clear() {
            Object2ObjectOpenHashMap.this.clear();
          }
          
          public void forEach(Consumer<? super V> consumer) {
            if (Object2ObjectOpenHashMap.this.containsNullKey)
              consumer.accept(Object2ObjectOpenHashMap.this.value[Object2ObjectOpenHashMap.this.n]); 
            for (int pos = Object2ObjectOpenHashMap.this.n; pos-- != 0;) {
              if (Object2ObjectOpenHashMap.this.key[pos] != null)
                consumer.accept(Object2ObjectOpenHashMap.this.value[pos]); 
            } 
          }
        }; 
    return this.values;
  }
  
  public boolean trim() {
    return trim(this.size);
  }
  
  public boolean trim(int n) {
    int l = HashCommon.nextPowerOfTwo((int)Math.ceil((n / this.f)));
    if (l >= this.n || this.size > HashCommon.maxFill(l, this.f))
      return true; 
    try {
      rehash(l);
    } catch (OutOfMemoryError cantDoIt) {
      return false;
    } 
    return true;
  }
  
  protected void rehash(int newN) {
    K[] key = this.key;
    V[] value = this.value;
    int mask = newN - 1;
    K[] newKey = (K[])new Object[newN + 1];
    V[] newValue = (V[])new Object[newN + 1];
    int i = this.n;
    for (int j = realSize(); j-- != 0; ) {
      while (key[--i] == null);
      int pos;
      if (newKey[pos = HashCommon.mix(key[i].hashCode()) & mask] != null)
        while (newKey[pos = pos + 1 & mask] != null); 
      newKey[pos] = key[i];
      newValue[pos] = value[i];
    } 
    newValue[newN] = value[this.n];
    this.n = newN;
    this.mask = mask;
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.key = newKey;
    this.value = newValue;
  }
  
  public Object2ObjectOpenHashMap<K, V> clone() {
    Object2ObjectOpenHashMap<K, V> c;
    try {
      c = (Object2ObjectOpenHashMap<K, V>)super.clone();
    } catch (CloneNotSupportedException cantHappen) {
      throw new InternalError();
    } 
    c.keys = null;
    c.values = null;
    c.entries = null;
    c.containsNullKey = this.containsNullKey;
    c.key = (K[])this.key.clone();
    c.value = (V[])this.value.clone();
    return c;
  }
  
  public int hashCode() {
    int h = 0;
    for (int j = realSize(), i = 0, t = 0; j-- != 0; ) {
      while (this.key[i] == null)
        i++; 
      if (this != this.key[i])
        t = this.key[i].hashCode(); 
      if (this != this.value[i])
        t ^= (this.value[i] == null) ? 0 : this.value[i].hashCode(); 
      h += t;
      i++;
    } 
    if (this.containsNullKey)
      h += (this.value[this.n] == null) ? 0 : this.value[this.n].hashCode(); 
    return h;
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    K[] key = this.key;
    V[] value = this.value;
    MapIterator i = new MapIterator();
    s.defaultWriteObject();
    for (int j = this.size; j-- != 0; ) {
      int e = i.nextEntry();
      s.writeObject(key[e]);
      s.writeObject(value[e]);
    } 
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.n = HashCommon.arraySize(this.size, this.f);
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.mask = this.n - 1;
    K[] key = this.key = (K[])new Object[this.n + 1];
    V[] value = this.value = (V[])new Object[this.n + 1];
    for (int i = this.size; i-- != 0; ) {
      int pos;
      K k = (K)s.readObject();
      V v = (V)s.readObject();
      if (k == null) {
        pos = this.n;
        this.containsNullKey = true;
      } else {
        pos = HashCommon.mix(k.hashCode()) & this.mask;
        while (key[pos] != null)
          pos = pos + 1 & this.mask; 
      } 
      key[pos] = k;
      value[pos] = v;
    } 
  }
  
  private void checkTable() {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\Object2ObjectOpenHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */