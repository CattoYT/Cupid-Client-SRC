package us.myles.viaversion.libs.fastutil.ints;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface IntList extends List<Integer>, Comparable<List<? extends Integer>>, IntCollection {
  default void setElements(int[] a) {
    setElements(0, a);
  }
  
  default void setElements(int index, int[] a) {
    setElements(index, a, 0, a.length);
  }
  
  default void setElements(int index, int[] a, int offset, int length) {
    if (index < 0)
      throw new IndexOutOfBoundsException("Index (" + index + ") is negative"); 
    if (index > size())
      throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + size() + ")"); 
    IntArrays.ensureOffsetLength(a, offset, length);
    if (index + length > size())
      throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + 
          size() + ")"); 
    IntListIterator iter = listIterator(index);
    int i = 0;
    while (i < length) {
      iter.nextInt();
      iter.set(a[offset + i++]);
    } 
  }
  
  @Deprecated
  default void add(int index, Integer key) {
    add(index, key.intValue());
  }
  
  @Deprecated
  default boolean contains(Object key) {
    return super.contains(key);
  }
  
  @Deprecated
  default Integer get(int index) {
    return Integer.valueOf(getInt(index));
  }
  
  @Deprecated
  default int indexOf(Object o) {
    return indexOf(((Integer)o).intValue());
  }
  
  @Deprecated
  default int lastIndexOf(Object o) {
    return lastIndexOf(((Integer)o).intValue());
  }
  
  @Deprecated
  default boolean add(Integer k) {
    return add(k.intValue());
  }
  
  @Deprecated
  default boolean remove(Object key) {
    return super.remove(key);
  }
  
  @Deprecated
  default Integer remove(int index) {
    return Integer.valueOf(removeInt(index));
  }
  
  @Deprecated
  default Integer set(int index, Integer k) {
    return Integer.valueOf(set(index, k.intValue()));
  }
  
  @Deprecated
  default void sort(Comparator<? super Integer> comparator) {
    sort(IntComparators.asIntComparator(comparator));
  }
  
  default void sort(IntComparator comparator) {
    if (comparator == null) {
      unstableSort(comparator);
    } else {
      int[] elements = toIntArray();
      IntArrays.stableSort(elements, comparator);
      setElements(elements);
    } 
  }
  
  @Deprecated
  default void unstableSort(Comparator<? super Integer> comparator) {
    unstableSort(IntComparators.asIntComparator(comparator));
  }
  
  default void unstableSort(IntComparator comparator) {
    int[] elements = toIntArray();
    if (comparator == null) {
      IntArrays.unstableSort(elements);
    } else {
      IntArrays.unstableSort(elements, comparator);
    } 
    setElements(elements);
  }
  
  IntListIterator iterator();
  
  IntListIterator listIterator();
  
  IntListIterator listIterator(int paramInt);
  
  IntList subList(int paramInt1, int paramInt2);
  
  void size(int paramInt);
  
  void getElements(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3);
  
  void removeElements(int paramInt1, int paramInt2);
  
  void addElements(int paramInt, int[] paramArrayOfint);
  
  void addElements(int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3);
  
  boolean add(int paramInt);
  
  void add(int paramInt1, int paramInt2);
  
  boolean addAll(int paramInt, IntCollection paramIntCollection);
  
  boolean addAll(int paramInt, IntList paramIntList);
  
  boolean addAll(IntList paramIntList);
  
  int set(int paramInt1, int paramInt2);
  
  int getInt(int paramInt);
  
  int indexOf(int paramInt);
  
  int lastIndexOf(int paramInt);
  
  int removeInt(int paramInt);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */