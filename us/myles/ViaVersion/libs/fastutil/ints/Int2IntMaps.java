package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import us.myles.viaversion.libs.fastutil.objects.ObjectIterable;
import us.myles.viaversion.libs.fastutil.objects.ObjectIterator;
import us.myles.viaversion.libs.fastutil.objects.ObjectSet;
import us.myles.viaversion.libs.fastutil.objects.ObjectSets;

public final class Int2IntMaps {
  public static ObjectIterator<Int2IntMap.Entry> fastIterator(Int2IntMap map) {
    ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
    return (entries instanceof Int2IntMap.FastEntrySet) ? (
      (Int2IntMap.FastEntrySet)entries).fastIterator() : 
      entries.iterator();
  }
  
  public static void fastForEach(Int2IntMap map, Consumer<? super Int2IntMap.Entry> consumer) {
    ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
    if (entries instanceof Int2IntMap.FastEntrySet) {
      ((Int2IntMap.FastEntrySet)entries).fastForEach(consumer);
    } else {
      entries.forEach(consumer);
    } 
  }
  
  public static ObjectIterable<Int2IntMap.Entry> fastIterable(Int2IntMap map) {
    final ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
    return (entries instanceof Int2IntMap.FastEntrySet) ? new ObjectIterable<Int2IntMap.Entry>() {
        public ObjectIterator<Int2IntMap.Entry> iterator() {
          return ((Int2IntMap.FastEntrySet)entries).fastIterator();
        }
        
        public void forEach(Consumer<? super Int2IntMap.Entry> consumer) {
          ((Int2IntMap.FastEntrySet)entries).fastForEach(consumer);
        }
      } : (ObjectIterable<Int2IntMap.Entry>)entries;
  }
  
  public static class EmptyMap extends Int2IntFunctions.EmptyFunction implements Int2IntMap, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public boolean containsValue(int v) {
      return false;
    }
    
    @Deprecated
    public boolean containsValue(Object ov) {
      return false;
    }
    
    public void putAll(Map<? extends Integer, ? extends Integer> m) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
      return (ObjectSet<Int2IntMap.Entry>)ObjectSets.EMPTY_SET;
    }
    
    public IntSet keySet() {
      return IntSets.EMPTY_SET;
    }
    
    public IntCollection values() {
      return IntSets.EMPTY_SET;
    }
    
    public Object clone() {
      return Int2IntMaps.EMPTY_MAP;
    }
    
    public boolean isEmpty() {
      return true;
    }
    
    public int hashCode() {
      return 0;
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Map))
        return false; 
      return ((Map)o).isEmpty();
    }
    
    public String toString() {
      return "{}";
    }
  }
  
  public static final EmptyMap EMPTY_MAP = new EmptyMap();
  
  public static class Singleton extends Int2IntFunctions.Singleton implements Int2IntMap, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected transient ObjectSet<Int2IntMap.Entry> entries;
    
    protected transient IntSet keys;
    
    protected transient IntCollection values;
    
    protected Singleton(int key, int value) {
      super(key, value);
    }
    
    public boolean containsValue(int v) {
      return (this.value == v);
    }
    
    @Deprecated
    public boolean containsValue(Object ov) {
      return (((Integer)ov).intValue() == this.value);
    }
    
    public void putAll(Map<? extends Integer, ? extends Integer> m) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
      if (this.entries == null)
        this.entries = ObjectSets.singleton(new AbstractInt2IntMap.BasicEntry(this.key, this.value)); 
      return this.entries;
    }
    
    @Deprecated
    public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
      return (ObjectSet)int2IntEntrySet();
    }
    
    public IntSet keySet() {
      if (this.keys == null)
        this.keys = IntSets.singleton(this.key); 
      return this.keys;
    }
    
    public IntCollection values() {
      if (this.values == null)
        this.values = IntSets.singleton(this.value); 
      return this.values;
    }
    
    public boolean isEmpty() {
      return false;
    }
    
    public int hashCode() {
      return this.key ^ this.value;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof Map))
        return false; 
      Map<?, ?> m = (Map<?, ?>)o;
      if (m.size() != 1)
        return false; 
      return ((Map.Entry)m.entrySet().iterator().next()).equals(entrySet().iterator().next());
    }
    
    public String toString() {
      return "{" + this.key + "=>" + this.value + "}";
    }
  }
  
  public static Int2IntMap singleton(int key, int value) {
    return new Singleton(key, value);
  }
  
  public static Int2IntMap singleton(Integer key, Integer value) {
    return new Singleton(key.intValue(), value.intValue());
  }
  
  public static Int2IntMap synchronize(Int2IntMap m) {
    return (Int2IntMap)new SynchronizedMap(m);
  }
  
  public static Int2IntMap synchronize(Int2IntMap m, Object sync) {
    return (Int2IntMap)new SynchronizedMap(m, sync);
  }
  
  public static Int2IntMap unmodifiable(Int2IntMap m) {
    return (Int2IntMap)new UnmodifiableMap(m);
  }
  
  public static class Int2IntMaps {}
  
  public static class Int2IntMaps {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\Int2IntMaps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */