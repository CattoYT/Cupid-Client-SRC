package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import us.myles.viaversion.libs.fastutil.objects.AbstractObjectCollection;
import us.myles.viaversion.libs.fastutil.objects.AbstractObjectSet;
import us.myles.viaversion.libs.fastutil.objects.ObjectCollection;
import us.myles.viaversion.libs.fastutil.objects.ObjectIterator;

public abstract class AbstractInt2ObjectMap<V> extends AbstractInt2ObjectFunction<V> implements Int2ObjectMap<V>, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  public boolean containsValue(Object v) {
    return values().contains(v);
  }
  
  public boolean containsKey(int k) {
    ObjectIterator<Int2ObjectMap.Entry<V>> i = int2ObjectEntrySet().iterator();
    while (i.hasNext()) {
      if (((Int2ObjectMap.Entry)i.next()).getIntKey() == k)
        return true; 
    } 
    return false;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public static class BasicEntry<V> implements Int2ObjectMap.Entry<V> {
    protected int key;
    
    protected V value;
    
    public BasicEntry() {}
    
    public BasicEntry(Integer key, V value) {
      this.key = key.intValue();
      this.value = value;
    }
    
    public BasicEntry(int key, V value) {
      this.key = key;
      this.value = value;
    }
    
    public int getIntKey() {
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
      if (o instanceof Int2ObjectMap.Entry) {
        Int2ObjectMap.Entry<V> entry = (Int2ObjectMap.Entry<V>)o;
        return (this.key == entry.getIntKey() && Objects.equals(this.value, entry.getValue()));
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      if (key == null || !(key instanceof Integer))
        return false; 
      Object value = e.getValue();
      return (this.key == ((Integer)key).intValue() && Objects.equals(this.value, value));
    }
    
    public int hashCode() {
      return this.key ^ ((this.value == null) ? 0 : this.value.hashCode());
    }
    
    public String toString() {
      return this.key + "->" + this.value;
    }
  }
  
  public static abstract class BasicEntrySet<V> extends AbstractObjectSet<Int2ObjectMap.Entry<V>> {
    protected final Int2ObjectMap<V> map;
    
    public BasicEntrySet(Int2ObjectMap<V> map) {
      this.map = map;
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      if (o instanceof Int2ObjectMap.Entry) {
        Int2ObjectMap.Entry<V> entry = (Int2ObjectMap.Entry<V>)o;
        int i = entry.getIntKey();
        return (this.map.containsKey(i) && Objects.equals(this.map.get(i), entry.getValue()));
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      if (key == null || !(key instanceof Integer))
        return false; 
      int k = ((Integer)key).intValue();
      Object value = e.getValue();
      return (this.map.containsKey(k) && Objects.equals(this.map.get(k), value));
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      if (o instanceof Int2ObjectMap.Entry) {
        Int2ObjectMap.Entry<V> entry = (Int2ObjectMap.Entry<V>)o;
        return this.map.remove(entry.getIntKey(), entry.getValue());
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      if (key == null || !(key instanceof Integer))
        return false; 
      int k = ((Integer)key).intValue();
      Object v = e.getValue();
      return this.map.remove(k, v);
    }
    
    public int size() {
      return this.map.size();
    }
  }
  
  public IntSet keySet() {
    return new AbstractIntSet() {
        public boolean contains(int k) {
          return AbstractInt2ObjectMap.this.containsKey(k);
        }
        
        public int size() {
          return AbstractInt2ObjectMap.this.size();
        }
        
        public void clear() {
          AbstractInt2ObjectMap.this.clear();
        }
        
        public IntIterator iterator() {
          return new IntIterator() {
              private final ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator(AbstractInt2ObjectMap.this);
              
              public int nextInt() {
                return ((Int2ObjectMap.Entry)this.i.next()).getIntKey();
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
    return (ObjectCollection<V>)new AbstractObjectCollection<V>() {
        public boolean contains(Object k) {
          return AbstractInt2ObjectMap.this.containsValue(k);
        }
        
        public int size() {
          return AbstractInt2ObjectMap.this.size();
        }
        
        public void clear() {
          AbstractInt2ObjectMap.this.clear();
        }
        
        public ObjectIterator<V> iterator() {
          return new ObjectIterator<V>() {
              private final ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator(AbstractInt2ObjectMap.this);
              
              public V next() {
                return ((Int2ObjectMap.Entry<V>)this.i.next()).getValue();
              }
              
              public boolean hasNext() {
                return this.i.hasNext();
              }
            };
        }
      };
  }
  
  public void putAll(Map<? extends Integer, ? extends V> m) {
    if (m instanceof Int2ObjectMap) {
      ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator((Int2ObjectMap)m);
      while (i.hasNext()) {
        Int2ObjectMap.Entry<? extends V> e = (Int2ObjectMap.Entry<? extends V>)i.next();
        put(e.getIntKey(), e.getValue());
      } 
    } else {
      int n = m.size();
      Iterator<? extends Map.Entry<? extends Integer, ? extends V>> i = m.entrySet().iterator();
      while (n-- != 0) {
        Map.Entry<? extends Integer, ? extends V> e = i.next();
        put(e.getKey(), e.getValue());
      } 
    } 
  }
  
  public int hashCode() {
    int h = 0, n = size();
    ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator(this);
    while (n-- != 0)
      h += ((Int2ObjectMap.Entry)i.next()).hashCode(); 
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
    return int2ObjectEntrySet().containsAll(m.entrySet());
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator(this);
    int n = size();
    boolean first = true;
    s.append("{");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      Int2ObjectMap.Entry<V> e = (Int2ObjectMap.Entry<V>)i.next();
      s.append(String.valueOf(e.getIntKey()));
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


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\AbstractInt2ObjectMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */