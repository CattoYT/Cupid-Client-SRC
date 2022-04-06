package us.myles.viaversion.libs.fastutil.objects;

import java.util.AbstractCollection;
import java.util.Iterator;

public abstract class AbstractObjectCollection<K> extends AbstractCollection<K> implements ObjectCollection<K> {
  public String toString() {
    StringBuilder s = new StringBuilder();
    ObjectIterator<K> i = iterator();
    int n = size();
    boolean first = true;
    s.append("{");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      Object k = i.next();
      if (this == k) {
        s.append("(this collection)");
        continue;
      } 
      s.append(String.valueOf(k));
    } 
    s.append("}");
    return s.toString();
  }
  
  public abstract ObjectIterator<K> iterator();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\AbstractObjectCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */