package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import us.myles.viaversion.libs.fastutil.objects.AbstractObjectSet;
import us.myles.viaversion.libs.fastutil.objects.ObjectIterator;

public abstract class AbstractInt2IntMap extends AbstractInt2IntFunction implements Int2IntMap, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  public boolean containsValue(int v) {
    return values().contains(v);
  }
  
  public boolean containsKey(int k) {
    ObjectIterator<Int2IntMap.Entry> i = int2IntEntrySet().iterator();
    while (i.hasNext()) {
      if (((Int2IntMap.Entry)i.next()).getIntKey() == k)
        return true; 
    } 
    return false;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public static class BasicEntry implements Int2IntMap.Entry {
    protected int key;
    
    protected int value;
    
    public BasicEntry() {}
    
    public BasicEntry(Integer key, Integer value) {
      this.key = key.intValue();
      this.value = value.intValue();
    }
    
    public BasicEntry(int key, int value) {
      this.key = key;
      this.value = value;
    }
    
    public int getIntKey() {
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
      if (o instanceof Int2IntMap.Entry) {
        Int2IntMap.Entry entry = (Int2IntMap.Entry)o;
        return (this.key == entry.getIntKey() && this.value == entry.getIntValue());
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      if (key == null || !(key instanceof Integer))
        return false; 
      Object value = e.getValue();
      if (value == null || !(value instanceof Integer))
        return false; 
      return (this.key == ((Integer)key).intValue() && this.value == ((Integer)value).intValue());
    }
    
    public int hashCode() {
      return this.key ^ this.value;
    }
    
    public String toString() {
      return this.key + "->" + this.value;
    }
  }
  
  public static abstract class BasicEntrySet extends AbstractObjectSet<Int2IntMap.Entry> {
    protected final Int2IntMap map;
    
    public BasicEntrySet(Int2IntMap map) {
      this.map = map;
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      if (o instanceof Int2IntMap.Entry) {
        Int2IntMap.Entry entry = (Int2IntMap.Entry)o;
        int i = entry.getIntKey();
        return (this.map.containsKey(i) && this.map.get(i) == entry.getIntValue());
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      if (key == null || !(key instanceof Integer))
        return false; 
      int k = ((Integer)key).intValue();
      Object value = e.getValue();
      if (value == null || !(value instanceof Integer))
        return false; 
      return (this.map.containsKey(k) && this.map.get(k) == ((Integer)value).intValue());
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      if (o instanceof Int2IntMap.Entry) {
        Int2IntMap.Entry entry = (Int2IntMap.Entry)o;
        return this.map.remove(entry.getIntKey(), entry.getIntValue());
      } 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      if (key == null || !(key instanceof Integer))
        return false; 
      int k = ((Integer)key).intValue();
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
  
  public IntSet keySet() {
    return new AbstractIntSet() {
        public boolean contains(int k) {
          return AbstractInt2IntMap.this.containsKey(k);
        }
        
        public int size() {
          return AbstractInt2IntMap.this.size();
        }
        
        public void clear() {
          AbstractInt2IntMap.this.clear();
        }
        
        public IntIterator iterator() {
          return new IntIterator() {
              private final ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator(AbstractInt2IntMap.this);
              
              public int nextInt() {
                return ((Int2IntMap.Entry)this.i.next()).getIntKey();
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
    return new AbstractIntCollection() {
        public boolean contains(int k) {
          return AbstractInt2IntMap.this.containsValue(k);
        }
        
        public int size() {
          return AbstractInt2IntMap.this.size();
        }
        
        public void clear() {
          AbstractInt2IntMap.this.clear();
        }
        
        public IntIterator iterator() {
          return new IntIterator() {
              private final ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator(AbstractInt2IntMap.this);
              
              public int nextInt() {
                return ((Int2IntMap.Entry)this.i.next()).getIntValue();
              }
              
              public boolean hasNext() {
                return this.i.hasNext();
              }
            };
        }
      };
  }
  
  public void putAll(Map<? extends Integer, ? extends Integer> m) {
    if (m instanceof Int2IntMap) {
      ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator((Int2IntMap)m);
      while (i.hasNext()) {
        Int2IntMap.Entry e = (Int2IntMap.Entry)i.next();
        put(e.getIntKey(), e.getIntValue());
      } 
    } else {
      int n = m.size();
      Iterator<? extends Map.Entry<? extends Integer, ? extends Integer>> i = m.entrySet().iterator();
      while (n-- != 0) {
        Map.Entry<? extends Integer, ? extends Integer> e = i.next();
        put(e.getKey(), e.getValue());
      } 
    } 
  }
  
  public int hashCode() {
    int h = 0, n = size();
    ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator(this);
    while (n-- != 0)
      h += ((Int2IntMap.Entry)i.next()).hashCode(); 
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
    return int2IntEntrySet().containsAll(m.entrySet());
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator(this);
    int n = size();
    boolean first = true;
    s.append("{");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      Int2IntMap.Entry e = (Int2IntMap.Entry)i.next();
      s.append(String.valueOf(e.getIntKey()));
      s.append("=>");
      s.append(String.valueOf(e.getIntValue()));
    } 
    s.append("}");
    return s.toString();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\AbstractInt2IntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */