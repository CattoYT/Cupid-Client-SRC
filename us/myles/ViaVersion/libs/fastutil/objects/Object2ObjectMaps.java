package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class Object2ObjectMaps {
  public static <K, V> ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator(Object2ObjectMap<K, V> map) {
    ObjectSet<Object2ObjectMap.Entry<K, V>> entries = map.object2ObjectEntrySet();
    return (entries instanceof Object2ObjectMap.FastEntrySet) ? (
      (Object2ObjectMap.FastEntrySet)entries).fastIterator() : 
      entries.iterator();
  }
  
  public static <K, V> void fastForEach(Object2ObjectMap<K, V> map, Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
    ObjectSet<Object2ObjectMap.Entry<K, V>> entries = map.object2ObjectEntrySet();
    if (entries instanceof Object2ObjectMap.FastEntrySet) {
      ((Object2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
    } else {
      entries.forEach(consumer);
    } 
  }
  
  public static <K, V> ObjectIterable<Object2ObjectMap.Entry<K, V>> fastIterable(Object2ObjectMap<K, V> map) {
    final ObjectSet<Object2ObjectMap.Entry<K, V>> entries = map.object2ObjectEntrySet();
    return (entries instanceof Object2ObjectMap.FastEntrySet) ? (ObjectIterable)new ObjectIterable<Object2ObjectMap.Entry<Object2ObjectMap.Entry<K, V>, V>>() {
        public ObjectIterator<Object2ObjectMap.Entry<K, V>> iterator() {
          return ((Object2ObjectMap.FastEntrySet<K, V>)entries).fastIterator();
        }
        
        public void forEach(Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
          ((Object2ObjectMap.FastEntrySet<K, V>)entries).fastForEach(consumer);
        }
      } : entries;
  }
  
  public static class EmptyMap<K, V> extends Object2ObjectFunctions.EmptyFunction<K, V> implements Object2ObjectMap<K, V>, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public boolean containsValue(Object v) {
      return false;
    }
    
    public void putAll(Map<? extends K, ? extends V> m) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
      return ObjectSets.EMPTY_SET;
    }
    
    public ObjectSet<K> keySet() {
      return ObjectSets.EMPTY_SET;
    }
    
    public ObjectCollection<V> values() {
      return ObjectSets.EMPTY_SET;
    }
    
    public Object clone() {
      return Object2ObjectMaps.EMPTY_MAP;
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
  
  public static final EmptyMap EMPTY_MAP = new EmptyMap<>();
  
  public static <K, V> Object2ObjectMap<K, V> emptyMap() {
    return EMPTY_MAP;
  }
  
  public static class Singleton<K, V> extends Object2ObjectFunctions.Singleton<K, V> implements Object2ObjectMap<K, V>, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    protected transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
    
    protected transient ObjectSet<K> keys;
    
    protected transient ObjectCollection<V> values;
    
    protected Singleton(K key, V value) {
      super(key, value);
    }
    
    public boolean containsValue(Object v) {
      return Objects.equals(this.value, v);
    }
    
    public void putAll(Map<? extends K, ? extends V> m) {
      throw new UnsupportedOperationException();
    }
    
    public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
      if (this.entries == null)
        this.entries = ObjectSets.singleton(new AbstractObject2ObjectMap.BasicEntry<>(this.key, this.value)); 
      return this.entries;
    }
    
    public ObjectSet<Map.Entry<K, V>> entrySet() {
      return (ObjectSet)object2ObjectEntrySet();
    }
    
    public ObjectSet<K> keySet() {
      if (this.keys == null)
        this.keys = ObjectSets.singleton(this.key); 
      return this.keys;
    }
    
    public ObjectCollection<V> values() {
      if (this.values == null)
        this.values = ObjectSets.singleton(this.value); 
      return this.values;
    }
    
    public boolean isEmpty() {
      return false;
    }
    
    public int hashCode() {
      return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
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
  
  public static <K, V> Object2ObjectMap<K, V> singleton(K key, V value) {
    return new Singleton<>(key, value);
  }
  
  public static <K, V> Object2ObjectMap<K, V> synchronize(Object2ObjectMap<K, V> m) {
    return (Object2ObjectMap<K, V>)new SynchronizedMap(m);
  }
  
  public static <K, V> Object2ObjectMap<K, V> synchronize(Object2ObjectMap<K, V> m, Object sync) {
    return (Object2ObjectMap<K, V>)new SynchronizedMap(m, sync);
  }
  
  public static <K, V> Object2ObjectMap<K, V> unmodifiable(Object2ObjectMap<K, V> m) {
    return (Object2ObjectMap<K, V>)new UnmodifiableMap(m);
  }
  
  public static class Object2ObjectMaps {}
  
  public static class Object2ObjectMaps {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\Object2ObjectMaps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */