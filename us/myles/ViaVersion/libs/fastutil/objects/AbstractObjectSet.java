package us.myles.viaversion.libs.fastutil.objects;

import java.util.Iterator;
import java.util.Set;

public abstract class AbstractObjectSet<K> extends AbstractObjectCollection<K> implements Cloneable, ObjectSet<K> {
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Set))
      return false; 
    Set<?> s = (Set)o;
    if (s.size() != size())
      return false; 
    return containsAll(s);
  }
  
  public int hashCode() {
    int h = 0, n = size();
    ObjectIterator<K> i = iterator();
    while (n-- != 0) {
      K k = i.next();
      h += (k == null) ? 0 : k.hashCode();
    } 
    return h;
  }
  
  public abstract ObjectIterator<K> iterator();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\AbstractObjectSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */