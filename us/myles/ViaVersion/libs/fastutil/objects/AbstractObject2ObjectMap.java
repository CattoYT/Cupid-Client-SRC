package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractObject2ObjectMap<K, V> extends AbstractObject2ObjectFunction<K, V> implements Object2ObjectMap<K, V>, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  public boolean containsValue(Object v) {
    return values().contains(v);
  }
  
  public boolean containsKey(Object k) {
    ObjectIterator<Object2ObjectMap.Entry<K, V>> i = object2ObjectEntrySet().iterator();
    while (i.hasNext()) {
      if (((Object2ObjectMap.Entry)i.next()).getKey() == k)
        return true; 
    } 
    return false;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public static class BasicEntry<K, V> implements Object2ObjectMap.Entry<K, V> {
    protected K key;
    
    protected V value;
    
    public BasicEntry() {}
    
    public BasicEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.value;
    }
    
    public V setValue(V value) {
      throw new UnsupportedOperationException();
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      if (o instanceof Object2ObjectMap.Entry) {
        Object2ObjectMap.Entry<K, V> entry = (Object2ObjectMap.Entry<K, V>)o;
        return (Objects.equals(this.key, entry.getKey()) && Objects.equals(this.value, entry.getValue()));
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      Object value = e.getValue();
      return (Objects.equals(this.key, key) && Objects.equals(this.value, value));
    }
    
    public int hashCode() {
      return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
    }
    
    public String toString() {
      return (new StringBuilder()).append(this.key).append("->").append(this.value).toString();
    }
  }
  
  public static abstract class BasicEntrySet<K, V> extends AbstractObjectSet<Object2ObjectMap.Entry<K, V>> {
    protected final Object2ObjectMap<K, V> map;
    
    public BasicEntrySet(Object2ObjectMap<K, V> map) {
      this.map = map;
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      if (o instanceof Object2ObjectMap.Entry) {
        Object2ObjectMap.Entry<K, V> entry = (Object2ObjectMap.Entry<K, V>)o;
        K k1 = entry.getKey();
        return (this.map.containsKey(k1) && Objects.equals(this.map.get(k1), entry.getValue()));
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object k = e.getKey();
      Object value = e.getValue();
      return (this.map.containsKey(k) && Objects.equals(this.map.get(k), value));
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      if (o instanceof Object2ObjectMap.Entry) {
        Object2ObjectMap.Entry<K, V> entry = (Object2ObjectMap.Entry<K, V>)o;
        return this.map.remove(entry.getKey(), entry.getValue());
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object k = e.getKey();
      Object v = e.getValue();
      return this.map.remove(k, v);
    }
    
    public int size() {
      return this.map.size();
    }
  }
  
  public ObjectSet<K> keySet() {
    return new AbstractObjectSet<K>() {
        public boolean contains(Object k) {
          return AbstractObject2ObjectMap.this.containsKey(k);
        }
        
        public int size() {
          return AbstractObject2ObjectMap.this.size();
        }
        
        public void clear() {
          AbstractObject2ObjectMap.this.clear();
        }
        
        public ObjectIterator<K> iterator() {
          return new ObjectIterator<K>() {
              private final ObjectIterator<Object2ObjectMap.Entry<K, V>> i = Object2ObjectMaps.fastIterator(AbstractObject2ObjectMap.this);
              
              public K next() {
                return (K)((Object2ObjectMap.Entry)this.i.next()).getKey();
              }
              
              public boolean hasNext() {
                return this.i.hasNext();
              }
              
              public void remove() {
                this.i.remove();
              }
            };
        }
      };
  }
  
  public ObjectCollection<V> values() {
    return new AbstractObjectCollection<V>() {
        public boolean contains(Object k) {
          return AbstractObject2ObjectMap.this.containsValue(k);
        }
        
        public int size() {
          return AbstractObject2ObjectMap.this.size();
        }
        
        public void clear() {
          AbstractObject2ObjectMap.this.clear();
        }
        
        public ObjectIterator<V> iterator() {
          return new ObjectIterator<V>() {
              private final ObjectIterator<Object2ObjectMap.Entry<K, V>> i = Object2ObjectMaps.fastIterator(AbstractObject2ObjectMap.this);
              
              public V next() {
                return (V)((Object2ObjectMap.Entry)this.i.next()).getValue();
              }
              
              public boolean hasNext() {
                return this.i.hasNext();
              }
            };
        }
      };
  }
  
  public void putAll(Map<? extends K, ? extends V> m) {
    if (m instanceof Object2ObjectMap) {
      ObjectIterator<Object2ObjectMap.Entry<K, V>> i = Object2ObjectMaps.fastIterator((Object2ObjectMap)m);
      while (i.hasNext()) {
        Object2ObjectMap.Entry<? extends K, ? extends V> e = i.next();
        put(e.getKey(), e.getValue());
      } 
    } else {
      int n = m.size();
      Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator();
      while (n-- != 0) {
        Map.Entry<? extends K, ? extends V> e = i.next();
        put(e.getKey(), e.getValue());
      } 
    } 
  }
  
  public int hashCode() {
    int h = 0, n = size();
    ObjectIterator<Object2ObjectMap.Entry<K, V>> i = Object2ObjectMaps.fastIterator(this);
    while (n-- != 0)
      h += ((Object2ObjectMap.Entry)i.next()).hashCode(); 
    return h;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Map))
      return false; 
    Map<?, ?> m = (Map<?, ?>)o;
    if (m.size() != size())
      return false; 
    return object2ObjectEntrySet().containsAll(m.entrySet());
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    ObjectIterator<Object2ObjectMap.Entry<K, V>> i = Object2ObjectMaps.fastIterator(this);
    int n = size();
    boolean first = true;
    s.append("{");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      Object2ObjectMap.Entry<K, V> e = i.next();
      if (this == e.getKey()) {
        s.append("(this map)");
      } else {
        s.append(String.valueOf(e.getKey()));
      } 
      s.append("=>");
      if (this == e.getValue()) {
        s.append("(this map)");
        continue;
      } 
      s.append(String.valueOf(e.getValue()));
    } 
    s.append("}");
    return s.toString();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\AbstractObject2ObjectMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */