package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import us.myles.viaversion.libs.fastutil.objects.ObjectCollection;
import us.myles.viaversion.libs.fastutil.objects.ObjectIterable;
import us.myles.viaversion.libs.fastutil.objects.ObjectIterator;
import us.myles.viaversion.libs.fastutil.objects.ObjectSet;
import us.myles.viaversion.libs.fastutil.objects.ObjectSets;

public final class Int2ObjectMaps {
  public static <V> ObjectIterator<Int2ObjectMap.Entry<V>> fastIterator(Int2ObjectMap<V> map) {
    ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
    return (entries instanceof Int2ObjectMap.FastEntrySet) ? (
      (Int2ObjectMap.FastEntrySet)entries).fastIterator() : 
      entries.iterator();
  }
  
  public static <V> void fastForEach(Int2ObjectMap<V> map, Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
    ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
    if (entries instanceof Int2ObjectMap.FastEntrySet) {
      ((Int2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
    } else {
      entries.forEach(consumer);
    } 
  }
  
  public static <V> ObjectIterable<Int2ObjectMap.Entry<V>> fastIterable(Int2ObjectMap<V> map) {
    final ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
    return (entries instanceof Int2ObjectMap.FastEntrySet) ? new ObjectIterable<Int2ObjectMap.Entry<V>>() {
        public ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
          return ((Int2ObjectMap.FastEntrySet<V>)entries).fastIterator();
        }
        
        public void forEach(Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
          ((Int2ObjectMap.FastEntrySet<V>)entries).fastForEach(consumer);
        }
      } : (ObjectIterable<Int2ObjectMap.Entry<V>>)entries;
  }
  
  public static class EmptyMap<V> extends Int2ObjectFunctions.EmptyFunction<V> implements Int2ObjectMap<V>, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public boolean containsValue(Object v) {
      return false;
    }
    
    public void putAll(Map<? extends Integer, ? extends V> m) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
      return (ObjectSet<Int2ObjectMap.Entry<V>>)ObjectSets.EMPTY_SET;
    }
    
    public IntSet keySet() {
      return IntSets.EMPTY_SET;
    }
    
    public ObjectCollection<V> values() {
      return (ObjectCollection<V>)ObjectSets.EMPTY_SET;
    }
    
    public Object clone() {
      return Int2ObjectMaps.EMPTY_MAP;
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
  
  public static <V> Int2ObjectMap<V> emptyMap() {
    return EMPTY_MAP;
  }
  
  public static class Singleton<V> extends Int2ObjectFunctions.Singleton<V> implements Int2ObjectMap<V>, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected transient ObjectSet<Int2ObjectMap.Entry<V>> entries;
    
    protected transient IntSet keys;
    
    protected transient ObjectCollection<V> values;
    
    protected Singleton(int key, V value) {
      super(key, value);
    }
    
    public boolean containsValue(Object v) {
      return Objects.equals(this.value, v);
    }
    
    public void putAll(Map<? extends Integer, ? extends V> m) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
      if (this.entries == null)
        this.entries = ObjectSets.singleton(new AbstractInt2ObjectMap.BasicEntry<>(this.key, this.value)); 
      return this.entries;
    }
    
    @Deprecated
    public ObjectSet<Map.Entry<Integer, V>> entrySet() {
      return (ObjectSet)int2ObjectEntrySet();
    }
    
    public IntSet keySet() {
      if (this.keys == null)
        this.keys = IntSets.singleton(this.key); 
      return this.keys;
    }
    
    public ObjectCollection<V> values() {
      if (this.values == null)
        this.values = (ObjectCollection<V>)ObjectSets.singleton(this.value); 
      return this.values;
    }
    
    public boolean isEmpty() {
      return false;
    }
    
    public int hashCode() {
      return this.key ^ ((this.value == null) ? 0 : this.value.hashCode());
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
  
  public static <V> Int2ObjectMap<V> singleton(int key, V value) {
    return new Singleton<>(key, value);
  }
  
  public static <V> Int2ObjectMap<V> singleton(Integer key, V value) {
    return new Singleton<>(key.intValue(), value);
  }
  
  public static <V> Int2ObjectMap<V> synchronize(Int2ObjectMap<V> m) {
    return (Int2ObjectMap<V>)new SynchronizedMap(m);
  }
  
  public static <V> Int2ObjectMap<V> synchronize(Int2ObjectMap<V> m, Object sync) {
    return (Int2ObjectMap<V>)new SynchronizedMap(m, sync);
  }
  
  public static <V> Int2ObjectMap<V> unmodifiable(Int2ObjectMap<V> m) {
    return (Int2ObjectMap<V>)new UnmodifiableMap(m);
  }
  
  public static class Int2ObjectMaps {}
  
  public static class Int2ObjectMaps {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\Int2ObjectMaps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */