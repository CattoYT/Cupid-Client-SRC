package us.myles.viaversion.libs.fastutil.objects;

import java.util.Collection;
import java.util.Iterator;

public interface ObjectCollection<K> extends Collection<K>, ObjectIterable<K> {
  ObjectIterator<K> iterator();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */