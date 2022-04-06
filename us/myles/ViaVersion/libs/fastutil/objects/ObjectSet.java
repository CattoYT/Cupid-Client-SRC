package us.myles.viaversion.libs.fastutil.objects;

import java.util.Iterator;
import java.util.Set;

public interface ObjectSet<K> extends ObjectCollection<K>, Set<K> {
  ObjectIterator<K> iterator();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */