package us.myles.viaversion.libs.fastutil.objects;

import us.myles.viaversion.libs.fastutil.BidirectionalIterator;

public interface ObjectBidirectionalIterator<K> extends ObjectIterator<K>, BidirectionalIterator<K> {
  default int back(int n) {
    int i = n;
    while (i-- != 0 && hasPrevious())
      previous(); 
    return n - i - 1;
  }
  
  default int skip(int n) {
    return super.skip(n);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectBidirectionalIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */