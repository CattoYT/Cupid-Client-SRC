package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import us.myles.viaversion.libs.fastutil.ints.AbstractIntCollection;
import us.myles.viaversion.libs.fastutil.ints.IntCollection;
import us.myles.viaversion.libs.fastutil.ints.IntIterator;

public abstract class AbstractObject2IntMap<K> extends AbstractObject2IntFunction<K> implements Object2IntMap<K>, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  public boolean containsValue(int v) {
    return values().contains(v);
  }
  
  public boolean containsKey(Object k) {
    ObjectIterator<Object2IntMap.Entry<K>> i = object2IntEntrySet().iterator();
    while (i.hasNext()) {
      if (((Object2IntMap.Entry)i.next()).getKey() == k)
        return true; 
    } 
    return false;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public static class BasicEntry<K> implements Object2IntMap.Entry<K> {
    protected K key;
    
    protected int value;
    
    public BasicEntry() {}
    
    public BasicEntry(K key, Integer value) {
      this.key = key;
      this.value = value.intValue();
    }
    
    public BasicEntry(K key, int value) {
      this.key = key;
      this.value = value;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public int getIntValue() {
      return this.value;
    }
    
    public int setValue(int value) {
      throw new UnsupportedOperationException();
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      if (o instanceof Object2IntMap.Entry) {
        Object2IntMap.Entry<K> entry = (Object2IntMap.Entry<K>)o;
        return (Objects.equals(this.key, entry.getKey()) && this.value == entry.getIntValue());
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      Object value = e.getValue();
      if (value == null || !(value instanceof Integer))
        return false; 
      return (Objects.equals(this.key, key) && this.value == ((Integer)value).intValue());
    }
    
    public int hashCode() {
      return ((this.key == null) ? 0 : this.key.hashCode()) ^ this.value;
    }
    
    public String toString() {
      return (new StringBuilder()).append(this.key).append("->").append(this.value).toString();
    }
  }
  
  public static abstract class BasicEntrySet<K> extends AbstractObjectSet<Object2IntMap.Entry<K>> {
    protected final Object2IntMap<K> map;
    
    public BasicEntrySet(Object2IntMap<K> map) {
      this.map = map;
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      if (o instanceof Object2IntMap.Entry) {
        Object2IntMap.Entry<K> entry = (Object2IntMap.Entry<K>)o;
        K k1 = entry.getKey();
        return (this.map.containsKey(k1) && this.map.getInt(k1) == entry.getIntValue());
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object k = e.getKey();
      Object value = e.getValue();
      if (value == null || !(value instanceof Integer))
        return false; 
      return (this.map.containsKey(k) && this.map.getInt(k) == ((Integer)value).intValue());
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      if (o instanceof Object2IntMap.Entry) {
        Object2IntMap.Entry<K> entry = (Object2IntMap.Entry<K>)o;
        return this.map.remove(entry.getKey(), entry.getIntValue());
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object k = e.getKey();
      Object value = e.getValue();
      if (value == null || !(value instanceof Integer))
        return false; 
      int v = ((Integer)value).intValue();
      return this.map.remove(k, v);
    }
    
    public int size() {
      return this.map.size();
    }
  }
  
  public ObjectSet<K> keySet() {
    return new AbstractObjectSet<K>() {
        public boolean contains(Object k) {
          return AbstractObject2IntMap.this.containsKey(k);
        }
        
        public int size() {
          return AbstractObject2IntMap.this.size();
        }
        
        public void clear() {
          AbstractObject2IntMap.this.clear();
        }
        
        public ObjectIterator<K> iterator() {
          return new ObjectIterator<K>() {
              private final ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator(AbstractObject2IntMap.this);
              
              public K next() {
                return ((Object2IntMap.Entry<K>)this.i.next()).getKey();
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
  
  public IntCollection values() {
    return (IntCollection)new AbstractIntCollection() {
        public boolean contains(int k) {
          return AbstractObject2IntMap.this.containsValue(k);
        }
        
        public int size() {
          return AbstractObject2IntMap.this.size();
        }
        
        public void clear() {
          AbstractObject2IntMap.this.clear();
        }
        
        public IntIterator iterator() {
          return new IntIterator() {
              private final ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator(AbstractObject2IntMap.this);
              
              public int nextInt() {
                return ((Object2IntMap.Entry)this.i.next()).getIntValue();
              }
              
              public boolean hasNext() {
                return this.i.hasNext();
              }
            };
        }
      };
  }
  
  public void putAll(Map<? extends K, ? extends Integer> m) {
    if (m instanceof Object2IntMap) {
      ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator((Object2IntMap)m);
      while (i.hasNext()) {
        Object2IntMap.Entry<? extends K> e = i.next();
        put(e.getKey(), e.getIntValue());
      } 
    } else {
      int n = m.size();
      Iterator<? extends Map.Entry<? extends K, ? extends Integer>> i = m.entrySet().iterator();
      while (n-- != 0) {
        Map.Entry<? extends K, ? extends Integer> e = i.next();
        put(e.getKey(), e.getValue());
      } 
    } 
  }
  
  public int hashCode() {
    int h = 0, n = size();
    ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator(this);
    while (n-- != 0)
      h += ((Object2IntMap.Entry)i.next()).hashCode(); 
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
    return object2IntEntrySet().containsAll(m.entrySet());
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator(this);
    int n = size();
    boolean first = true;
    s.append("{");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      Object2IntMap.Entry<K> e = i.next();
      if (this == e.getKey()) {
        s.append("(this map)");
      } else {
        s.append(String.valueOf(e.getKey()));
      } 
      s.append("=>");
      s.append(String.valueOf(e.getIntValue()));
    } 
    s.append("}");
    return s.toString();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\AbstractObject2IntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */