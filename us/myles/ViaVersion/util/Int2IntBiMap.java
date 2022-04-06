package us.myles.ViaVersion.util;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import us.myles.viaversion.libs.fastutil.ints.Int2IntMap;
import us.myles.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import us.myles.viaversion.libs.fastutil.ints.IntCollection;
import us.myles.viaversion.libs.fastutil.ints.IntSet;
import us.myles.viaversion.libs.fastutil.objects.ObjectSet;

public class Int2IntBiMap implements Int2IntMap {
  private final Int2IntMap map;
  
  private final Int2IntBiMap inverse;
  
  public Int2IntBiMap() {
    this.map = (Int2IntMap)new Int2IntOpenHashMap();
    this.inverse = new Int2IntBiMap(this);
  }
  
  private Int2IntBiMap(Int2IntBiMap inverse) {
    this.map = (Int2IntMap)new Int2IntOpenHashMap();
    this.inverse = inverse;
  }
  
  public Int2IntBiMap inverse() {
    return this.inverse;
  }
  
  public int put(int key, int value) {
    if (containsKey(key) && value == get(key))
      return value; 
    Preconditions.checkArgument(!containsValue(value), "value already present: %s", new Object[] { Integer.valueOf(value) });
    this.map.put(key, value);
    this.inverse.map.put(value, key);
    return defaultReturnValue();
  }
  
  public boolean remove(int key, int value) {
    this.map.remove(key, value);
    return this.inverse.map.remove(key, value);
  }
  
  public int get(int key) {
    return this.map.get(key);
  }
  
  public void clear() {
    this.map.clear();
    this.inverse.map.clear();
  }
  
  public int size() {
    return this.map.size();
  }
  
  public boolean isEmpty() {
    return this.map.isEmpty();
  }
  
  @Deprecated
  public void putAll(@NotNull Map<? extends Integer, ? extends Integer> m) {
    throw new UnsupportedOperationException();
  }
  
  public void defaultReturnValue(int rv) {
    this.map.defaultReturnValue(rv);
    this.inverse.map.defaultReturnValue(rv);
  }
  
  public int defaultReturnValue() {
    return this.map.defaultReturnValue();
  }
  
  public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
    return this.map.int2IntEntrySet();
  }
  
  @NotNull
  public IntSet keySet() {
    return this.map.keySet();
  }
  
  @NotNull
  public IntSet values() {
    return this.inverse.map.keySet();
  }
  
  public boolean containsKey(int key) {
    return this.map.containsKey(key);
  }
  
  public boolean containsValue(int value) {
    return this.inverse.map.containsKey(value);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersio\\util\Int2IntBiMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */