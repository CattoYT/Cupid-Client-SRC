package us.myles.viaversion.libs.fastutil.ints;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import us.myles.viaversion.libs.fastutil.BigArrays;
import us.myles.viaversion.libs.fastutil.longs.LongArrays;
import us.myles.viaversion.libs.fastutil.objects.AbstractObjectList;
import us.myles.viaversion.libs.fastutil.objects.ObjectListIterator;

public class IntArrayFrontCodedList extends AbstractObjectList<int[]> implements Serializable, Cloneable, RandomAccess {
  private static final long serialVersionUID = 1L;
  
  protected final int n;
  
  protected final int ratio;
  
  protected final int[][] array;
  
  protected transient long[] p;
  
  public IntArrayFrontCodedList(Iterator<int[]> arrays, int ratio) {
    if (ratio < 1)
      throw new IllegalArgumentException("Illegal ratio (" + ratio + ")"); 
    int[][] array = IntBigArrays.EMPTY_BIG_ARRAY;
    long[] p = LongArrays.EMPTY_ARRAY;
    int[][] a = new int[2][];
    long curSize = 0L;
    int n = 0, b = 0;
    while (arrays.hasNext()) {
      a[b] = arrays.next();
      int length = (a[b]).length;
      if (n % ratio == 0) {
        p = LongArrays.grow(p, n / ratio + 1);
        p[n / ratio] = curSize;
        array = BigArrays.grow(array, curSize + count(length) + length, curSize);
        curSize += writeInt(array, length, curSize);
        BigArrays.copyToBig(a[b], 0, array, curSize, length);
        curSize += length;
      } else {
        int minLength = (a[1 - b]).length;
        if (length < minLength)
          minLength = length; 
        int common;
        for (common = 0; common < minLength && 
          a[0][common] == a[1][common]; common++);
        length -= common;
        array = BigArrays.grow(array, curSize + count(length) + count(common) + length, curSize);
        curSize += writeInt(array, length, curSize);
        curSize += writeInt(array, common, curSize);
        BigArrays.copyToBig(a[b], common, array, curSize, length);
        curSize += length;
      } 
      b = 1 - b;
      n++;
    } 
    this.n = n;
    this.ratio = ratio;
    this.array = BigArrays.trim(array, curSize);
    this.p = LongArrays.trim(p, (n + ratio - 1) / ratio);
  }
  
  public IntArrayFrontCodedList(Collection<int[]> c, int ratio) {
    this((Iterator)c.iterator(), ratio);
  }
  
  private static int readInt(int[][] a, long pos) {
    return BigArrays.get(a, pos);
  }
  
  private static int count(int length) {
    return 1;
  }
  
  private static int writeInt(int[][] a, int length, long pos) {
    BigArrays.set(a, pos, length);
    return 1;
  }
  
  public int ratio() {
    return this.ratio;
  }
  
  private int length(int index) {
    int[][] array = this.array;
    int delta = index % this.ratio;
    long pos = this.p[index / this.ratio];
    int length = readInt(array, pos);
    if (delta == 0)
      return length; 
    pos += (count(length) + length);
    length = readInt(array, pos);
    int common = readInt(array, pos + count(length));
    for (int i = 0; i < delta - 1; i++) {
      pos += (count(length) + count(common) + length);
      length = readInt(array, pos);
      common = readInt(array, pos + count(length));
    } 
    return length + common;
  }
  
  public int arrayLength(int index) {
    ensureRestrictedIndex(index);
    return length(index);
  }
  
  private int extract(int index, int[] a, int offset, int length) {
    int delta = index % this.ratio;
    long startPos = this.p[index / this.ratio];
    long pos;
    int arrayLength = readInt(this.array, pos = startPos), currLen = 0;
    if (delta == 0) {
      pos = this.p[index / this.ratio] + count(arrayLength);
      BigArrays.copyFromBig(this.array, pos, a, offset, Math.min(length, arrayLength));
      return arrayLength;
    } 
    int common = 0;
    for (int i = 0; i < delta; i++) {
      long prevArrayPos = pos + count(arrayLength) + ((i != 0) ? count(common) : 0L);
      pos = prevArrayPos + arrayLength;
      arrayLength = readInt(this.array, pos);
      common = readInt(this.array, pos + count(arrayLength));
      int actualCommon = Math.min(common, length);
      if (actualCommon <= currLen) {
        currLen = actualCommon;
      } else {
        BigArrays.copyFromBig(this.array, prevArrayPos, a, currLen + offset, actualCommon - currLen);
        currLen = actualCommon;
      } 
    } 
    if (currLen < length)
      BigArrays.copyFromBig(this.array, pos + count(arrayLength) + count(common), a, currLen + offset, 
          Math.min(arrayLength, length - currLen)); 
    return arrayLength + common;
  }
  
  public int[] get(int index) {
    return getArray(index);
  }
  
  public int[] getArray(int index) {
    ensureRestrictedIndex(index);
    int length = length(index);
    int[] a = new int[length];
    extract(index, a, 0, length);
    return a;
  }
  
  public int get(int index, int[] a, int offset, int length) {
    ensureRestrictedIndex(index);
    IntArrays.ensureOffsetLength(a, offset, length);
    int arrayLength = extract(index, a, offset, length);
    if (length >= arrayLength)
      return arrayLength; 
    return length - arrayLength;
  }
  
  public int get(int index, int[] a) {
    return get(index, a, 0, a.length);
  }
  
  public int size() {
    return this.n;
  }
  
  public ObjectListIterator<int[]> listIterator(final int start) {
    ensureIndex(start);
    return new ObjectListIterator<int[]>() {
        int[] s = IntArrays.EMPTY_ARRAY;
        
        int i = 0;
        
        long pos = 0L;
        
        boolean inSync;
        
        public boolean hasNext() {
          return (this.i < IntArrayFrontCodedList.this.n);
        }
        
        public boolean hasPrevious() {
          return (this.i > 0);
        }
        
        public int previousIndex() {
          return this.i - 1;
        }
        
        public int nextIndex() {
          return this.i;
        }
        
        public int[] next() {
          int length;
          if (!hasNext())
            throw new NoSuchElementException(); 
          if (this.i % IntArrayFrontCodedList.this.ratio == 0) {
            this.pos = IntArrayFrontCodedList.this.p[this.i / IntArrayFrontCodedList.this.ratio];
            length = IntArrayFrontCodedList.readInt(IntArrayFrontCodedList.this.array, this.pos);
            this.s = IntArrays.ensureCapacity(this.s, length, 0);
            BigArrays.copyFromBig(IntArrayFrontCodedList.this.array, this.pos + IntArrayFrontCodedList.count(length), this.s, 0, length);
            this.pos += (length + IntArrayFrontCodedList.count(length));
            this.inSync = true;
          } else if (this.inSync) {
            length = IntArrayFrontCodedList.readInt(IntArrayFrontCodedList.this.array, this.pos);
            int common = IntArrayFrontCodedList.readInt(IntArrayFrontCodedList.this.array, this.pos + IntArrayFrontCodedList.count(length));
            this.s = IntArrays.ensureCapacity(this.s, length + common, common);
            BigArrays.copyFromBig(IntArrayFrontCodedList.this.array, this.pos + IntArrayFrontCodedList.count(length) + IntArrayFrontCodedList.count(common), this.s, common, length);
            this.pos += (IntArrayFrontCodedList.count(length) + IntArrayFrontCodedList.count(common) + length);
            length += common;
          } else {
            this.s = IntArrays.ensureCapacity(this.s, length = IntArrayFrontCodedList.this.length(this.i), 0);
            IntArrayFrontCodedList.this.extract(this.i, this.s, 0, length);
          } 
          this.i++;
          return IntArrays.copy(this.s, 0, length);
        }
        
        public int[] previous() {
          if (!hasPrevious())
            throw new NoSuchElementException(); 
          this.inSync = false;
          return IntArrayFrontCodedList.this.getArray(--this.i);
        }
      };
  }
  
  public IntArrayFrontCodedList clone() {
    return this;
  }
  
  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("[");
    for (int i = 0; i < this.n; i++) {
      if (i != 0)
        s.append(", "); 
      s.append(IntArrayList.wrap(getArray(i)).toString());
    } 
    s.append("]");
    return s.toString();
  }
  
  protected long[] rebuildPointerArray() {
    long[] p = new long[(this.n + this.ratio - 1) / this.ratio];
    int[][] a = this.array;
    long pos = 0L;
    for (int i = 0, j = 0, skip = this.ratio - 1; i < this.n; i++) {
      int length = readInt(a, pos);
      int count = count(length);
      if (++skip == this.ratio) {
        skip = 0;
        p[j++] = pos;
        pos += (count + length);
      } else {
        pos += (count + count(readInt(a, pos + count)) + length);
      } 
    } 
    return p;
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    this.p = rebuildPointerArray();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntArrayFrontCodedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */