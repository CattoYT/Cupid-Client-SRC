package us.myles.viaversion.libs.fastutil.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import us.myles.viaversion.libs.fastutil.Hash;
import us.myles.viaversion.libs.fastutil.HashCommon;

public class ObjectOpenHashSet<K> extends AbstractObjectSet<K> implements Serializable, Cloneable, Hash {
  private static final long serialVersionUID = 0L;
  
  private static final boolean ASSERTS = false;
  
  protected transient K[] key;
  
  protected transient int mask;
  
  protected transient boolean containsNull;
  
  protected transient int n;
  
  protected transient int maxFill;
  
  protected final transient int minN;
  
  protected int size;
  
  protected final float f;
  
  public ObjectOpenHashSet(int expected, float f) {
    if (f <= 0.0F || f > 1.0F)
      throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1"); 
    if (expected < 0)
      throw new IllegalArgumentException("The expected number of elements must be nonnegative"); 
    this.f = f;
    this.minN = this.n = HashCommon.arraySize(expected, f);
    this.mask = this.n - 1;
    this.maxFill = HashCommon.maxFill(this.n, f);
    this.key = (K[])new Object[this.n + 1];
  }
  
  public ObjectOpenHashSet(int expected) {
    this(expected, 0.75F);
  }
  
  public ObjectOpenHashSet() {
    this(16, 0.75F);
  }
  
  public ObjectOpenHashSet(Collection<? extends K> c, float f) {
    this(c.size(), f);
    addAll(c);
  }
  
  public ObjectOpenHashSet(Collection<? extends K> c) {
    this(c, 0.75F);
  }
  
  public ObjectOpenHashSet(ObjectCollection<? extends K> c, float f) {
    this(c.size(), f);
    addAll(c);
  }
  
  public ObjectOpenHashSet(ObjectCollection<? extends K> c) {
    this(c, 0.75F);
  }
  
  public ObjectOpenHashSet(Iterator<? extends K> i, float f) {
    this(16, f);
    while (i.hasNext())
      add(i.next()); 
  }
  
  public ObjectOpenHashSet(Iterator<? extends K> i) {
    this(i, 0.75F);
  }
  
  public ObjectOpenHashSet(K[] a, int offset, int length, float f) {
    this((length < 0) ? 0 : length, f);
    ObjectArrays.ensureOffsetLength(a, offset, length);
    for (int i = 0; i < length; i++)
      add(a[offset + i]); 
  }
  
  public ObjectOpenHashSet(K[] a, int offset, int length) {
    this(a, offset, length, 0.75F);
  }
  
  public ObjectOpenHashSet(K[] a, float f) {
    this(a, 0, a.length, f);
  }
  
  public ObjectOpenHashSet(K[] a) {
    this(a, 0.75F);
  }
  
  private int realSize() {
    return this.containsNull ? (this.size - 1) : this.size;
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
  
  public boolean addAll(Collection<? extends K> c) {
    if (this.f <= 0.5D) {
      ensureCapacity(c.size());
    } else {
      tryCapacity((size() + c.size()));
    } 
    return super.addAll(c);
  }
  
  public boolean add(K k) {
    if (k == null) {
      if (this.containsNull)
        return false; 
      this.containsNull = true;
    } else {
      K[] key = this.key;
      int pos;
      K curr;
      if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) != null) {
        if (curr.equals(k))
          return false; 
        while ((curr = key[pos = pos + 1 & this.mask]) != null) {
          if (curr.equals(k))
            return false; 
        } 
      } 
      key[pos] = k;
    } 
    if (this.size++ >= this.maxFill)
      rehash(HashCommon.arraySize(this.size + 1, this.f)); 
    return true;
  }
  
  public K addOrGet(K k) {
    if (k == null) {
      if (this.containsNull)
        return this.key[this.n]; 
      this.containsNull = true;
    } else {
      K[] key = this.key;
      int pos;
      K curr;
      if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) != null) {
        if (curr.equals(k))
          return curr; 
        while ((curr = key[pos = pos + 1 & this.mask]) != null) {
          if (curr.equals(k))
            return curr; 
        } 
      } 
      key[pos] = k;
    } 
    if (this.size++ >= this.maxFill)
      rehash(HashCommon.arraySize(this.size + 1, this.f)); 
    return k;
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
          return;
        } 
        int slot = HashCommon.mix(curr.hashCode()) & this.mask;
        if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
          break; 
        pos = pos + 1 & this.mask;
      } 
      key[last] = curr;
    } 
  }
  
  private boolean removeEntry(int pos) {
    this.size--;
    shiftKeys(pos);
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return true;
  }
  
  private boolean removeNullEntry() {
    this.containsNull = false;
    this.key[this.n] = null;
    this.size--;
    if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16)
      rehash(this.n / 2); 
    return true;
  }
  
  public boolean remove(Object k) {
    if (k == null) {
      if (this.containsNull)
        return removeNullEntry(); 
      return false;
    } 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return false; 
    if (k.equals(curr))
      return removeEntry(pos); 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return false; 
      if (k.equals(curr))
        return removeEntry(pos); 
    } 
  }
  
  public boolean contains(Object k) {
    if (k == null)
      return this.containsNull; 
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
  
  public K get(Object k) {
    if (k == null)
      return this.key[this.n]; 
    K[] key = this.key;
    K curr;
    int pos;
    if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null)
      return null; 
    if (k.equals(curr))
      return curr; 
    while (true) {
      if ((curr = key[pos = pos + 1 & this.mask]) == null)
        return null; 
      if (k.equals(curr))
        return curr; 
    } 
  }
  
  public void clear() {
    if (this.size == 0)
      return; 
    this.size = 0;
    this.containsNull = false;
    Arrays.fill((Object[])this.key, (Object)null);
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  private class SetIterator implements ObjectIterator<K> {
    int pos = ObjectOpenHashSet.this.n;
    
    int last = -1;
    
    int c = ObjectOpenHashSet.this.size;
    
    boolean mustReturnNull = ObjectOpenHashSet.this.containsNull;
    
    ObjectArrayList<K> wrapped;
    
    public boolean hasNext() {
      return (this.c != 0);
    }
    
    public K next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.c--;
      if (this.mustReturnNull) {
        this.mustReturnNull = false;
        this.last = ObjectOpenHashSet.this.n;
        return ObjectOpenHashSet.this.key[ObjectOpenHashSet.this.n];
      } 
      K[] key = ObjectOpenHashSet.this.key;
      while (true) {
        if (--this.pos < 0) {
          this.last = Integer.MIN_VALUE;
          return this.wrapped.get(-this.pos - 1);
        } 
        if (key[this.pos] != null)
          return key[this.last = this.pos]; 
      } 
    }
    
    private final void shiftKeys(int pos) {
      K[] key = ObjectOpenHashSet.this.key;
      while (true) {
        K curr;
        int last;
        pos = (last = pos) + 1 & ObjectOpenHashSet.this.mask;
        while (true) {
          if ((curr = key[pos]) == null) {
            key[last] = null;
            return;
          } 
          int slot = HashCommon.mix(curr.hashCode()) & ObjectOpenHashSet.this.mask;
          if ((last <= pos) ? (last >= slot || slot > pos) : (last >= slot && slot > pos))
            break; 
          pos = pos + 1 & ObjectOpenHashSet.this.mask;
        } 
        if (pos < last) {
          if (this.wrapped == null)
            this.wrapped = new ObjectArrayList<>(2); 
          this.wrapped.add(key[pos]);
        } 
        key[last] = curr;
      } 
    }
    
    public void remove() {
      if (this.last == -1)
        throw new IllegalStateException(); 
      if (this.last == ObjectOpenHashSet.this.n) {
        ObjectOpenHashSet.this.containsNull = false;
        ObjectOpenHashSet.this.key[ObjectOpenHashSet.this.n] = null;
      } else if (this.pos >= 0) {
        shiftKeys(this.last);
      } else {
        ObjectOpenHashSet.this.remove(this.wrapped.set(-this.pos - 1, null));
        this.last = -1;
        return;
      } 
      ObjectOpenHashSet.this.size--;
      this.last = -1;
    }
    
    private SetIterator() {}
  }
  
  public ObjectIterator<K> iterator() {
    return new SetIterator();
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
    int mask = newN - 1;
    K[] newKey = (K[])new Object[newN + 1];
    int i = this.n;
    for (int j = realSize(); j-- != 0; ) {
      while (key[--i] == null);
      int pos;
      if (newKey[pos = HashCommon.mix(key[i].hashCode()) & mask] != null)
        while (newKey[pos = pos + 1 & mask] != null); 
      newKey[pos] = key[i];
    } 
    this.n = newN;
    this.mask = mask;
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.key = newKey;
  }
  
  public ObjectOpenHashSet<K> clone() {
    ObjectOpenHashSet<K> c;
    try {
      c = (ObjectOpenHashSet<K>)super.clone();
    } catch (CloneNotSupportedException cantHappen) {
      throw new InternalError();
    } 
    c.key = (K[])this.key.clone();
    c.containsNull = this.containsNull;
    return c;
  }
  
  public int hashCode() {
    int h = 0;
    for (int j = realSize(), i = 0; j-- != 0; ) {
      while (this.key[i] == null)
        i++; 
      if (this != this.key[i])
        h += this.key[i].hashCode(); 
      i++;
    } 
    return h;
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    ObjectIterator<K> i = iterator();
    s.defaultWriteObject();
    for (int j = this.size; j-- != 0;)
      s.writeObject(i.next()); 
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.n = HashCommon.arraySize(this.size, this.f);
    this.maxFill = HashCommon.maxFill(this.n, this.f);
    this.mask = this.n - 1;
    K[] key = this.key = (K[])new Object[this.n + 1];
    for (int i = this.size; i-- != 0; ) {
      int pos;
      K k = (K)s.readObject();
      if (k == null) {
        pos = this.n;
        this.containsNull = true;
      } else if (key[pos = HashCommon.mix(k.hashCode()) & this.mask] != null) {
        while (key[pos = pos + 1 & this.mask] != null);
      } 
      key[pos] = k;
    } 
  }
  
  private void checkTable() {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectOpenHashSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */