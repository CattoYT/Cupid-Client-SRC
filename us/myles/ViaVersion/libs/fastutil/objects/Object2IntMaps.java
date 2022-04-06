package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import us.myles.viaversion.libs.fastutil.ints.IntCollection;
import us.myles.viaversion.libs.fastutil.ints.IntSets;

public final class Object2IntMaps {
  public static <K> ObjectIterator<Object2IntMap.Entry<K>> fastIterator(Object2IntMap<K> map) {
    ObjectSet<Object2IntMap.Entry<K>> entries = map.object2IntEntrySet();
    return (entries instanceof Object2IntMap.FastEntrySet) ? (
      (Object2IntMap.FastEntrySet)entries).fastIterator() : 
      entries.iterator();
  }
  
  public static <K> void fastForEach(Object2IntMap<K> map, Consumer<? super Object2IntMap.Entry<K>> consumer) {
    ObjectSet<Object2IntMap.Entry<K>> entries = map.object2IntEntrySet();
    if (entries instanceof Object2IntMap.FastEntrySet) {
      ((Object2IntMap.FastEntrySet)entries).fastForEach(consumer);
    } else {
      entries.forEach(consumer);
    } 
  }
  
  public static <K> ObjectIterable<Object2IntMap.Entry<K>> fastIterable(Object2IntMap<K> map) {
    final ObjectSet<Object2IntMap.Entry<K>> entries = map.object2IntEntrySet();
    return (entries instanceof Object2IntMap.FastEntrySet) ? (ObjectIterable)new ObjectIterable<Object2IntMap.Entry<Object2IntMap.Entry<K>>>() {
        public ObjectIterator<Object2IntMap.Entry<K>> iterator() {
          return ((Object2IntMap.FastEntrySet<K>)entries).fastIterator();
        }
        
        public void forEach(Consumer<? super Object2IntMap.Entry<K>> consumer) {
          ((Object2IntMap.FastEntrySet<K>)entries).fastForEach(consumer);
        }
      } : entries;
  }
  
  public static class EmptyMap<K> extends Object2IntFunctions.EmptyFunction<K> implements Object2IntMap<K>, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public boolean containsValue(int v) {
      return false;
    }
    
    @Deprecated
    public boolean containsValue(Object ov) {
      return false;
    }
    
    public void putAll(Map<? extends K, ? extends Integer> m) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
      return ObjectSets.EMPTY_SET;
    }
    
    public ObjectSet<K> keySet() {
      return ObjectSets.EMPTY_SET;
    }
    
    public IntCollection values() {
      return (IntCollection)IntSets.EMPTY_SET;
    }
    
    public Object clone() {
      return Object2IntMaps.EMPTY_MAP;
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
  
  public static <K> Object2IntMap<K> emptyMap() {
    return EMPTY_MAP;
  }
  
  public static class Singleton<K> extends Object2IntFunctions.Singleton<K> implements Object2IntMap<K>, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected transient ObjectSet<Object2IntMap.Entry<K>> entries;
    
    protected transient ObjectSet<K> keys;
    
    protected transient IntCollection values;
    
    protected Singleton(K key, int value) {
      super(key, value);
    }
    
    public boolean containsValue(int v) {
      return (this.value == v);
    }
    
    @Deprecated
    public boolean containsValue(Object ov) {
      return (((Integer)ov).intValue() == this.value);
    }
    
    public void putAll(Map<? extends K, ? extends Integer> m) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
      if (this.entries == null)
        this.entries = ObjectSets.singleton(new AbstractObject2IntMap.BasicEntry<>(this.key, this.value)); 
      return this.entries;
    }
    
    @Deprecated
    public ObjectSet<Map.Entry<K, Integer>> entrySet() {
      return (ObjectSet)object2IntEntrySet();
    }
    
    public ObjectSet<K> keySet() {
      if (this.keys == null)
        this.keys = ObjectSets.singleton(this.key); 
      return this.keys;
    }
    
    public IntCollection values() {
      if (this.values == null)
        this.values = (IntCollection)IntSets.singleton(this.value); 
      return this.values;
    }
    
    public boolean isEmpty() {
      return false;
    }
    
    public int hashCode() {
      return ((this.key == null) ? 0 : this.key.hashCode()) ^ this.value;
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
  
  public static <K> Object2IntMap<K> singleton(K key, int value) {
    return new Singleton<>(key, value);
  }
  
  public static <K> Object2IntMap<K> singleton(K key, Integer value) {
    return new Singleton<>(key, value.intValue());
  }
  
  public static <K> Object2IntMap<K> synchronize(Object2IntMap<K> m) {
    return (Object2IntMap<K>)new SynchronizedMap(m);
  }
  
  public static <K> Object2IntMap<K> synchronize(Object2IntMap<K> m, Object sync) {
    return (Object2IntMap<K>)new SynchronizedMap(m, sync);
  }
  
  public static <K> Object2IntMap<K> unmodifiable(Object2IntMap<K> m) {
    return (Object2IntMap<K>)new UnmodifiableMap(m);
  }
  
  public static class Object2IntMaps {}
  
  public static class Object2IntMaps {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\Object2IntMaps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */