package us.myles.viaversion.libs.fastutil.objects;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface ObjectList<K> extends List<K>, Comparable<List<? extends K>>, ObjectCollection<K> {
  default void setElements(K[] a) {
    setElements(0, a);
  }
  
  default void setElements(int index, K[] a) {
    setElements(index, a, 0, a.length);
  }
  
  default void setElements(int index, K[] a, int offset, int length) {
    if (index < 0)
      throw new IndexOutOfBoundsException("Index (" + index + ") is negative"); 
    if (index > size())
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + size() + ")"); 
    ObjectArrays.ensureOffsetLength(a, offset, length);
    if (index + length > size())
      throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + 
          size() + ")"); 
    ObjectListIterator<K> iter = listIterator(index);
    int i = 0;
    while (i < length) {
      iter.next();
      iter.set(a[offset + i++]);
    } 
  }
  
  default void unstableSort(Comparator<? super K> comparator) {
    K[] elements = (K[])toArray();
    if (comparator == null) {
      ObjectArrays.unstableSort(elements);
    } else {
      ObjectArrays.unstableSort(elements, (Comparator)comparator);
    } 
    setElements(elements);
  }
  
  ObjectListIterator<K> iterator();
  
  ObjectListIterator<K> listIterator();
  
  ObjectListIterator<K> listIterator(int paramInt);
  
  ObjectList<K> subList(int paramInt1, int paramInt2);
  
  void size(int paramInt);
  
  void getElements(int paramInt1, Object[] paramArrayOfObject, int paramInt2, int paramInt3);
  
  void removeElements(int paramInt1, int paramInt2);
  
  void addElements(int paramInt, K[] paramArrayOfK);
  
  void addElements(int paramInt1, K[] paramArrayOfK, int paramInt2, int paramInt3);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */