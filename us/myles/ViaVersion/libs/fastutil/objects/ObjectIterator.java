package us.myles.viaversion.libs.fastutil.objects;

import java.util.Iterator;

public interface ObjectIterator<K> extends Iterator<K> {
  default int skip(int n) {
    if (n < 0)
      throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
    int i = n;
    while (i-- != 0 && hasNext())
      next(); 
    return n - i - 1;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */