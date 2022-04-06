package us.myles.viaversion.libs.fastutil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import us.myles.viaversion.libs.fastutil.booleans.BooleanArrays;
import us.myles.viaversion.libs.fastutil.booleans.BooleanBigArrays;
import us.myles.viaversion.libs.fastutil.bytes.ByteArrays;
import us.myles.viaversion.libs.fastutil.bytes.ByteBigArrays;
import us.myles.viaversion.libs.fastutil.chars.CharArrays;
import us.myles.viaversion.libs.fastutil.chars.CharBigArrays;
import us.myles.viaversion.libs.fastutil.doubles.DoubleArrays;
import us.myles.viaversion.libs.fastutil.doubles.DoubleBigArrays;
import us.myles.viaversion.libs.fastutil.floats.FloatArrays;
import us.myles.viaversion.libs.fastutil.floats.FloatBigArrays;
import us.myles.viaversion.libs.fastutil.ints.IntArrays;
import us.myles.viaversion.libs.fastutil.ints.IntBigArrays;
import us.myles.viaversion.libs.fastutil.longs.LongArrays;
import us.myles.viaversion.libs.fastutil.longs.LongBigArrays;
import us.myles.viaversion.libs.fastutil.longs.LongComparator;
import us.myles.viaversion.libs.fastutil.objects.ObjectArrays;
import us.myles.viaversion.libs.fastutil.objects.ObjectBigArrays;
import us.myles.viaversion.libs.fastutil.shorts.ShortArrays;
import us.myles.viaversion.libs.fastutil.shorts.ShortBigArrays;

public class BigArrays {
  public static final int SEGMENT_SHIFT = 27;
  
  public static final int SEGMENT_SIZE = 134217728;
  
  public static final int SEGMENT_MASK = 134217727;
  
  private static final int SMALL = 7;
  
  private static final int MEDIUM = 40;
  
  public static int segment(long index) {
    return (int)(index >>> 27L);
  }
  
  public static int displacement(long index) {
    return (int)(index & 0x7FFFFFFL);
  }
  
  public static long start(int segment) {
    return segment << 27L;
  }
  
  public static long index(int segment, int displacement) {
    return start(segment) + displacement;
  }
  
  public static void ensureFromTo(long bigArrayLength, long from, long to) {
    if (from < 0L)
      throw new ArrayIndexOutOfBoundsException("Start index (" + from + ") is negative"); 
    if (from > to)
      throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")"); 
    if (to > bigArrayLength)
      throw new ArrayIndexOutOfBoundsException("End index (" + to + ") is greater than big-array length (" + bigArrayLength + ")"); 
  }
  
  public static void ensureOffsetLength(long bigArrayLength, long offset, long length) {
    if (offset < 0L)
      throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative"); 
    if (length < 0L)
      throw new IllegalArgumentException("Length (" + length + ") is negative"); 
    if (offset + length > bigArrayLength)
      throw new ArrayIndexOutOfBoundsException("Last index (" + (offset + length) + ") is greater than big-array length (" + bigArrayLength + ")"); 
  }
  
  public static void ensureLength(long bigArrayLength) {
    if (bigArrayLength < 0L)
      throw new IllegalArgumentException("Negative big-array size: " + bigArrayLength); 
    if (bigArrayLength >= 288230376017494016L)
      throw new IllegalArgumentException("Big-array size too big: " + bigArrayLength); 
  }
  
  private static void inPlaceMerge(long from, long mid, long to, LongComparator comp, BigSwapper swapper) {
    // Byte code:
    //   0: lload_0
    //   1: lload_2
    //   2: lcmp
    //   3: ifge -> 13
    //   6: lload_2
    //   7: lload #4
    //   9: lcmp
    //   10: iflt -> 14
    //   13: return
    //   14: lload #4
    //   16: lload_0
    //   17: lsub
    //   18: ldc2_w 2
    //   21: lcmp
    //   22: ifne -> 47
    //   25: aload #6
    //   27: lload_2
    //   28: lload_0
    //   29: invokeinterface compare : (JJ)I
    //   34: ifge -> 46
    //   37: aload #7
    //   39: lload_0
    //   40: lload_2
    //   41: invokeinterface swap : (JJ)V
    //   46: return
    //   47: lload_2
    //   48: lload_0
    //   49: lsub
    //   50: lload #4
    //   52: lload_2
    //   53: lsub
    //   54: lcmp
    //   55: ifle -> 84
    //   58: lload_0
    //   59: lload_2
    //   60: lload_0
    //   61: lsub
    //   62: ldc2_w 2
    //   65: ldiv
    //   66: ladd
    //   67: lstore #8
    //   69: lload_2
    //   70: lload #4
    //   72: lload #8
    //   74: aload #6
    //   76: invokestatic lowerBound : (JJJLus/myles/viaversion/libs/fastutil/longs/LongComparator;)J
    //   79: lstore #10
    //   81: goto -> 107
    //   84: lload_2
    //   85: lload #4
    //   87: lload_2
    //   88: lsub
    //   89: ldc2_w 2
    //   92: ldiv
    //   93: ladd
    //   94: lstore #10
    //   96: lload_0
    //   97: lload_2
    //   98: lload #10
    //   100: aload #6
    //   102: invokestatic upperBound : (JJJLus/myles/viaversion/libs/fastutil/longs/LongComparator;)J
    //   105: lstore #8
    //   107: lload #8
    //   109: lstore #12
    //   111: lload_2
    //   112: lstore #14
    //   114: lload #10
    //   116: lstore #16
    //   118: lload #14
    //   120: lload #12
    //   122: lcmp
    //   123: ifeq -> 254
    //   126: lload #14
    //   128: lload #16
    //   130: lcmp
    //   131: ifeq -> 254
    //   134: lload #12
    //   136: lstore #18
    //   138: lload #14
    //   140: lstore #20
    //   142: lload #18
    //   144: lload #20
    //   146: lconst_1
    //   147: lsub
    //   148: dup2
    //   149: lstore #20
    //   151: lcmp
    //   152: ifge -> 174
    //   155: aload #7
    //   157: lload #18
    //   159: dup2
    //   160: lconst_1
    //   161: ladd
    //   162: lstore #18
    //   164: lload #20
    //   166: invokeinterface swap : (JJ)V
    //   171: goto -> 142
    //   174: lload #14
    //   176: lstore #18
    //   178: lload #16
    //   180: lstore #20
    //   182: lload #18
    //   184: lload #20
    //   186: lconst_1
    //   187: lsub
    //   188: dup2
    //   189: lstore #20
    //   191: lcmp
    //   192: ifge -> 214
    //   195: aload #7
    //   197: lload #18
    //   199: dup2
    //   200: lconst_1
    //   201: ladd
    //   202: lstore #18
    //   204: lload #20
    //   206: invokeinterface swap : (JJ)V
    //   211: goto -> 182
    //   214: lload #12
    //   216: lstore #18
    //   218: lload #16
    //   220: lstore #20
    //   222: lload #18
    //   224: lload #20
    //   226: lconst_1
    //   227: lsub
    //   228: dup2
    //   229: lstore #20
    //   231: lcmp
    //   232: ifge -> 254
    //   235: aload #7
    //   237: lload #18
    //   239: dup2
    //   240: lconst_1
    //   241: ladd
    //   242: lstore #18
    //   244: lload #20
    //   246: invokeinterface swap : (JJ)V
    //   251: goto -> 222
    //   254: lload #8
    //   256: lload #10
    //   258: lload_2
    //   259: lsub
    //   260: ladd
    //   261: lstore_2
    //   262: lload_0
    //   263: lload #8
    //   265: lload_2
    //   266: aload #6
    //   268: aload #7
    //   270: invokestatic inPlaceMerge : (JJJLus/myles/viaversion/libs/fastutil/longs/LongComparator;Lus/myles/viaversion/libs/fastutil/BigSwapper;)V
    //   273: lload_2
    //   274: lload #10
    //   276: lload #4
    //   278: aload #6
    //   280: aload #7
    //   282: invokestatic inPlaceMerge : (JJJLus/myles/viaversion/libs/fastutil/longs/LongComparator;Lus/myles/viaversion/libs/fastutil/BigSwapper;)V
    //   285: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #327	-> 0
    //   #328	-> 14
    //   #329	-> 25
    //   #330	-> 37
    //   #332	-> 46
    //   #336	-> 47
    //   #337	-> 58
    //   #338	-> 69
    //   #340	-> 84
    //   #341	-> 96
    //   #343	-> 107
    //   #344	-> 111
    //   #345	-> 114
    //   #346	-> 118
    //   #347	-> 134
    //   #348	-> 138
    //   #349	-> 142
    //   #350	-> 155
    //   #351	-> 174
    //   #352	-> 178
    //   #353	-> 182
    //   #354	-> 195
    //   #355	-> 214
    //   #356	-> 218
    //   #357	-> 222
    //   #358	-> 235
    //   #360	-> 254
    //   #361	-> 262
    //   #362	-> 273
    //   #363	-> 285
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   69	15	8	firstCut	J
    //   81	3	10	secondCut	J
    //   138	116	18	first1	J
    //   142	112	20	last1	J
    //   0	286	0	from	J
    //   0	286	2	mid	J
    //   0	286	4	to	J
    //   0	286	6	comp	Lus/myles/viaversion/libs/fastutil/longs/LongComparator;
    //   0	286	7	swapper	Lus/myles/viaversion/libs/fastutil/BigSwapper;
    //   107	179	8	firstCut	J
    //   96	190	10	secondCut	J
    //   111	175	12	first2	J
    //   114	172	14	middle2	J
    //   118	168	16	last2	J
  }
  
  private static long lowerBound(long mid, long to, long firstCut, LongComparator comp) {
    long len = to - mid;
    while (len > 0L) {
      long half = len / 2L;
      long middle = mid + half;
      if (comp.compare(middle, firstCut) < 0) {
        mid = middle + 1L;
        len -= half + 1L;
        continue;
      } 
      len = half;
    } 
    return mid;
  }
  
  private static long med3(long a, long b, long c, LongComparator comp) {
    int ab = comp.compare(a, b);
    int ac = comp.compare(a, c);
    int bc = comp.compare(b, c);
    return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
  }
  
  public static void mergeSort(long from, long to, LongComparator comp, BigSwapper swapper) {
    long length = to - from;
    if (length < 7L) {
      long i;
      for (i = from; i < to; i++) {
        long j;
        for (j = i; j > from && comp.compare(j - 1L, j) > 0; j--)
          swapper.swap(j, j - 1L); 
      } 
      return;
    } 
    long mid = from + to >>> 1L;
    mergeSort(from, mid, comp, swapper);
    mergeSort(mid, to, comp, swapper);
    if (comp.compare(mid - 1L, mid) <= 0)
      return; 
    inPlaceMerge(from, mid, to, comp, swapper);
  }
  
  public static void quickSort(long from, long to, LongComparator comp, BigSwapper swapper) {
    long len = to - from;
    if (len < 7L) {
      long i;
      for (i = from; i < to; i++) {
        long j;
        for (j = i; j > from && comp.compare(j - 1L, j) > 0; j--)
          swapper.swap(j, j - 1L); 
      } 
      return;
    } 
    long m = from + len / 2L;
    if (len > 7L) {
      long l = from, n = to - 1L;
      if (len > 40L) {
        long s = len / 8L;
        l = med3(l, l + s, l + 2L * s, comp);
        m = med3(m - s, m, m + s, comp);
        n = med3(n - 2L * s, n - s, n, comp);
      } 
      m = med3(l, m, n, comp);
    } 
    long a = from, b = a, c = to - 1L, d = c;
    int comparison;
    while (b <= c && (comparison = comp.compare(b, m)) <= 0) {
      if (comparison == 0) {
        if (a == m) {
          m = b;
        } else if (b == m) {
          m = a;
        } 
        swapper.swap(a++, b);
      } 
      b++;
    } 
    while (c >= b && (comparison = comp.compare(c, m)) >= 0) {
      if (comparison == 0) {
        if (c == m) {
          m = d;
        } else if (d == m) {
          m = c;
        } 
        swapper.swap(c, d--);
      } 
      c--;
    } 
    if (b > c) {
      long n = from + len;
      long s = Math.min(a - from, b - a);
      vecSwap(swapper, from, b - s, s);
      s = Math.min(d - c, n - d - 1L);
      vecSwap(swapper, b, n - s, s);
      if ((s = b - a) > 1L)
        quickSort(from, from + s, comp, swapper); 
      if ((s = d - c) > 1L)
        quickSort(n - s, n, comp, swapper); 
      return;
    } 
    if (b == m) {
      m = d;
    } else if (c == m) {
      m = c;
    } 
    swapper.swap(b++, c--);
  }
  
  private static long upperBound(long from, long mid, long secondCut, LongComparator comp) {
    long len = mid - from;
    while (len > 0L) {
      long half = len / 2L;
      long middle = from + half;
      if (comp.compare(secondCut, middle) < 0) {
        len = half;
        continue;
      } 
      from = middle + 1L;
      len -= half + 1L;
    } 
    return from;
  }
  
  private static void vecSwap(BigSwapper swapper, long from, long l, long s) {
    for (int i = 0; i < s; i++, from++, l++)
      swapper.swap(from, l); 
  }
  
  public static byte get(byte[][] array, long index) {
    return array[segment(index)][displacement(index)];
  }
  
  public static void set(byte[][] array, long index, byte value) {
    array[segment(index)][displacement(index)] = value;
  }
  
  public static void swap(byte[][] array, long first, long second) {
    byte t = array[segment(first)][displacement(first)];
    array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
    array[segment(second)][displacement(second)] = t;
  }
  
  public static byte[][] reverse(byte[][] a) {
    long length = length(a);
    for (long i = length / 2L; i-- != 0L; swap(a, i, length - i - 1L));
    return a;
  }
  
  public static void add(byte[][] array, long index, byte incr) {
    array[segment(index)][displacement(index)] = (byte)(array[segment(index)][displacement(index)] + incr);
  }
  
  public static void mul(byte[][] array, long index, byte factor) {
    array[segment(index)][displacement(index)] = (byte)(array[segment(index)][displacement(index)] * factor);
  }
  
  public static void incr(byte[][] array, long index) {
    array[segment(index)][displacement(index)] = (byte)(array[segment(index)][displacement(index)] + 1);
  }
  
  public static void decr(byte[][] array, long index) {
    array[segment(index)][displacement(index)] = (byte)(array[segment(index)][displacement(index)] - 1);
  }
  
  public static long length(byte[][] array) {
    int length = array.length;
    return (length == 0) ? 0L : (start(length - 1) + (array[length - 1]).length);
  }
  
  public static void copy(byte[][] srcArray, long srcPos, byte[][] destArray, long destPos, long length) {
    if (destPos <= srcPos) {
      int srcSegment = segment(srcPos);
      int destSegment = segment(destPos);
      int srcDispl = displacement(srcPos);
      int destDispl = displacement(destPos);
      while (length > 0L) {
        int l = (int)Math.min(length, Math.min((srcArray[srcSegment]).length - srcDispl, (destArray[destSegment]).length - destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
        if ((srcDispl += l) == 134217728) {
          srcDispl = 0;
          srcSegment++;
        } 
        if ((destDispl += l) == 134217728) {
          destDispl = 0;
          destSegment++;
        } 
        length -= l;
      } 
    } else {
      int srcSegment = segment(srcPos + length);
      int destSegment = segment(destPos + length);
      int srcDispl = displacement(srcPos + length);
      int destDispl = displacement(destPos + length);
      while (length > 0L) {
        if (srcDispl == 0) {
          srcDispl = 134217728;
          srcSegment--;
        } 
        if (destDispl == 0) {
          destDispl = 134217728;
          destSegment--;
        } 
        int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
        srcDispl -= l;
        destDispl -= l;
        length -= l;
      } 
    } 
  }
  
  public static void copyFromBig(byte[][] srcArray, long srcPos, byte[] destArray, int destPos, int length) {
    int srcSegment = segment(srcPos);
    int srcDispl = displacement(srcPos);
    while (length > 0) {
      int l = Math.min((srcArray[srcSegment]).length - srcDispl, length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
      if ((srcDispl += l) == 134217728) {
        srcDispl = 0;
        srcSegment++;
      } 
      destPos += l;
      length -= l;
    } 
  }
  
  public static void copyToBig(byte[] srcArray, int srcPos, byte[][] destArray, long destPos, long length) {
    int destSegment = segment(destPos);
    int destDispl = displacement(destPos);
    while (length > 0L) {
      int l = (int)Math.min(((destArray[destSegment]).length - destDispl), length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
      if ((destDispl += l) == 134217728) {
        destDispl = 0;
        destSegment++;
      } 
      srcPos += l;
      length -= l;
    } 
  }
  
  public static byte[][] wrap(byte[] array) {
    if (array.length == 0)
      return ByteBigArrays.EMPTY_BIG_ARRAY; 
    if (array.length <= 134217728)
      return new byte[][] { array }; 
    byte[][] bigArray = ByteBigArrays.newBigArray(array.length);
    for (int i = 0; i < bigArray.length; ) {
      System.arraycopy(array, (int)start(i), bigArray[i], 0, (bigArray[i]).length);
      i++;
    } 
    return bigArray;
  }
  
  public static byte[][] ensureCapacity(byte[][] array, long length) {
    return ensureCapacity(array, length, length(array));
  }
  
  public static byte[][] forceCapacity(byte[][] array, long length, long preserve) {
    ensureLength(length);
    int valid = array.length - ((array.length == 0 || (array.length > 0 && (array[array.length - 1]).length == 134217728)) ? 0 : 1);
    int baseLength = (int)(length + 134217727L >>> 27L);
    byte[][] base = Arrays.<byte[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0) {
      for (int i = valid; i < baseLength - 1; ) {
        base[i] = new byte[134217728];
        i++;
      } 
      base[baseLength - 1] = new byte[residual];
    } else {
      for (int i = valid; i < baseLength; ) {
        base[i] = new byte[134217728];
        i++;
      } 
    } 
    if (preserve - valid * 134217728L > 0L)
      copy(array, valid * 134217728L, base, valid * 134217728L, preserve - valid * 134217728L); 
    return base;
  }
  
  public static byte[][] ensureCapacity(byte[][] array, long length, long preserve) {
    return (length > length(array)) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static byte[][] grow(byte[][] array, long length) {
    long oldLength = length(array);
    return (length > oldLength) ? grow(array, length, oldLength) : array;
  }
  
  public static byte[][] grow(byte[][] array, long length, long preserve) {
    long oldLength = length(array);
    return (length > oldLength) ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1L), length), preserve) : array;
  }
  
  public static byte[][] trim(byte[][] array, long length) {
    ensureLength(length);
    long oldLength = length(array);
    if (length >= oldLength)
      return array; 
    int baseLength = (int)(length + 134217727L >>> 27L);
    byte[][] base = Arrays.<byte[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0)
      base[baseLength - 1] = ByteArrays.trim(base[baseLength - 1], residual); 
    return base;
  }
  
  public static byte[][] setLength(byte[][] array, long length) {
    long oldLength = length(array);
    if (length == oldLength)
      return array; 
    if (length < oldLength)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static byte[][] copy(byte[][] array, long offset, long length) {
    ensureOffsetLength(array, offset, length);
    byte[][] a = ByteBigArrays.newBigArray(length);
    copy(array, offset, a, 0L, length);
    return a;
  }
  
  public static byte[][] copy(byte[][] array) {
    byte[][] base = (byte[][])array.clone();
    for (int i = base.length; i-- != 0; base[i] = (byte[])array[i].clone());
    return base;
  }
  
  public static void fill(byte[][] array, byte value) {
    for (int i = array.length; i-- != 0; Arrays.fill(array[i], value));
  }
  
  public static void fill(byte[][] array, long from, long to, byte value) {
    long length = length(array);
    ensureFromTo(length, from, to);
    if (length == 0L)
      return; 
    int fromSegment = segment(from);
    int toSegment = segment(to);
    int fromDispl = displacement(from);
    int toDispl = displacement(to);
    if (fromSegment == toSegment) {
      Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
      return;
    } 
    if (toDispl != 0)
      Arrays.fill(array[toSegment], 0, toDispl, value); 
    for (; --toSegment > fromSegment; Arrays.fill(array[toSegment], value));
    Arrays.fill(array[fromSegment], fromDispl, 134217728, value);
  }
  
  public static boolean equals(byte[][] a1, byte[][] a2) {
    if (length(a1) != length(a2))
      return false; 
    int i = a1.length;
    while (i-- != 0) {
      byte[] t = a1[i];
      byte[] u = a2[i];
      int j = t.length;
      while (j-- != 0) {
        if (t[j] != u[j])
          return false; 
      } 
    } 
    return true;
  }
  
  public static String toString(byte[][] a) {
    if (a == null)
      return "null"; 
    long last = length(a) - 1L;
    if (last == -1L)
      return "[]"; 
    StringBuilder b = new StringBuilder();
    b.append('[');
    long i;
    for (i = 0L;; i++) {
      b.append(String.valueOf(get(a, i)));
      if (i == last)
        return b.append(']').toString(); 
      b.append(", ");
    } 
  }
  
  public static void ensureFromTo(byte[][] a, long from, long to) {
    ensureFromTo(length(a), from, to);
  }
  
  public static void ensureOffsetLength(byte[][] a, long offset, long length) {
    ensureOffsetLength(length(a), offset, length);
  }
  
  public static void ensureSameLength(byte[][] a, byte[][] b) {
    if (length(a) != length(b))
      throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b)); 
  }
  
  public static byte[][] shuffle(byte[][] a, long from, long to, Random random) {
    for (long i = to - from; i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      byte t = get(a, from + i);
      set(a, from + i, get(a, from + p));
      set(a, from + p, t);
    } 
    return a;
  }
  
  public static byte[][] shuffle(byte[][] a, Random random) {
    for (long i = length(a); i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      byte t = get(a, i);
      set(a, i, get(a, p));
      set(a, p, t);
    } 
    return a;
  }
  
  public static int get(int[][] array, long index) {
    return array[segment(index)][displacement(index)];
  }
  
  public static void set(int[][] array, long index, int value) {
    array[segment(index)][displacement(index)] = value;
  }
  
  public static void swap(int[][] array, long first, long second) {
    int t = array[segment(first)][displacement(first)];
    array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
    array[segment(second)][displacement(second)] = t;
  }
  
  public static int[][] reverse(int[][] a) {
    long length = length(a);
    for (long i = length / 2L; i-- != 0L; swap(a, i, length - i - 1L));
    return a;
  }
  
  public static void add(int[][] array, long index, int incr) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] + incr;
  }
  
  public static void mul(int[][] array, long index, int factor) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] * factor;
  }
  
  public static void incr(int[][] array, long index) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] + 1;
  }
  
  public static void decr(int[][] array, long index) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] - 1;
  }
  
  public static long length(int[][] array) {
    int length = array.length;
    return (length == 0) ? 0L : (start(length - 1) + (array[length - 1]).length);
  }
  
  public static void copy(int[][] srcArray, long srcPos, int[][] destArray, long destPos, long length) {
    if (destPos <= srcPos) {
      int srcSegment = segment(srcPos);
      int destSegment = segment(destPos);
      int srcDispl = displacement(srcPos);
      int destDispl = displacement(destPos);
      while (length > 0L) {
        int l = (int)Math.min(length, Math.min((srcArray[srcSegment]).length - srcDispl, (destArray[destSegment]).length - destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
        if ((srcDispl += l) == 134217728) {
          srcDispl = 0;
          srcSegment++;
        } 
        if ((destDispl += l) == 134217728) {
          destDispl = 0;
          destSegment++;
        } 
        length -= l;
      } 
    } else {
      int srcSegment = segment(srcPos + length);
      int destSegment = segment(destPos + length);
      int srcDispl = displacement(srcPos + length);
      int destDispl = displacement(destPos + length);
      while (length > 0L) {
        if (srcDispl == 0) {
          srcDispl = 134217728;
          srcSegment--;
        } 
        if (destDispl == 0) {
          destDispl = 134217728;
          destSegment--;
        } 
        int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
        srcDispl -= l;
        destDispl -= l;
        length -= l;
      } 
    } 
  }
  
  public static void copyFromBig(int[][] srcArray, long srcPos, int[] destArray, int destPos, int length) {
    int srcSegment = segment(srcPos);
    int srcDispl = displacement(srcPos);
    while (length > 0) {
      int l = Math.min((srcArray[srcSegment]).length - srcDispl, length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
      if ((srcDispl += l) == 134217728) {
        srcDispl = 0;
        srcSegment++;
      } 
      destPos += l;
      length -= l;
    } 
  }
  
  public static void copyToBig(int[] srcArray, int srcPos, int[][] destArray, long destPos, long length) {
    int destSegment = segment(destPos);
    int destDispl = displacement(destPos);
    while (length > 0L) {
      int l = (int)Math.min(((destArray[destSegment]).length - destDispl), length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
      if ((destDispl += l) == 134217728) {
        destDispl = 0;
        destSegment++;
      } 
      srcPos += l;
      length -= l;
    } 
  }
  
  public static int[][] wrap(int[] array) {
    if (array.length == 0)
      return IntBigArrays.EMPTY_BIG_ARRAY; 
    if (array.length <= 134217728)
      return new int[][] { array }; 
    int[][] bigArray = IntBigArrays.newBigArray(array.length);
    for (int i = 0; i < bigArray.length; ) {
      System.arraycopy(array, (int)start(i), bigArray[i], 0, (bigArray[i]).length);
      i++;
    } 
    return bigArray;
  }
  
  public static int[][] ensureCapacity(int[][] array, long length) {
    return ensureCapacity(array, length, length(array));
  }
  
  public static int[][] forceCapacity(int[][] array, long length, long preserve) {
    ensureLength(length);
    int valid = array.length - ((array.length == 0 || (array.length > 0 && (array[array.length - 1]).length == 134217728)) ? 0 : 1);
    int baseLength = (int)(length + 134217727L >>> 27L);
    int[][] base = Arrays.<int[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0) {
      for (int i = valid; i < baseLength - 1; ) {
        base[i] = new int[134217728];
        i++;
      } 
      base[baseLength - 1] = new int[residual];
    } else {
      for (int i = valid; i < baseLength; ) {
        base[i] = new int[134217728];
        i++;
      } 
    } 
    if (preserve - valid * 134217728L > 0L)
      copy(array, valid * 134217728L, base, valid * 134217728L, preserve - valid * 134217728L); 
    return base;
  }
  
  public static int[][] ensureCapacity(int[][] array, long length, long preserve) {
    return (length > length(array)) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static int[][] grow(int[][] array, long length) {
    long oldLength = length(array);
    return (length > oldLength) ? grow(array, length, oldLength) : array;
  }
  
  public static int[][] grow(int[][] array, long length, long preserve) {
    long oldLength = length(array);
    return (length > oldLength) ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1L), length), preserve) : array;
  }
  
  public static int[][] trim(int[][] array, long length) {
    ensureLength(length);
    long oldLength = length(array);
    if (length >= oldLength)
      return array; 
    int baseLength = (int)(length + 134217727L >>> 27L);
    int[][] base = Arrays.<int[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0)
      base[baseLength - 1] = IntArrays.trim(base[baseLength - 1], residual); 
    return base;
  }
  
  public static int[][] setLength(int[][] array, long length) {
    long oldLength = length(array);
    if (length == oldLength)
      return array; 
    if (length < oldLength)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static int[][] copy(int[][] array, long offset, long length) {
    ensureOffsetLength(array, offset, length);
    int[][] a = IntBigArrays.newBigArray(length);
    copy(array, offset, a, 0L, length);
    return a;
  }
  
  public static int[][] copy(int[][] array) {
    int[][] base = (int[][])array.clone();
    for (int i = base.length; i-- != 0; base[i] = (int[])array[i].clone());
    return base;
  }
  
  public static void fill(int[][] array, int value) {
    for (int i = array.length; i-- != 0; Arrays.fill(array[i], value));
  }
  
  public static void fill(int[][] array, long from, long to, int value) {
    long length = length(array);
    ensureFromTo(length, from, to);
    if (length == 0L)
      return; 
    int fromSegment = segment(from);
    int toSegment = segment(to);
    int fromDispl = displacement(from);
    int toDispl = displacement(to);
    if (fromSegment == toSegment) {
      Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
      return;
    } 
    if (toDispl != 0)
      Arrays.fill(array[toSegment], 0, toDispl, value); 
    for (; --toSegment > fromSegment; Arrays.fill(array[toSegment], value));
    Arrays.fill(array[fromSegment], fromDispl, 134217728, value);
  }
  
  public static boolean equals(int[][] a1, int[][] a2) {
    if (length(a1) != length(a2))
      return false; 
    int i = a1.length;
    while (i-- != 0) {
      int[] t = a1[i];
      int[] u = a2[i];
      int j = t.length;
      while (j-- != 0) {
        if (t[j] != u[j])
          return false; 
      } 
    } 
    return true;
  }
  
  public static String toString(int[][] a) {
    if (a == null)
      return "null"; 
    long last = length(a) - 1L;
    if (last == -1L)
      return "[]"; 
    StringBuilder b = new StringBuilder();
    b.append('[');
    long i;
    for (i = 0L;; i++) {
      b.append(String.valueOf(get(a, i)));
      if (i == last)
        return b.append(']').toString(); 
      b.append(", ");
    } 
  }
  
  public static void ensureFromTo(int[][] a, long from, long to) {
    ensureFromTo(length(a), from, to);
  }
  
  public static void ensureOffsetLength(int[][] a, long offset, long length) {
    ensureOffsetLength(length(a), offset, length);
  }
  
  public static void ensureSameLength(int[][] a, int[][] b) {
    if (length(a) != length(b))
      throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b)); 
  }
  
  public static int[][] shuffle(int[][] a, long from, long to, Random random) {
    for (long i = to - from; i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      int t = get(a, from + i);
      set(a, from + i, get(a, from + p));
      set(a, from + p, t);
    } 
    return a;
  }
  
  public static int[][] shuffle(int[][] a, Random random) {
    for (long i = length(a); i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      int t = get(a, i);
      set(a, i, get(a, p));
      set(a, p, t);
    } 
    return a;
  }
  
  public static long get(long[][] array, long index) {
    return array[segment(index)][displacement(index)];
  }
  
  public static void set(long[][] array, long index, long value) {
    array[segment(index)][displacement(index)] = value;
  }
  
  public static void swap(long[][] array, long first, long second) {
    long t = array[segment(first)][displacement(first)];
    array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
    array[segment(second)][displacement(second)] = t;
  }
  
  public static long[][] reverse(long[][] a) {
    long length = length(a);
    for (long i = length / 2L; i-- != 0L; swap(a, i, length - i - 1L));
    return a;
  }
  
  public static void add(long[][] array, long index, long incr) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] + incr;
  }
  
  public static void mul(long[][] array, long index, long factor) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] * factor;
  }
  
  public static void incr(long[][] array, long index) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] + 1L;
  }
  
  public static void decr(long[][] array, long index) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] - 1L;
  }
  
  public static long length(long[][] array) {
    int length = array.length;
    return (length == 0) ? 0L : (start(length - 1) + (array[length - 1]).length);
  }
  
  public static void copy(long[][] srcArray, long srcPos, long[][] destArray, long destPos, long length) {
    if (destPos <= srcPos) {
      int srcSegment = segment(srcPos);
      int destSegment = segment(destPos);
      int srcDispl = displacement(srcPos);
      int destDispl = displacement(destPos);
      while (length > 0L) {
        int l = (int)Math.min(length, Math.min((srcArray[srcSegment]).length - srcDispl, (destArray[destSegment]).length - destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
        if ((srcDispl += l) == 134217728) {
          srcDispl = 0;
          srcSegment++;
        } 
        if ((destDispl += l) == 134217728) {
          destDispl = 0;
          destSegment++;
        } 
        length -= l;
      } 
    } else {
      int srcSegment = segment(srcPos + length);
      int destSegment = segment(destPos + length);
      int srcDispl = displacement(srcPos + length);
      int destDispl = displacement(destPos + length);
      while (length > 0L) {
        if (srcDispl == 0) {
          srcDispl = 134217728;
          srcSegment--;
        } 
        if (destDispl == 0) {
          destDispl = 134217728;
          destSegment--;
        } 
        int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
        srcDispl -= l;
        destDispl -= l;
        length -= l;
      } 
    } 
  }
  
  public static void copyFromBig(long[][] srcArray, long srcPos, long[] destArray, int destPos, int length) {
    int srcSegment = segment(srcPos);
    int srcDispl = displacement(srcPos);
    while (length > 0) {
      int l = Math.min((srcArray[srcSegment]).length - srcDispl, length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
      if ((srcDispl += l) == 134217728) {
        srcDispl = 0;
        srcSegment++;
      } 
      destPos += l;
      length -= l;
    } 
  }
  
  public static void copyToBig(long[] srcArray, int srcPos, long[][] destArray, long destPos, long length) {
    int destSegment = segment(destPos);
    int destDispl = displacement(destPos);
    while (length > 0L) {
      int l = (int)Math.min(((destArray[destSegment]).length - destDispl), length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
      if ((destDispl += l) == 134217728) {
        destDispl = 0;
        destSegment++;
      } 
      srcPos += l;
      length -= l;
    } 
  }
  
  public static long[][] wrap(long[] array) {
    if (array.length == 0)
      return LongBigArrays.EMPTY_BIG_ARRAY; 
    if (array.length <= 134217728)
      return new long[][] { array }; 
    long[][] bigArray = LongBigArrays.newBigArray(array.length);
    for (int i = 0; i < bigArray.length; ) {
      System.arraycopy(array, (int)start(i), bigArray[i], 0, (bigArray[i]).length);
      i++;
    } 
    return bigArray;
  }
  
  public static long[][] ensureCapacity(long[][] array, long length) {
    return ensureCapacity(array, length, length(array));
  }
  
  public static long[][] forceCapacity(long[][] array, long length, long preserve) {
    ensureLength(length);
    int valid = array.length - ((array.length == 0 || (array.length > 0 && (array[array.length - 1]).length == 134217728)) ? 0 : 1);
    int baseLength = (int)(length + 134217727L >>> 27L);
    long[][] base = Arrays.<long[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0) {
      for (int i = valid; i < baseLength - 1; ) {
        base[i] = new long[134217728];
        i++;
      } 
      base[baseLength - 1] = new long[residual];
    } else {
      for (int i = valid; i < baseLength; ) {
        base[i] = new long[134217728];
        i++;
      } 
    } 
    if (preserve - valid * 134217728L > 0L)
      copy(array, valid * 134217728L, base, valid * 134217728L, preserve - valid * 134217728L); 
    return base;
  }
  
  public static long[][] ensureCapacity(long[][] array, long length, long preserve) {
    return (length > length(array)) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static long[][] grow(long[][] array, long length) {
    long oldLength = length(array);
    return (length > oldLength) ? grow(array, length, oldLength) : array;
  }
  
  public static long[][] grow(long[][] array, long length, long preserve) {
    long oldLength = length(array);
    return (length > oldLength) ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1L), length), preserve) : array;
  }
  
  public static long[][] trim(long[][] array, long length) {
    ensureLength(length);
    long oldLength = length(array);
    if (length >= oldLength)
      return array; 
    int baseLength = (int)(length + 134217727L >>> 27L);
    long[][] base = Arrays.<long[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0)
      base[baseLength - 1] = LongArrays.trim(base[baseLength - 1], residual); 
    return base;
  }
  
  public static long[][] setLength(long[][] array, long length) {
    long oldLength = length(array);
    if (length == oldLength)
      return array; 
    if (length < oldLength)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static long[][] copy(long[][] array, long offset, long length) {
    ensureOffsetLength(array, offset, length);
    long[][] a = LongBigArrays.newBigArray(length);
    copy(array, offset, a, 0L, length);
    return a;
  }
  
  public static long[][] copy(long[][] array) {
    long[][] base = (long[][])array.clone();
    for (int i = base.length; i-- != 0; base[i] = (long[])array[i].clone());
    return base;
  }
  
  public static void fill(long[][] array, long value) {
    for (int i = array.length; i-- != 0; Arrays.fill(array[i], value));
  }
  
  public static void fill(long[][] array, long from, long to, long value) {
    long length = length(array);
    ensureFromTo(length, from, to);
    if (length == 0L)
      return; 
    int fromSegment = segment(from);
    int toSegment = segment(to);
    int fromDispl = displacement(from);
    int toDispl = displacement(to);
    if (fromSegment == toSegment) {
      Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
      return;
    } 
    if (toDispl != 0)
      Arrays.fill(array[toSegment], 0, toDispl, value); 
    for (; --toSegment > fromSegment; Arrays.fill(array[toSegment], value));
    Arrays.fill(array[fromSegment], fromDispl, 134217728, value);
  }
  
  public static boolean equals(long[][] a1, long[][] a2) {
    if (length(a1) != length(a2))
      return false; 
    int i = a1.length;
    while (i-- != 0) {
      long[] t = a1[i];
      long[] u = a2[i];
      int j = t.length;
      while (j-- != 0) {
        if (t[j] != u[j])
          return false; 
      } 
    } 
    return true;
  }
  
  public static String toString(long[][] a) {
    if (a == null)
      return "null"; 
    long last = length(a) - 1L;
    if (last == -1L)
      return "[]"; 
    StringBuilder b = new StringBuilder();
    b.append('[');
    long i;
    for (i = 0L;; i++) {
      b.append(String.valueOf(get(a, i)));
      if (i == last)
        return b.append(']').toString(); 
      b.append(", ");
    } 
  }
  
  public static void ensureFromTo(long[][] a, long from, long to) {
    ensureFromTo(length(a), from, to);
  }
  
  public static void ensureOffsetLength(long[][] a, long offset, long length) {
    ensureOffsetLength(length(a), offset, length);
  }
  
  public static void ensureSameLength(long[][] a, long[][] b) {
    if (length(a) != length(b))
      throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b)); 
  }
  
  public static long[][] shuffle(long[][] a, long from, long to, Random random) {
    for (long i = to - from; i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      long t = get(a, from + i);
      set(a, from + i, get(a, from + p));
      set(a, from + p, t);
    } 
    return a;
  }
  
  public static long[][] shuffle(long[][] a, Random random) {
    for (long i = length(a); i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      long t = get(a, i);
      set(a, i, get(a, p));
      set(a, p, t);
    } 
    return a;
  }
  
  public static double get(double[][] array, long index) {
    return array[segment(index)][displacement(index)];
  }
  
  public static void set(double[][] array, long index, double value) {
    array[segment(index)][displacement(index)] = value;
  }
  
  public static void swap(double[][] array, long first, long second) {
    double t = array[segment(first)][displacement(first)];
    array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
    array[segment(second)][displacement(second)] = t;
  }
  
  public static double[][] reverse(double[][] a) {
    long length = length(a);
    for (long i = length / 2L; i-- != 0L; swap(a, i, length - i - 1L));
    return a;
  }
  
  public static void add(double[][] array, long index, double incr) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] + incr;
  }
  
  public static void mul(double[][] array, long index, double factor) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] * factor;
  }
  
  public static void incr(double[][] array, long index) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] + 1.0D;
  }
  
  public static void decr(double[][] array, long index) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] - 1.0D;
  }
  
  public static long length(double[][] array) {
    int length = array.length;
    return (length == 0) ? 0L : (start(length - 1) + (array[length - 1]).length);
  }
  
  public static void copy(double[][] srcArray, long srcPos, double[][] destArray, long destPos, long length) {
    if (destPos <= srcPos) {
      int srcSegment = segment(srcPos);
      int destSegment = segment(destPos);
      int srcDispl = displacement(srcPos);
      int destDispl = displacement(destPos);
      while (length > 0L) {
        int l = (int)Math.min(length, Math.min((srcArray[srcSegment]).length - srcDispl, (destArray[destSegment]).length - destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
        if ((srcDispl += l) == 134217728) {
          srcDispl = 0;
          srcSegment++;
        } 
        if ((destDispl += l) == 134217728) {
          destDispl = 0;
          destSegment++;
        } 
        length -= l;
      } 
    } else {
      int srcSegment = segment(srcPos + length);
      int destSegment = segment(destPos + length);
      int srcDispl = displacement(srcPos + length);
      int destDispl = displacement(destPos + length);
      while (length > 0L) {
        if (srcDispl == 0) {
          srcDispl = 134217728;
          srcSegment--;
        } 
        if (destDispl == 0) {
          destDispl = 134217728;
          destSegment--;
        } 
        int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
        srcDispl -= l;
        destDispl -= l;
        length -= l;
      } 
    } 
  }
  
  public static void copyFromBig(double[][] srcArray, long srcPos, double[] destArray, int destPos, int length) {
    int srcSegment = segment(srcPos);
    int srcDispl = displacement(srcPos);
    while (length > 0) {
      int l = Math.min((srcArray[srcSegment]).length - srcDispl, length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
      if ((srcDispl += l) == 134217728) {
        srcDispl = 0;
        srcSegment++;
      } 
      destPos += l;
      length -= l;
    } 
  }
  
  public static void copyToBig(double[] srcArray, int srcPos, double[][] destArray, long destPos, long length) {
    int destSegment = segment(destPos);
    int destDispl = displacement(destPos);
    while (length > 0L) {
      int l = (int)Math.min(((destArray[destSegment]).length - destDispl), length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
      if ((destDispl += l) == 134217728) {
        destDispl = 0;
        destSegment++;
      } 
      srcPos += l;
      length -= l;
    } 
  }
  
  public static double[][] wrap(double[] array) {
    if (array.length == 0)
      return DoubleBigArrays.EMPTY_BIG_ARRAY; 
    if (array.length <= 134217728)
      return new double[][] { array }; 
    double[][] bigArray = DoubleBigArrays.newBigArray(array.length);
    for (int i = 0; i < bigArray.length; ) {
      System.arraycopy(array, (int)start(i), bigArray[i], 0, (bigArray[i]).length);
      i++;
    } 
    return bigArray;
  }
  
  public static double[][] ensureCapacity(double[][] array, long length) {
    return ensureCapacity(array, length, length(array));
  }
  
  public static double[][] forceCapacity(double[][] array, long length, long preserve) {
    ensureLength(length);
    int valid = array.length - ((array.length == 0 || (array.length > 0 && (array[array.length - 1]).length == 134217728)) ? 0 : 1);
    int baseLength = (int)(length + 134217727L >>> 27L);
    double[][] base = Arrays.<double[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0) {
      for (int i = valid; i < baseLength - 1; ) {
        base[i] = new double[134217728];
        i++;
      } 
      base[baseLength - 1] = new double[residual];
    } else {
      for (int i = valid; i < baseLength; ) {
        base[i] = new double[134217728];
        i++;
      } 
    } 
    if (preserve - valid * 134217728L > 0L)
      copy(array, valid * 134217728L, base, valid * 134217728L, preserve - valid * 134217728L); 
    return base;
  }
  
  public static double[][] ensureCapacity(double[][] array, long length, long preserve) {
    return (length > length(array)) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static double[][] grow(double[][] array, long length) {
    long oldLength = length(array);
    return (length > oldLength) ? grow(array, length, oldLength) : array;
  }
  
  public static double[][] grow(double[][] array, long length, long preserve) {
    long oldLength = length(array);
    return (length > oldLength) ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1L), length), preserve) : array;
  }
  
  public static double[][] trim(double[][] array, long length) {
    ensureLength(length);
    long oldLength = length(array);
    if (length >= oldLength)
      return array; 
    int baseLength = (int)(length + 134217727L >>> 27L);
    double[][] base = Arrays.<double[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0)
      base[baseLength - 1] = DoubleArrays.trim(base[baseLength - 1], residual); 
    return base;
  }
  
  public static double[][] setLength(double[][] array, long length) {
    long oldLength = length(array);
    if (length == oldLength)
      return array; 
    if (length < oldLength)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static double[][] copy(double[][] array, long offset, long length) {
    ensureOffsetLength(array, offset, length);
    double[][] a = DoubleBigArrays.newBigArray(length);
    copy(array, offset, a, 0L, length);
    return a;
  }
  
  public static double[][] copy(double[][] array) {
    double[][] base = (double[][])array.clone();
    for (int i = base.length; i-- != 0; base[i] = (double[])array[i].clone());
    return base;
  }
  
  public static void fill(double[][] array, double value) {
    for (int i = array.length; i-- != 0; Arrays.fill(array[i], value));
  }
  
  public static void fill(double[][] array, long from, long to, double value) {
    long length = length(array);
    ensureFromTo(length, from, to);
    if (length == 0L)
      return; 
    int fromSegment = segment(from);
    int toSegment = segment(to);
    int fromDispl = displacement(from);
    int toDispl = displacement(to);
    if (fromSegment == toSegment) {
      Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
      return;
    } 
    if (toDispl != 0)
      Arrays.fill(array[toSegment], 0, toDispl, value); 
    for (; --toSegment > fromSegment; Arrays.fill(array[toSegment], value));
    Arrays.fill(array[fromSegment], fromDispl, 134217728, value);
  }
  
  public static boolean equals(double[][] a1, double[][] a2) {
    if (length(a1) != length(a2))
      return false; 
    int i = a1.length;
    while (i-- != 0) {
      double[] t = a1[i];
      double[] u = a2[i];
      int j = t.length;
      while (j-- != 0) {
        if (Double.doubleToLongBits(t[j]) != Double.doubleToLongBits(u[j]))
          return false; 
      } 
    } 
    return true;
  }
  
  public static String toString(double[][] a) {
    if (a == null)
      return "null"; 
    long last = length(a) - 1L;
    if (last == -1L)
      return "[]"; 
    StringBuilder b = new StringBuilder();
    b.append('[');
    long i;
    for (i = 0L;; i++) {
      b.append(String.valueOf(get(a, i)));
      if (i == last)
        return b.append(']').toString(); 
      b.append(", ");
    } 
  }
  
  public static void ensureFromTo(double[][] a, long from, long to) {
    ensureFromTo(length(a), from, to);
  }
  
  public static void ensureOffsetLength(double[][] a, long offset, long length) {
    ensureOffsetLength(length(a), offset, length);
  }
  
  public static void ensureSameLength(double[][] a, double[][] b) {
    if (length(a) != length(b))
      throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b)); 
  }
  
  public static double[][] shuffle(double[][] a, long from, long to, Random random) {
    for (long i = to - from; i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      double t = get(a, from + i);
      set(a, from + i, get(a, from + p));
      set(a, from + p, t);
    } 
    return a;
  }
  
  public static double[][] shuffle(double[][] a, Random random) {
    for (long i = length(a); i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      double t = get(a, i);
      set(a, i, get(a, p));
      set(a, p, t);
    } 
    return a;
  }
  
  public static boolean get(boolean[][] array, long index) {
    return array[segment(index)][displacement(index)];
  }
  
  public static void set(boolean[][] array, long index, boolean value) {
    array[segment(index)][displacement(index)] = value;
  }
  
  public static void swap(boolean[][] array, long first, long second) {
    boolean t = array[segment(first)][displacement(first)];
    array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
    array[segment(second)][displacement(second)] = t;
  }
  
  public static boolean[][] reverse(boolean[][] a) {
    long length = length(a);
    for (long i = length / 2L; i-- != 0L; swap(a, i, length - i - 1L));
    return a;
  }
  
  public static long length(boolean[][] array) {
    int length = array.length;
    return (length == 0) ? 0L : (start(length - 1) + (array[length - 1]).length);
  }
  
  public static void copy(boolean[][] srcArray, long srcPos, boolean[][] destArray, long destPos, long length) {
    if (destPos <= srcPos) {
      int srcSegment = segment(srcPos);
      int destSegment = segment(destPos);
      int srcDispl = displacement(srcPos);
      int destDispl = displacement(destPos);
      while (length > 0L) {
        int l = (int)Math.min(length, Math.min((srcArray[srcSegment]).length - srcDispl, (destArray[destSegment]).length - destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
        if ((srcDispl += l) == 134217728) {
          srcDispl = 0;
          srcSegment++;
        } 
        if ((destDispl += l) == 134217728) {
          destDispl = 0;
          destSegment++;
        } 
        length -= l;
      } 
    } else {
      int srcSegment = segment(srcPos + length);
      int destSegment = segment(destPos + length);
      int srcDispl = displacement(srcPos + length);
      int destDispl = displacement(destPos + length);
      while (length > 0L) {
        if (srcDispl == 0) {
          srcDispl = 134217728;
          srcSegment--;
        } 
        if (destDispl == 0) {
          destDispl = 134217728;
          destSegment--;
        } 
        int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
        srcDispl -= l;
        destDispl -= l;
        length -= l;
      } 
    } 
  }
  
  public static void copyFromBig(boolean[][] srcArray, long srcPos, boolean[] destArray, int destPos, int length) {
    int srcSegment = segment(srcPos);
    int srcDispl = displacement(srcPos);
    while (length > 0) {
      int l = Math.min((srcArray[srcSegment]).length - srcDispl, length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
      if ((srcDispl += l) == 134217728) {
        srcDispl = 0;
        srcSegment++;
      } 
      destPos += l;
      length -= l;
    } 
  }
  
  public static void copyToBig(boolean[] srcArray, int srcPos, boolean[][] destArray, long destPos, long length) {
    int destSegment = segment(destPos);
    int destDispl = displacement(destPos);
    while (length > 0L) {
      int l = (int)Math.min(((destArray[destSegment]).length - destDispl), length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
      if ((destDispl += l) == 134217728) {
        destDispl = 0;
        destSegment++;
      } 
      srcPos += l;
      length -= l;
    } 
  }
  
  public static boolean[][] wrap(boolean[] array) {
    if (array.length == 0)
      return BooleanBigArrays.EMPTY_BIG_ARRAY; 
    if (array.length <= 134217728)
      return new boolean[][] { array }; 
    boolean[][] bigArray = BooleanBigArrays.newBigArray(array.length);
    for (int i = 0; i < bigArray.length; ) {
      System.arraycopy(array, (int)start(i), bigArray[i], 0, (bigArray[i]).length);
      i++;
    } 
    return bigArray;
  }
  
  public static boolean[][] ensureCapacity(boolean[][] array, long length) {
    return ensureCapacity(array, length, length(array));
  }
  
  public static boolean[][] forceCapacity(boolean[][] array, long length, long preserve) {
    ensureLength(length);
    int valid = array.length - ((array.length == 0 || (array.length > 0 && (array[array.length - 1]).length == 134217728)) ? 0 : 1);
    int baseLength = (int)(length + 134217727L >>> 27L);
    boolean[][] base = Arrays.<boolean[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0) {
      for (int i = valid; i < baseLength - 1; ) {
        base[i] = new boolean[134217728];
        i++;
      } 
      base[baseLength - 1] = new boolean[residual];
    } else {
      for (int i = valid; i < baseLength; ) {
        base[i] = new boolean[134217728];
        i++;
      } 
    } 
    if (preserve - valid * 134217728L > 0L)
      copy(array, valid * 134217728L, base, valid * 134217728L, preserve - valid * 134217728L); 
    return base;
  }
  
  public static boolean[][] ensureCapacity(boolean[][] array, long length, long preserve) {
    return (length > length(array)) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static boolean[][] grow(boolean[][] array, long length) {
    long oldLength = length(array);
    return (length > oldLength) ? grow(array, length, oldLength) : array;
  }
  
  public static boolean[][] grow(boolean[][] array, long length, long preserve) {
    long oldLength = length(array);
    return (length > oldLength) ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1L), length), preserve) : array;
  }
  
  public static boolean[][] trim(boolean[][] array, long length) {
    ensureLength(length);
    long oldLength = length(array);
    if (length >= oldLength)
      return array; 
    int baseLength = (int)(length + 134217727L >>> 27L);
    boolean[][] base = Arrays.<boolean[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0)
      base[baseLength - 1] = BooleanArrays.trim(base[baseLength - 1], residual); 
    return base;
  }
  
  public static boolean[][] setLength(boolean[][] array, long length) {
    long oldLength = length(array);
    if (length == oldLength)
      return array; 
    if (length < oldLength)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static boolean[][] copy(boolean[][] array, long offset, long length) {
    ensureOffsetLength(array, offset, length);
    boolean[][] a = BooleanBigArrays.newBigArray(length);
    copy(array, offset, a, 0L, length);
    return a;
  }
  
  public static boolean[][] copy(boolean[][] array) {
    boolean[][] base = (boolean[][])array.clone();
    for (int i = base.length; i-- != 0; base[i] = (boolean[])array[i].clone());
    return base;
  }
  
  public static void fill(boolean[][] array, boolean value) {
    for (int i = array.length; i-- != 0; Arrays.fill(array[i], value));
  }
  
  public static void fill(boolean[][] array, long from, long to, boolean value) {
    long length = length(array);
    ensureFromTo(length, from, to);
    if (length == 0L)
      return; 
    int fromSegment = segment(from);
    int toSegment = segment(to);
    int fromDispl = displacement(from);
    int toDispl = displacement(to);
    if (fromSegment == toSegment) {
      Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
      return;
    } 
    if (toDispl != 0)
      Arrays.fill(array[toSegment], 0, toDispl, value); 
    for (; --toSegment > fromSegment; Arrays.fill(array[toSegment], value));
    Arrays.fill(array[fromSegment], fromDispl, 134217728, value);
  }
  
  public static boolean equals(boolean[][] a1, boolean[][] a2) {
    if (length(a1) != length(a2))
      return false; 
    int i = a1.length;
    while (i-- != 0) {
      boolean[] t = a1[i];
      boolean[] u = a2[i];
      int j = t.length;
      while (j-- != 0) {
        if (t[j] != u[j])
          return false; 
      } 
    } 
    return true;
  }
  
  public static String toString(boolean[][] a) {
    if (a == null)
      return "null"; 
    long last = length(a) - 1L;
    if (last == -1L)
      return "[]"; 
    StringBuilder b = new StringBuilder();
    b.append('[');
    long i;
    for (i = 0L;; i++) {
      b.append(String.valueOf(get(a, i)));
      if (i == last)
        return b.append(']').toString(); 
      b.append(", ");
    } 
  }
  
  public static void ensureFromTo(boolean[][] a, long from, long to) {
    ensureFromTo(length(a), from, to);
  }
  
  public static void ensureOffsetLength(boolean[][] a, long offset, long length) {
    ensureOffsetLength(length(a), offset, length);
  }
  
  public static void ensureSameLength(boolean[][] a, boolean[][] b) {
    if (length(a) != length(b))
      throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b)); 
  }
  
  public static boolean[][] shuffle(boolean[][] a, long from, long to, Random random) {
    for (long i = to - from; i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      boolean t = get(a, from + i);
      set(a, from + i, get(a, from + p));
      set(a, from + p, t);
    } 
    return a;
  }
  
  public static boolean[][] shuffle(boolean[][] a, Random random) {
    for (long i = length(a); i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      boolean t = get(a, i);
      set(a, i, get(a, p));
      set(a, p, t);
    } 
    return a;
  }
  
  public static short get(short[][] array, long index) {
    return array[segment(index)][displacement(index)];
  }
  
  public static void set(short[][] array, long index, short value) {
    array[segment(index)][displacement(index)] = value;
  }
  
  public static void swap(short[][] array, long first, long second) {
    short t = array[segment(first)][displacement(first)];
    array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
    array[segment(second)][displacement(second)] = t;
  }
  
  public static short[][] reverse(short[][] a) {
    long length = length(a);
    for (long i = length / 2L; i-- != 0L; swap(a, i, length - i - 1L));
    return a;
  }
  
  public static void add(short[][] array, long index, short incr) {
    array[segment(index)][displacement(index)] = (short)(array[segment(index)][displacement(index)] + incr);
  }
  
  public static void mul(short[][] array, long index, short factor) {
    array[segment(index)][displacement(index)] = (short)(array[segment(index)][displacement(index)] * factor);
  }
  
  public static void incr(short[][] array, long index) {
    array[segment(index)][displacement(index)] = (short)(array[segment(index)][displacement(index)] + 1);
  }
  
  public static void decr(short[][] array, long index) {
    array[segment(index)][displacement(index)] = (short)(array[segment(index)][displacement(index)] - 1);
  }
  
  public static long length(short[][] array) {
    int length = array.length;
    return (length == 0) ? 0L : (start(length - 1) + (array[length - 1]).length);
  }
  
  public static void copy(short[][] srcArray, long srcPos, short[][] destArray, long destPos, long length) {
    if (destPos <= srcPos) {
      int srcSegment = segment(srcPos);
      int destSegment = segment(destPos);
      int srcDispl = displacement(srcPos);
      int destDispl = displacement(destPos);
      while (length > 0L) {
        int l = (int)Math.min(length, Math.min((srcArray[srcSegment]).length - srcDispl, (destArray[destSegment]).length - destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
        if ((srcDispl += l) == 134217728) {
          srcDispl = 0;
          srcSegment++;
        } 
        if ((destDispl += l) == 134217728) {
          destDispl = 0;
          destSegment++;
        } 
        length -= l;
      } 
    } else {
      int srcSegment = segment(srcPos + length);
      int destSegment = segment(destPos + length);
      int srcDispl = displacement(srcPos + length);
      int destDispl = displacement(destPos + length);
      while (length > 0L) {
        if (srcDispl == 0) {
          srcDispl = 134217728;
          srcSegment--;
        } 
        if (destDispl == 0) {
          destDispl = 134217728;
          destSegment--;
        } 
        int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
        srcDispl -= l;
        destDispl -= l;
        length -= l;
      } 
    } 
  }
  
  public static void copyFromBig(short[][] srcArray, long srcPos, short[] destArray, int destPos, int length) {
    int srcSegment = segment(srcPos);
    int srcDispl = displacement(srcPos);
    while (length > 0) {
      int l = Math.min((srcArray[srcSegment]).length - srcDispl, length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
      if ((srcDispl += l) == 134217728) {
        srcDispl = 0;
        srcSegment++;
      } 
      destPos += l;
      length -= l;
    } 
  }
  
  public static void copyToBig(short[] srcArray, int srcPos, short[][] destArray, long destPos, long length) {
    int destSegment = segment(destPos);
    int destDispl = displacement(destPos);
    while (length > 0L) {
      int l = (int)Math.min(((destArray[destSegment]).length - destDispl), length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
      if ((destDispl += l) == 134217728) {
        destDispl = 0;
        destSegment++;
      } 
      srcPos += l;
      length -= l;
    } 
  }
  
  public static short[][] wrap(short[] array) {
    if (array.length == 0)
      return ShortBigArrays.EMPTY_BIG_ARRAY; 
    if (array.length <= 134217728)
      return new short[][] { array }; 
    short[][] bigArray = ShortBigArrays.newBigArray(array.length);
    for (int i = 0; i < bigArray.length; ) {
      System.arraycopy(array, (int)start(i), bigArray[i], 0, (bigArray[i]).length);
      i++;
    } 
    return bigArray;
  }
  
  public static short[][] ensureCapacity(short[][] array, long length) {
    return ensureCapacity(array, length, length(array));
  }
  
  public static short[][] forceCapacity(short[][] array, long length, long preserve) {
    ensureLength(length);
    int valid = array.length - ((array.length == 0 || (array.length > 0 && (array[array.length - 1]).length == 134217728)) ? 0 : 1);
    int baseLength = (int)(length + 134217727L >>> 27L);
    short[][] base = Arrays.<short[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0) {
      for (int i = valid; i < baseLength - 1; ) {
        base[i] = new short[134217728];
        i++;
      } 
      base[baseLength - 1] = new short[residual];
    } else {
      for (int i = valid; i < baseLength; ) {
        base[i] = new short[134217728];
        i++;
      } 
    } 
    if (preserve - valid * 134217728L > 0L)
      copy(array, valid * 134217728L, base, valid * 134217728L, preserve - valid * 134217728L); 
    return base;
  }
  
  public static short[][] ensureCapacity(short[][] array, long length, long preserve) {
    return (length > length(array)) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static short[][] grow(short[][] array, long length) {
    long oldLength = length(array);
    return (length > oldLength) ? grow(array, length, oldLength) : array;
  }
  
  public static short[][] grow(short[][] array, long length, long preserve) {
    long oldLength = length(array);
    return (length > oldLength) ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1L), length), preserve) : array;
  }
  
  public static short[][] trim(short[][] array, long length) {
    ensureLength(length);
    long oldLength = length(array);
    if (length >= oldLength)
      return array; 
    int baseLength = (int)(length + 134217727L >>> 27L);
    short[][] base = Arrays.<short[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0)
      base[baseLength - 1] = ShortArrays.trim(base[baseLength - 1], residual); 
    return base;
  }
  
  public static short[][] setLength(short[][] array, long length) {
    long oldLength = length(array);
    if (length == oldLength)
      return array; 
    if (length < oldLength)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static short[][] copy(short[][] array, long offset, long length) {
    ensureOffsetLength(array, offset, length);
    short[][] a = ShortBigArrays.newBigArray(length);
    copy(array, offset, a, 0L, length);
    return a;
  }
  
  public static short[][] copy(short[][] array) {
    short[][] base = (short[][])array.clone();
    for (int i = base.length; i-- != 0; base[i] = (short[])array[i].clone());
    return base;
  }
  
  public static void fill(short[][] array, short value) {
    for (int i = array.length; i-- != 0; Arrays.fill(array[i], value));
  }
  
  public static void fill(short[][] array, long from, long to, short value) {
    long length = length(array);
    ensureFromTo(length, from, to);
    if (length == 0L)
      return; 
    int fromSegment = segment(from);
    int toSegment = segment(to);
    int fromDispl = displacement(from);
    int toDispl = displacement(to);
    if (fromSegment == toSegment) {
      Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
      return;
    } 
    if (toDispl != 0)
      Arrays.fill(array[toSegment], 0, toDispl, value); 
    for (; --toSegment > fromSegment; Arrays.fill(array[toSegment], value));
    Arrays.fill(array[fromSegment], fromDispl, 134217728, value);
  }
  
  public static boolean equals(short[][] a1, short[][] a2) {
    if (length(a1) != length(a2))
      return false; 
    int i = a1.length;
    while (i-- != 0) {
      short[] t = a1[i];
      short[] u = a2[i];
      int j = t.length;
      while (j-- != 0) {
        if (t[j] != u[j])
          return false; 
      } 
    } 
    return true;
  }
  
  public static String toString(short[][] a) {
    if (a == null)
      return "null"; 
    long last = length(a) - 1L;
    if (last == -1L)
      return "[]"; 
    StringBuilder b = new StringBuilder();
    b.append('[');
    long i;
    for (i = 0L;; i++) {
      b.append(String.valueOf(get(a, i)));
      if (i == last)
        return b.append(']').toString(); 
      b.append(", ");
    } 
  }
  
  public static void ensureFromTo(short[][] a, long from, long to) {
    ensureFromTo(length(a), from, to);
  }
  
  public static void ensureOffsetLength(short[][] a, long offset, long length) {
    ensureOffsetLength(length(a), offset, length);
  }
  
  public static void ensureSameLength(short[][] a, short[][] b) {
    if (length(a) != length(b))
      throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b)); 
  }
  
  public static short[][] shuffle(short[][] a, long from, long to, Random random) {
    for (long i = to - from; i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      short t = get(a, from + i);
      set(a, from + i, get(a, from + p));
      set(a, from + p, t);
    } 
    return a;
  }
  
  public static short[][] shuffle(short[][] a, Random random) {
    for (long i = length(a); i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      short t = get(a, i);
      set(a, i, get(a, p));
      set(a, p, t);
    } 
    return a;
  }
  
  public static char get(char[][] array, long index) {
    return array[segment(index)][displacement(index)];
  }
  
  public static void set(char[][] array, long index, char value) {
    array[segment(index)][displacement(index)] = value;
  }
  
  public static void swap(char[][] array, long first, long second) {
    char t = array[segment(first)][displacement(first)];
    array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
    array[segment(second)][displacement(second)] = t;
  }
  
  public static char[][] reverse(char[][] a) {
    long length = length(a);
    for (long i = length / 2L; i-- != 0L; swap(a, i, length - i - 1L));
    return a;
  }
  
  public static void add(char[][] array, long index, char incr) {
    array[segment(index)][displacement(index)] = (char)(array[segment(index)][displacement(index)] + incr);
  }
  
  public static void mul(char[][] array, long index, char factor) {
    array[segment(index)][displacement(index)] = (char)(array[segment(index)][displacement(index)] * factor);
  }
  
  public static void incr(char[][] array, long index) {
    array[segment(index)][displacement(index)] = (char)(array[segment(index)][displacement(index)] + 1);
  }
  
  public static void decr(char[][] array, long index) {
    array[segment(index)][displacement(index)] = (char)(array[segment(index)][displacement(index)] - 1);
  }
  
  public static long length(char[][] array) {
    int length = array.length;
    return (length == 0) ? 0L : (start(length - 1) + (array[length - 1]).length);
  }
  
  public static void copy(char[][] srcArray, long srcPos, char[][] destArray, long destPos, long length) {
    if (destPos <= srcPos) {
      int srcSegment = segment(srcPos);
      int destSegment = segment(destPos);
      int srcDispl = displacement(srcPos);
      int destDispl = displacement(destPos);
      while (length > 0L) {
        int l = (int)Math.min(length, Math.min((srcArray[srcSegment]).length - srcDispl, (destArray[destSegment]).length - destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
        if ((srcDispl += l) == 134217728) {
          srcDispl = 0;
          srcSegment++;
        } 
        if ((destDispl += l) == 134217728) {
          destDispl = 0;
          destSegment++;
        } 
        length -= l;
      } 
    } else {
      int srcSegment = segment(srcPos + length);
      int destSegment = segment(destPos + length);
      int srcDispl = displacement(srcPos + length);
      int destDispl = displacement(destPos + length);
      while (length > 0L) {
        if (srcDispl == 0) {
          srcDispl = 134217728;
          srcSegment--;
        } 
        if (destDispl == 0) {
          destDispl = 134217728;
          destSegment--;
        } 
        int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
        srcDispl -= l;
        destDispl -= l;
        length -= l;
      } 
    } 
  }
  
  public static void copyFromBig(char[][] srcArray, long srcPos, char[] destArray, int destPos, int length) {
    int srcSegment = segment(srcPos);
    int srcDispl = displacement(srcPos);
    while (length > 0) {
      int l = Math.min((srcArray[srcSegment]).length - srcDispl, length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
      if ((srcDispl += l) == 134217728) {
        srcDispl = 0;
        srcSegment++;
      } 
      destPos += l;
      length -= l;
    } 
  }
  
  public static void copyToBig(char[] srcArray, int srcPos, char[][] destArray, long destPos, long length) {
    int destSegment = segment(destPos);
    int destDispl = displacement(destPos);
    while (length > 0L) {
      int l = (int)Math.min(((destArray[destSegment]).length - destDispl), length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
      if ((destDispl += l) == 134217728) {
        destDispl = 0;
        destSegment++;
      } 
      srcPos += l;
      length -= l;
    } 
  }
  
  public static char[][] wrap(char[] array) {
    if (array.length == 0)
      return CharBigArrays.EMPTY_BIG_ARRAY; 
    if (array.length <= 134217728)
      return new char[][] { array }; 
    char[][] bigArray = CharBigArrays.newBigArray(array.length);
    for (int i = 0; i < bigArray.length; ) {
      System.arraycopy(array, (int)start(i), bigArray[i], 0, (bigArray[i]).length);
      i++;
    } 
    return bigArray;
  }
  
  public static char[][] ensureCapacity(char[][] array, long length) {
    return ensureCapacity(array, length, length(array));
  }
  
  public static char[][] forceCapacity(char[][] array, long length, long preserve) {
    ensureLength(length);
    int valid = array.length - ((array.length == 0 || (array.length > 0 && (array[array.length - 1]).length == 134217728)) ? 0 : 1);
    int baseLength = (int)(length + 134217727L >>> 27L);
    char[][] base = Arrays.<char[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0) {
      for (int i = valid; i < baseLength - 1; ) {
        base[i] = new char[134217728];
        i++;
      } 
      base[baseLength - 1] = new char[residual];
    } else {
      for (int i = valid; i < baseLength; ) {
        base[i] = new char[134217728];
        i++;
      } 
    } 
    if (preserve - valid * 134217728L > 0L)
      copy(array, valid * 134217728L, base, valid * 134217728L, preserve - valid * 134217728L); 
    return base;
  }
  
  public static char[][] ensureCapacity(char[][] array, long length, long preserve) {
    return (length > length(array)) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static char[][] grow(char[][] array, long length) {
    long oldLength = length(array);
    return (length > oldLength) ? grow(array, length, oldLength) : array;
  }
  
  public static char[][] grow(char[][] array, long length, long preserve) {
    long oldLength = length(array);
    return (length > oldLength) ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1L), length), preserve) : array;
  }
  
  public static char[][] trim(char[][] array, long length) {
    ensureLength(length);
    long oldLength = length(array);
    if (length >= oldLength)
      return array; 
    int baseLength = (int)(length + 134217727L >>> 27L);
    char[][] base = Arrays.<char[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0)
      base[baseLength - 1] = CharArrays.trim(base[baseLength - 1], residual); 
    return base;
  }
  
  public static char[][] setLength(char[][] array, long length) {
    long oldLength = length(array);
    if (length == oldLength)
      return array; 
    if (length < oldLength)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static char[][] copy(char[][] array, long offset, long length) {
    ensureOffsetLength(array, offset, length);
    char[][] a = CharBigArrays.newBigArray(length);
    copy(array, offset, a, 0L, length);
    return a;
  }
  
  public static char[][] copy(char[][] array) {
    char[][] base = (char[][])array.clone();
    for (int i = base.length; i-- != 0; base[i] = (char[])array[i].clone());
    return base;
  }
  
  public static void fill(char[][] array, char value) {
    for (int i = array.length; i-- != 0; Arrays.fill(array[i], value));
  }
  
  public static void fill(char[][] array, long from, long to, char value) {
    long length = length(array);
    ensureFromTo(length, from, to);
    if (length == 0L)
      return; 
    int fromSegment = segment(from);
    int toSegment = segment(to);
    int fromDispl = displacement(from);
    int toDispl = displacement(to);
    if (fromSegment == toSegment) {
      Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
      return;
    } 
    if (toDispl != 0)
      Arrays.fill(array[toSegment], 0, toDispl, value); 
    for (; --toSegment > fromSegment; Arrays.fill(array[toSegment], value));
    Arrays.fill(array[fromSegment], fromDispl, 134217728, value);
  }
  
  public static boolean equals(char[][] a1, char[][] a2) {
    if (length(a1) != length(a2))
      return false; 
    int i = a1.length;
    while (i-- != 0) {
      char[] t = a1[i];
      char[] u = a2[i];
      int j = t.length;
      while (j-- != 0) {
        if (t[j] != u[j])
          return false; 
      } 
    } 
    return true;
  }
  
  public static String toString(char[][] a) {
    if (a == null)
      return "null"; 
    long last = length(a) - 1L;
    if (last == -1L)
      return "[]"; 
    StringBuilder b = new StringBuilder();
    b.append('[');
    long i;
    for (i = 0L;; i++) {
      b.append(String.valueOf(get(a, i)));
      if (i == last)
        return b.append(']').toString(); 
      b.append(", ");
    } 
  }
  
  public static void ensureFromTo(char[][] a, long from, long to) {
    ensureFromTo(length(a), from, to);
  }
  
  public static void ensureOffsetLength(char[][] a, long offset, long length) {
    ensureOffsetLength(length(a), offset, length);
  }
  
  public static void ensureSameLength(char[][] a, char[][] b) {
    if (length(a) != length(b))
      throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b)); 
  }
  
  public static char[][] shuffle(char[][] a, long from, long to, Random random) {
    for (long i = to - from; i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      char t = get(a, from + i);
      set(a, from + i, get(a, from + p));
      set(a, from + p, t);
    } 
    return a;
  }
  
  public static char[][] shuffle(char[][] a, Random random) {
    for (long i = length(a); i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      char t = get(a, i);
      set(a, i, get(a, p));
      set(a, p, t);
    } 
    return a;
  }
  
  public static float get(float[][] array, long index) {
    return array[segment(index)][displacement(index)];
  }
  
  public static void set(float[][] array, long index, float value) {
    array[segment(index)][displacement(index)] = value;
  }
  
  public static void swap(float[][] array, long first, long second) {
    float t = array[segment(first)][displacement(first)];
    array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
    array[segment(second)][displacement(second)] = t;
  }
  
  public static float[][] reverse(float[][] a) {
    long length = length(a);
    for (long i = length / 2L; i-- != 0L; swap(a, i, length - i - 1L));
    return a;
  }
  
  public static void add(float[][] array, long index, float incr) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] + incr;
  }
  
  public static void mul(float[][] array, long index, float factor) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] * factor;
  }
  
  public static void incr(float[][] array, long index) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] + 1.0F;
  }
  
  public static void decr(float[][] array, long index) {
    array[segment(index)][displacement(index)] = array[segment(index)][displacement(index)] - 1.0F;
  }
  
  public static long length(float[][] array) {
    int length = array.length;
    return (length == 0) ? 0L : (start(length - 1) + (array[length - 1]).length);
  }
  
  public static void copy(float[][] srcArray, long srcPos, float[][] destArray, long destPos, long length) {
    if (destPos <= srcPos) {
      int srcSegment = segment(srcPos);
      int destSegment = segment(destPos);
      int srcDispl = displacement(srcPos);
      int destDispl = displacement(destPos);
      while (length > 0L) {
        int l = (int)Math.min(length, Math.min((srcArray[srcSegment]).length - srcDispl, (destArray[destSegment]).length - destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
        if ((srcDispl += l) == 134217728) {
          srcDispl = 0;
          srcSegment++;
        } 
        if ((destDispl += l) == 134217728) {
          destDispl = 0;
          destSegment++;
        } 
        length -= l;
      } 
    } else {
      int srcSegment = segment(srcPos + length);
      int destSegment = segment(destPos + length);
      int srcDispl = displacement(srcPos + length);
      int destDispl = displacement(destPos + length);
      while (length > 0L) {
        if (srcDispl == 0) {
          srcDispl = 134217728;
          srcSegment--;
        } 
        if (destDispl == 0) {
          destDispl = 134217728;
          destSegment--;
        } 
        int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
        srcDispl -= l;
        destDispl -= l;
        length -= l;
      } 
    } 
  }
  
  public static void copyFromBig(float[][] srcArray, long srcPos, float[] destArray, int destPos, int length) {
    int srcSegment = segment(srcPos);
    int srcDispl = displacement(srcPos);
    while (length > 0) {
      int l = Math.min((srcArray[srcSegment]).length - srcDispl, length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
      if ((srcDispl += l) == 134217728) {
        srcDispl = 0;
        srcSegment++;
      } 
      destPos += l;
      length -= l;
    } 
  }
  
  public static void copyToBig(float[] srcArray, int srcPos, float[][] destArray, long destPos, long length) {
    int destSegment = segment(destPos);
    int destDispl = displacement(destPos);
    while (length > 0L) {
      int l = (int)Math.min(((destArray[destSegment]).length - destDispl), length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
      if ((destDispl += l) == 134217728) {
        destDispl = 0;
        destSegment++;
      } 
      srcPos += l;
      length -= l;
    } 
  }
  
  public static float[][] wrap(float[] array) {
    if (array.length == 0)
      return FloatBigArrays.EMPTY_BIG_ARRAY; 
    if (array.length <= 134217728)
      return new float[][] { array }; 
    float[][] bigArray = FloatBigArrays.newBigArray(array.length);
    for (int i = 0; i < bigArray.length; ) {
      System.arraycopy(array, (int)start(i), bigArray[i], 0, (bigArray[i]).length);
      i++;
    } 
    return bigArray;
  }
  
  public static float[][] ensureCapacity(float[][] array, long length) {
    return ensureCapacity(array, length, length(array));
  }
  
  public static float[][] forceCapacity(float[][] array, long length, long preserve) {
    ensureLength(length);
    int valid = array.length - ((array.length == 0 || (array.length > 0 && (array[array.length - 1]).length == 134217728)) ? 0 : 1);
    int baseLength = (int)(length + 134217727L >>> 27L);
    float[][] base = Arrays.<float[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0) {
      for (int i = valid; i < baseLength - 1; ) {
        base[i] = new float[134217728];
        i++;
      } 
      base[baseLength - 1] = new float[residual];
    } else {
      for (int i = valid; i < baseLength; ) {
        base[i] = new float[134217728];
        i++;
      } 
    } 
    if (preserve - valid * 134217728L > 0L)
      copy(array, valid * 134217728L, base, valid * 134217728L, preserve - valid * 134217728L); 
    return base;
  }
  
  public static float[][] ensureCapacity(float[][] array, long length, long preserve) {
    return (length > length(array)) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static float[][] grow(float[][] array, long length) {
    long oldLength = length(array);
    return (length > oldLength) ? grow(array, length, oldLength) : array;
  }
  
  public static float[][] grow(float[][] array, long length, long preserve) {
    long oldLength = length(array);
    return (length > oldLength) ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1L), length), preserve) : array;
  }
  
  public static float[][] trim(float[][] array, long length) {
    ensureLength(length);
    long oldLength = length(array);
    if (length >= oldLength)
      return array; 
    int baseLength = (int)(length + 134217727L >>> 27L);
    float[][] base = Arrays.<float[]>copyOf(array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0)
      base[baseLength - 1] = FloatArrays.trim(base[baseLength - 1], residual); 
    return base;
  }
  
  public static float[][] setLength(float[][] array, long length) {
    long oldLength = length(array);
    if (length == oldLength)
      return array; 
    if (length < oldLength)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static float[][] copy(float[][] array, long offset, long length) {
    ensureOffsetLength(array, offset, length);
    float[][] a = FloatBigArrays.newBigArray(length);
    copy(array, offset, a, 0L, length);
    return a;
  }
  
  public static float[][] copy(float[][] array) {
    float[][] base = (float[][])array.clone();
    for (int i = base.length; i-- != 0; base[i] = (float[])array[i].clone());
    return base;
  }
  
  public static void fill(float[][] array, float value) {
    for (int i = array.length; i-- != 0; Arrays.fill(array[i], value));
  }
  
  public static void fill(float[][] array, long from, long to, float value) {
    long length = length(array);
    ensureFromTo(length, from, to);
    if (length == 0L)
      return; 
    int fromSegment = segment(from);
    int toSegment = segment(to);
    int fromDispl = displacement(from);
    int toDispl = displacement(to);
    if (fromSegment == toSegment) {
      Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
      return;
    } 
    if (toDispl != 0)
      Arrays.fill(array[toSegment], 0, toDispl, value); 
    for (; --toSegment > fromSegment; Arrays.fill(array[toSegment], value));
    Arrays.fill(array[fromSegment], fromDispl, 134217728, value);
  }
  
  public static boolean equals(float[][] a1, float[][] a2) {
    if (length(a1) != length(a2))
      return false; 
    int i = a1.length;
    while (i-- != 0) {
      float[] t = a1[i];
      float[] u = a2[i];
      int j = t.length;
      while (j-- != 0) {
        if (Float.floatToIntBits(t[j]) != Float.floatToIntBits(u[j]))
          return false; 
      } 
    } 
    return true;
  }
  
  public static String toString(float[][] a) {
    if (a == null)
      return "null"; 
    long last = length(a) - 1L;
    if (last == -1L)
      return "[]"; 
    StringBuilder b = new StringBuilder();
    b.append('[');
    long i;
    for (i = 0L;; i++) {
      b.append(String.valueOf(get(a, i)));
      if (i == last)
        return b.append(']').toString(); 
      b.append(", ");
    } 
  }
  
  public static void ensureFromTo(float[][] a, long from, long to) {
    ensureFromTo(length(a), from, to);
  }
  
  public static void ensureOffsetLength(float[][] a, long offset, long length) {
    ensureOffsetLength(length(a), offset, length);
  }
  
  public static void ensureSameLength(float[][] a, float[][] b) {
    if (length(a) != length(b))
      throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b)); 
  }
  
  public static float[][] shuffle(float[][] a, long from, long to, Random random) {
    for (long i = to - from; i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      float t = get(a, from + i);
      set(a, from + i, get(a, from + p));
      set(a, from + p, t);
    } 
    return a;
  }
  
  public static float[][] shuffle(float[][] a, Random random) {
    for (long i = length(a); i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      float t = get(a, i);
      set(a, i, get(a, p));
      set(a, p, t);
    } 
    return a;
  }
  
  public static <K> K get(K[][] array, long index) {
    return array[segment(index)][displacement(index)];
  }
  
  public static <K> void set(K[][] array, long index, K value) {
    array[segment(index)][displacement(index)] = value;
  }
  
  public static <K> void swap(K[][] array, long first, long second) {
    K t = array[segment(first)][displacement(first)];
    array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
    array[segment(second)][displacement(second)] = t;
  }
  
  public static <K> K[][] reverse(K[][] a) {
    long length = length(a);
    for (long i = length / 2L; i-- != 0L; swap(a, i, length - i - 1L));
    return a;
  }
  
  public static <K> long length(K[][] array) {
    int length = array.length;
    return (length == 0) ? 0L : (start(length - 1) + (array[length - 1]).length);
  }
  
  public static <K> void copy(K[][] srcArray, long srcPos, K[][] destArray, long destPos, long length) {
    if (destPos <= srcPos) {
      int srcSegment = segment(srcPos);
      int destSegment = segment(destPos);
      int srcDispl = displacement(srcPos);
      int destDispl = displacement(destPos);
      while (length > 0L) {
        int l = (int)Math.min(length, Math.min((srcArray[srcSegment]).length - srcDispl, (destArray[destSegment]).length - destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
        if ((srcDispl += l) == 134217728) {
          srcDispl = 0;
          srcSegment++;
        } 
        if ((destDispl += l) == 134217728) {
          destDispl = 0;
          destSegment++;
        } 
        length -= l;
      } 
    } else {
      int srcSegment = segment(srcPos + length);
      int destSegment = segment(destPos + length);
      int srcDispl = displacement(srcPos + length);
      int destDispl = displacement(destPos + length);
      while (length > 0L) {
        if (srcDispl == 0) {
          srcDispl = 134217728;
          srcSegment--;
        } 
        if (destDispl == 0) {
          destDispl = 134217728;
          destSegment--;
        } 
        int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
        if (l == 0)
          throw new ArrayIndexOutOfBoundsException(); 
        System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
        srcDispl -= l;
        destDispl -= l;
        length -= l;
      } 
    } 
  }
  
  public static <K> void copyFromBig(K[][] srcArray, long srcPos, K[] destArray, int destPos, int length) {
    int srcSegment = segment(srcPos);
    int srcDispl = displacement(srcPos);
    while (length > 0) {
      int l = Math.min((srcArray[srcSegment]).length - srcDispl, length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
      if ((srcDispl += l) == 134217728) {
        srcDispl = 0;
        srcSegment++;
      } 
      destPos += l;
      length -= l;
    } 
  }
  
  public static <K> void copyToBig(K[] srcArray, int srcPos, K[][] destArray, long destPos, long length) {
    int destSegment = segment(destPos);
    int destDispl = displacement(destPos);
    while (length > 0L) {
      int l = (int)Math.min(((destArray[destSegment]).length - destDispl), length);
      if (l == 0)
        throw new ArrayIndexOutOfBoundsException(); 
      System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
      if ((destDispl += l) == 134217728) {
        destDispl = 0;
        destSegment++;
      } 
      srcPos += l;
      length -= l;
    } 
  }
  
  public static <K> K[][] wrap(K[] array) {
    if (array.length == 0 && array.getClass() == Object[].class)
      return (K[][])ObjectBigArrays.EMPTY_BIG_ARRAY; 
    if (array.length <= 134217728) {
      K[][] arrayOfK = (K[][])Array.newInstance(array.getClass(), 1);
      arrayOfK[0] = array;
      return arrayOfK;
    } 
    K[][] bigArray = (K[][])ObjectBigArrays.newBigArray(array.getClass(), array.length);
    for (int i = 0; i < bigArray.length; ) {
      System.arraycopy(array, (int)start(i), bigArray[i], 0, (bigArray[i]).length);
      i++;
    } 
    return bigArray;
  }
  
  public static <K> K[][] ensureCapacity(K[][] array, long length) {
    return ensureCapacity(array, length, length(array));
  }
  
  public static <K> K[][] forceCapacity(K[][] array, long length, long preserve) {
    ensureLength(length);
    int valid = array.length - ((array.length == 0 || (array.length > 0 && (array[array.length - 1]).length == 134217728)) ? 0 : 1);
    int baseLength = (int)(length + 134217727L >>> 27L);
    K[][] base = (K[][])Arrays.<Object[]>copyOf((Object[][])array, baseLength);
    Class<?> componentType = array.getClass().getComponentType();
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0) {
      for (int i = valid; i < baseLength - 1; ) {
        base[i] = (K[])Array.newInstance(componentType.getComponentType(), 134217728);
        i++;
      } 
      base[baseLength - 1] = (K[])Array.newInstance(componentType.getComponentType(), residual);
    } else {
      for (int i = valid; i < baseLength; ) {
        base[i] = (K[])Array.newInstance(componentType.getComponentType(), 134217728);
        i++;
      } 
    } 
    if (preserve - valid * 134217728L > 0L)
      copy(array, valid * 134217728L, base, valid * 134217728L, preserve - valid * 134217728L); 
    return base;
  }
  
  public static <K> K[][] ensureCapacity(K[][] array, long length, long preserve) {
    return (length > length(array)) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static <K> K[][] grow(K[][] array, long length) {
    long oldLength = length(array);
    return (length > oldLength) ? grow(array, length, oldLength) : array;
  }
  
  public static <K> K[][] grow(K[][] array, long length, long preserve) {
    long oldLength = length(array);
    return (length > oldLength) ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1L), length), preserve) : array;
  }
  
  public static <K> K[][] trim(K[][] array, long length) {
    ensureLength(length);
    long oldLength = length(array);
    if (length >= oldLength)
      return array; 
    int baseLength = (int)(length + 134217727L >>> 27L);
    K[][] base = (K[][])Arrays.<Object[]>copyOf((Object[][])array, baseLength);
    int residual = (int)(length & 0x7FFFFFFL);
    if (residual != 0)
      base[baseLength - 1] = (K[])ObjectArrays.trim((Object[])base[baseLength - 1], residual); 
    return base;
  }
  
  public static <K> K[][] setLength(K[][] array, long length) {
    long oldLength = length(array);
    if (length == oldLength)
      return array; 
    if (length < oldLength)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static <K> K[][] copy(K[][] array, long offset, long length) {
    ensureOffsetLength(array, offset, length);
    K[][] a = (K[][])ObjectBigArrays.newBigArray((Object[][])array, length);
    copy(array, offset, a, 0L, length);
    return a;
  }
  
  public static <K> K[][] copy(K[][] array) {
    K[][] base = (K[][])array.clone();
    for (int i = base.length; i-- != 0; base[i] = (K[])array[i].clone());
    return base;
  }
  
  public static <K> void fill(K[][] array, K value) {
    for (int i = array.length; i-- != 0; Arrays.fill((Object[])array[i], value));
  }
  
  public static <K> void fill(K[][] array, long from, long to, K value) {
    long length = length(array);
    ensureFromTo(length, from, to);
    if (length == 0L)
      return; 
    int fromSegment = segment(from);
    int toSegment = segment(to);
    int fromDispl = displacement(from);
    int toDispl = displacement(to);
    if (fromSegment == toSegment) {
      Arrays.fill((Object[])array[fromSegment], fromDispl, toDispl, value);
      return;
    } 
    if (toDispl != 0)
      Arrays.fill((Object[])array[toSegment], 0, toDispl, value); 
    for (; --toSegment > fromSegment; Arrays.fill((Object[])array[toSegment], value));
    Arrays.fill((Object[])array[fromSegment], fromDispl, 134217728, value);
  }
  
  public static <K> boolean equals(K[][] a1, K[][] a2) {
    if (length(a1) != length(a2))
      return false; 
    int i = a1.length;
    while (i-- != 0) {
      K[] t = a1[i];
      K[] u = a2[i];
      int j = t.length;
      while (j-- != 0) {
        if (!Objects.equals(t[j], u[j]))
          return false; 
      } 
    } 
    return true;
  }
  
  public static <K> String toString(K[][] a) {
    if (a == null)
      return "null"; 
    long last = length(a) - 1L;
    if (last == -1L)
      return "[]"; 
    StringBuilder b = new StringBuilder();
    b.append('[');
    long i;
    for (i = 0L;; i++) {
      b.append(String.valueOf(get(a, i)));
      if (i == last)
        return b.append(']').toString(); 
      b.append(", ");
    } 
  }
  
  public static <K> void ensureFromTo(K[][] a, long from, long to) {
    ensureFromTo(length(a), from, to);
  }
  
  public static <K> void ensureOffsetLength(K[][] a, long offset, long length) {
    ensureOffsetLength(length(a), offset, length);
  }
  
  public static <K> void ensureSameLength(K[][] a, K[][] b) {
    if (length(a) != length(b))
      throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b)); 
  }
  
  public static <K> K[][] shuffle(K[][] a, long from, long to, Random random) {
    for (long i = to - from; i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      K t = get(a, from + i);
      set(a, from + i, get(a, from + p));
      set(a, from + p, t);
    } 
    return a;
  }
  
  public static <K> K[][] shuffle(K[][] a, Random random) {
    for (long i = length(a); i-- != 0L; ) {
      long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
      K t = get(a, i);
      set(a, i, get(a, p));
      set(a, p, t);
    } 
    return a;
  }
  
  public static void main(String[] arg) {
    // Byte code:
    //   0: lconst_1
    //   1: aload_0
    //   2: iconst_0
    //   3: aaload
    //   4: invokestatic parseInt : (Ljava/lang/String;)I
    //   7: lshl
    //   8: invokestatic newBigArray : (J)[[I
    //   11: astore_1
    //   12: bipush #10
    //   14: istore #10
    //   16: iload #10
    //   18: iinc #10, -1
    //   21: ifeq -> 378
    //   24: invokestatic currentTimeMillis : ()J
    //   27: lneg
    //   28: lstore #8
    //   30: lconst_0
    //   31: lstore_2
    //   32: aload_1
    //   33: invokestatic length : ([[I)J
    //   36: lstore #11
    //   38: lload #11
    //   40: dup2
    //   41: lconst_1
    //   42: lsub
    //   43: lstore #11
    //   45: lconst_0
    //   46: lcmp
    //   47: ifeq -> 66
    //   50: lload_2
    //   51: lload #11
    //   53: aload_1
    //   54: lload #11
    //   56: invokestatic get : ([[IJ)I
    //   59: i2l
    //   60: lxor
    //   61: lxor
    //   62: lstore_2
    //   63: goto -> 38
    //   66: lload_2
    //   67: lconst_0
    //   68: lcmp
    //   69: ifne -> 78
    //   72: getstatic java/lang/System.err : Ljava/io/PrintStream;
    //   75: invokevirtual println : ()V
    //   78: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   81: new java/lang/StringBuilder
    //   84: dup
    //   85: invokespecial <init> : ()V
    //   88: ldc_w 'Single loop: '
    //   91: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   94: lload #8
    //   96: invokestatic currentTimeMillis : ()J
    //   99: ladd
    //   100: invokevirtual append : (J)Ljava/lang/StringBuilder;
    //   103: ldc_w 'ms'
    //   106: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   109: invokevirtual toString : ()Ljava/lang/String;
    //   112: invokevirtual println : (Ljava/lang/String;)V
    //   115: invokestatic currentTimeMillis : ()J
    //   118: lneg
    //   119: lstore #8
    //   121: lconst_0
    //   122: lstore #4
    //   124: aload_1
    //   125: arraylength
    //   126: istore #11
    //   128: iload #11
    //   130: iinc #11, -1
    //   133: ifeq -> 180
    //   136: aload_1
    //   137: iload #11
    //   139: aaload
    //   140: astore #12
    //   142: aload #12
    //   144: arraylength
    //   145: istore #13
    //   147: iload #13
    //   149: iinc #13, -1
    //   152: ifeq -> 177
    //   155: lload #4
    //   157: aload #12
    //   159: iload #13
    //   161: iaload
    //   162: i2l
    //   163: iload #11
    //   165: iload #13
    //   167: invokestatic index : (II)J
    //   170: lxor
    //   171: lxor
    //   172: lstore #4
    //   174: goto -> 147
    //   177: goto -> 128
    //   180: lload #4
    //   182: lconst_0
    //   183: lcmp
    //   184: ifne -> 193
    //   187: getstatic java/lang/System.err : Ljava/io/PrintStream;
    //   190: invokevirtual println : ()V
    //   193: lload_2
    //   194: lload #4
    //   196: lcmp
    //   197: ifeq -> 208
    //   200: new java/lang/AssertionError
    //   203: dup
    //   204: invokespecial <init> : ()V
    //   207: athrow
    //   208: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   211: new java/lang/StringBuilder
    //   214: dup
    //   215: invokespecial <init> : ()V
    //   218: ldc_w 'Double loop: '
    //   221: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   224: lload #8
    //   226: invokestatic currentTimeMillis : ()J
    //   229: ladd
    //   230: invokevirtual append : (J)Ljava/lang/StringBuilder;
    //   233: ldc_w 'ms'
    //   236: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   239: invokevirtual toString : ()Ljava/lang/String;
    //   242: invokevirtual println : (Ljava/lang/String;)V
    //   245: lconst_0
    //   246: lstore #6
    //   248: aload_1
    //   249: invokestatic length : ([[I)J
    //   252: lstore #11
    //   254: aload_1
    //   255: arraylength
    //   256: istore #13
    //   258: iload #13
    //   260: iinc #13, -1
    //   263: ifeq -> 310
    //   266: aload_1
    //   267: iload #13
    //   269: aaload
    //   270: astore #14
    //   272: aload #14
    //   274: arraylength
    //   275: istore #15
    //   277: iload #15
    //   279: iinc #15, -1
    //   282: ifeq -> 307
    //   285: lload #4
    //   287: aload #14
    //   289: iload #15
    //   291: iaload
    //   292: i2l
    //   293: lload #11
    //   295: lconst_1
    //   296: lsub
    //   297: dup2
    //   298: lstore #11
    //   300: lxor
    //   301: lxor
    //   302: lstore #4
    //   304: goto -> 277
    //   307: goto -> 258
    //   310: lload #6
    //   312: lconst_0
    //   313: lcmp
    //   314: ifne -> 323
    //   317: getstatic java/lang/System.err : Ljava/io/PrintStream;
    //   320: invokevirtual println : ()V
    //   323: lload_2
    //   324: lload #6
    //   326: lcmp
    //   327: ifeq -> 338
    //   330: new java/lang/AssertionError
    //   333: dup
    //   334: invokespecial <init> : ()V
    //   337: athrow
    //   338: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   341: new java/lang/StringBuilder
    //   344: dup
    //   345: invokespecial <init> : ()V
    //   348: ldc_w 'Double loop (with additional index): '
    //   351: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   354: lload #8
    //   356: invokestatic currentTimeMillis : ()J
    //   359: ladd
    //   360: invokevirtual append : (J)Ljava/lang/StringBuilder;
    //   363: ldc_w 'ms'
    //   366: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   369: invokevirtual toString : ()Ljava/lang/String;
    //   372: invokevirtual println : (Ljava/lang/String;)V
    //   375: goto -> 16
    //   378: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #5460	-> 0
    //   #5462	-> 12
    //   #5463	-> 24
    //   #5464	-> 30
    //   #5465	-> 32
    //   #5466	-> 50
    //   #5467	-> 66
    //   #5468	-> 78
    //   #5469	-> 115
    //   #5470	-> 121
    //   #5471	-> 124
    //   #5472	-> 136
    //   #5473	-> 142
    //   #5474	-> 155
    //   #5475	-> 177
    //   #5476	-> 180
    //   #5477	-> 193
    //   #5478	-> 208
    //   #5479	-> 245
    //   #5480	-> 248
    //   #5481	-> 254
    //   #5482	-> 266
    //   #5483	-> 272
    //   #5484	-> 285
    //   #5485	-> 307
    //   #5486	-> 310
    //   #5487	-> 323
    //   #5488	-> 338
    //   #5489	-> 375
    //   #5490	-> 378
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   38	28	11	i	J
    //   147	30	13	d	I
    //   142	35	12	t	[I
    //   128	52	11	i	I
    //   277	30	15	d	I
    //   272	35	14	t	[I
    //   258	52	13	i	I
    //   254	121	11	j	J
    //   32	346	2	x	J
    //   124	254	4	y	J
    //   248	130	6	z	J
    //   30	348	8	start	J
    //   16	362	10	k	I
    //   0	379	0	arg	[Ljava/lang/String;
    //   12	367	1	a	[[I
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\BigArrays.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */