package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import us.myles.viaversion.libs.fastutil.Arrays;
import us.myles.viaversion.libs.fastutil.Hash;
import us.myles.viaversion.libs.fastutil.ints.IntArrays;

public final class ObjectArrays {
  public static final Object[] EMPTY_ARRAY = new Object[0];
  
  public static final Object[] DEFAULT_EMPTY_ARRAY = new Object[0];
  
  private static final int QUICKSORT_NO_REC = 16;
  
  private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
  
  private static final int QUICKSORT_MEDIAN_OF_9 = 128;
  
  private static final int MERGESORT_NO_REC = 16;
  
  private static <K> K[] newArray(K[] prototype, int length) {
    Class<?> klass = prototype.getClass();
    if (klass == Object[].class)
      return (length == 0) ? (K[])EMPTY_ARRAY : (K[])new Object[length]; 
    return (K[])Array.newInstance(klass.getComponentType(), length);
  }
  
  public static <K> K[] forceCapacity(K[] array, int length, int preserve) {
    K[] t = newArray(array, length);
    System.arraycopy(array, 0, t, 0, preserve);
    return t;
  }
  
  public static <K> K[] ensureCapacity(K[] array, int length) {
    return ensureCapacity(array, length, array.length);
  }
  
  public static <K> K[] ensureCapacity(K[] array, int length, int preserve) {
    return (length > array.length) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static <K> K[] grow(K[] array, int length) {
    return grow(array, length, array.length);
  }
  
  public static <K> K[] grow(K[] array, int length, int preserve) {
    if (length > array.length) {
      int newLength = (int)Math.max(Math.min(array.length + (array.length >> 1), 2147483639L), length);
      K[] t = newArray(array, newLength);
      System.arraycopy(array, 0, t, 0, preserve);
      return t;
    } 
    return array;
  }
  
  public static <K> K[] trim(K[] array, int length) {
    if (length >= array.length)
      return array; 
    K[] t = newArray(array, length);
    System.arraycopy(array, 0, t, 0, length);
    return t;
  }
  
  public static <K> K[] setLength(K[] array, int length) {
    if (length == array.length)
      return array; 
    if (length < array.length)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static <K> K[] copy(K[] array, int offset, int length) {
    ensureOffsetLength(array, offset, length);
    K[] a = newArray(array, length);
    System.arraycopy(array, offset, a, 0, length);
    return a;
  }
  
  public static <K> K[] copy(K[] array) {
    return (K[])array.clone();
  }
  
  @Deprecated
  public static <K> void fill(K[] array, K value) {
    int i = array.length;
    while (i-- != 0)
      array[i] = value; 
  }
  
  @Deprecated
  public static <K> void fill(K[] array, int from, int to, K value) {
    ensureFromTo(array, from, to);
    if (from == 0) {
      while (to-- != 0)
        array[to] = value; 
    } else {
      for (int i = from; i < to; i++)
        array[i] = value; 
    } 
  }
  
  @Deprecated
  public static <K> boolean equals(K[] a1, K[] a2) {
    int i = a1.length;
    if (i != a2.length)
      return false; 
    while (i-- != 0) {
      if (!Objects.equals(a1[i], a2[i]))
        return false; 
    } 
    return true;
  }
  
  public static <K> void ensureFromTo(K[] a, int from, int to) {
    Arrays.ensureFromTo(a.length, from, to);
  }
  
  public static <K> void ensureOffsetLength(K[] a, int offset, int length) {
    Arrays.ensureOffsetLength(a.length, offset, length);
  }
  
  public static <K> void ensureSameLength(K[] a, K[] b) {
    if (a.length != b.length)
      throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length); 
  }
  
  public static <K> void swap(K[] x, int a, int b) {
    K t = x[a];
    x[a] = x[b];
    x[b] = t;
  }
  
  public static <K> void swap(K[] x, int a, int b, int n) {
    for (int i = 0; i < n; i++, a++, b++)
      swap(x, a, b); 
  }
  
  private static <K> int med3(K[] x, int a, int b, int c, Comparator<K> comp) {
    int ab = comp.compare(x[a], x[b]);
    int ac = comp.compare(x[a], x[c]);
    int bc = comp.compare(x[b], x[c]);
    return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
  }
  
  private static <K> void selectionSort(K[] a, int from, int to, Comparator<K> comp) {
    for (int i = from; i < to - 1; i++) {
      int m = i;
      for (int j = i + 1; j < to; j++) {
        if (comp.compare(a[j], a[m]) < 0)
          m = j; 
      } 
      if (m != i) {
        K u = a[i];
        a[i] = a[m];
        a[m] = u;
      } 
    } 
  }
  
  private static <K> void insertionSort(K[] a, int from, int to, Comparator<K> comp) {
    for (int i = from; ++i < to; ) {
      K t = a[i];
      int j = i;
      for (K u = a[j - 1]; comp.compare(t, u) < 0; u = a[--j - 1]) {
        a[j] = u;
        if (from == j - 1) {
          j--;
          break;
        } 
      } 
      a[j] = t;
    } 
  }
  
  public static <K> void quickSort(K[] x, int from, int to, Comparator<K> comp) {
    int len = to - from;
    if (len < 16) {
      selectionSort(x, from, to, comp);
      return;
    } 
    int m = from + len / 2;
    int l = from;
    int n = to - 1;
    if (len > 128) {
      int i = len / 8;
      l = med3(x, l, l + i, l + 2 * i, comp);
      m = med3(x, m - i, m, m + i, comp);
      n = med3(x, n - 2 * i, n - i, n, comp);
    } 
    m = med3(x, l, m, n, comp);
    K v = x[m];
    int a = from, b = a, c = to - 1, d = c;
    while (true) {
      int comparison;
      if (b <= c && (comparison = comp.compare(x[b], v)) <= 0) {
        if (comparison == 0)
          swap(x, a++, b); 
        b++;
        continue;
      } 
      while (c >= b && (comparison = comp.compare(x[c], v)) >= 0) {
        if (comparison == 0)
          swap(x, c, d--); 
        c--;
      } 
      if (b > c)
        break; 
      swap(x, b++, c--);
    } 
    int s = Math.min(a - from, b - a);
    swap(x, from, b - s, s);
    s = Math.min(d - c, to - d - 1);
    swap(x, b, to - s, s);
    if ((s = b - a) > 1)
      quickSort(x, from, from + s, comp); 
    if ((s = d - c) > 1)
      quickSort(x, to - s, to, comp); 
  }
  
  public static <K> void quickSort(K[] x, Comparator<K> comp) {
    quickSort(x, 0, x.length, comp);
  }
  
  protected static class ForkJoinQuickSortComp<K> extends RecursiveAction {
    private static final long serialVersionUID = 1L;
    
    private final int from;
    
    private final int to;
    
    private final K[] x;
    
    private final Comparator<K> comp;
    
    public ForkJoinQuickSortComp(K[] x, int from, int to, Comparator<K> comp) {
      this.from = from;
      this.to = to;
      this.x = x;
      this.comp = comp;
    }
    
    protected void compute() {
      K[] x = this.x;
      int len = this.to - this.from;
      if (len < 8192) {
        ObjectArrays.quickSort(x, this.from, this.to, this.comp);
        return;
      } 
      int m = this.from + len / 2;
      int l = this.from;
      int n = this.to - 1;
      int s = len / 8;
      l = ObjectArrays.med3(x, l, l + s, l + 2 * s, this.comp);
      m = ObjectArrays.med3(x, m - s, m, m + s, this.comp);
      n = ObjectArrays.med3(x, n - 2 * s, n - s, n, this.comp);
      m = ObjectArrays.med3(x, l, m, n, this.comp);
      K v = x[m];
      int a = this.from, b = a, c = this.to - 1, d = c;
      while (true) {
        int comparison;
        if (b <= c && (comparison = this.comp.compare(x[b], v)) <= 0) {
          if (comparison == 0)
            ObjectArrays.swap(x, a++, b); 
          b++;
          continue;
        } 
        while (c >= b && (comparison = this.comp.compare(x[c], v)) >= 0) {
          if (comparison == 0)
            ObjectArrays.swap(x, c, d--); 
          c--;
        } 
        if (b > c)
          break; 
        ObjectArrays.swap(x, b++, c--);
      } 
      s = Math.min(a - this.from, b - a);
      ObjectArrays.swap(x, this.from, b - s, s);
      s = Math.min(d - c, this.to - d - 1);
      ObjectArrays.swap(x, b, this.to - s, s);
      s = b - a;
      int t = d - c;
      if (s > 1 && t > 1) {
        invokeAll(new ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp), new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
      } else if (s > 1) {
        invokeAll((ForkJoinTask<?>[])new ForkJoinTask[] { new ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp) });
      } else {
        invokeAll((ForkJoinTask<?>[])new ForkJoinTask[] { new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp) });
      } 
    }
  }
  
  public static <K> void parallelQuickSort(K[] x, int from, int to, Comparator<K> comp) {
    if (to - from < 8192) {
      quickSort(x, from, to, comp);
    } else {
      ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
      pool.invoke(new ForkJoinQuickSortComp<>(x, from, to, comp));
      pool.shutdown();
    } 
  }
  
  public static <K> void parallelQuickSort(K[] x, Comparator<K> comp) {
    parallelQuickSort(x, 0, x.length, comp);
  }
  
  private static <K> int med3(K[] x, int a, int b, int c) {
    int ab = ((Comparable<K>)x[a]).compareTo(x[b]);
    int ac = ((Comparable<K>)x[a]).compareTo(x[c]);
    int bc = ((Comparable<K>)x[b]).compareTo(x[c]);
    return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
  }
  
  private static <K> void selectionSort(K[] a, int from, int to) {
    for (int i = from; i < to - 1; i++) {
      int m = i;
      for (int j = i + 1; j < to; j++) {
        if (((Comparable<K>)a[j]).compareTo(a[m]) < 0)
          m = j; 
      } 
      if (m != i) {
        K u = a[i];
        a[i] = a[m];
        a[m] = u;
      } 
    } 
  }
  
  private static <K> void insertionSort(K[] a, int from, int to) {
    for (int i = from; ++i < to; ) {
      K t = a[i];
      int j = i;
      for (K u = a[j - 1]; ((Comparable<K>)t).compareTo(u) < 0; u = a[--j - 1]) {
        a[j] = u;
        if (from == j - 1) {
          j--;
          break;
        } 
      } 
      a[j] = t;
    } 
  }
  
  public static <K> void quickSort(K[] x, int from, int to) {
    int len = to - from;
    if (len < 16) {
      selectionSort(x, from, to);
      return;
    } 
    int m = from + len / 2;
    int l = from;
    int n = to - 1;
    if (len > 128) {
      int i = len / 8;
      l = med3(x, l, l + i, l + 2 * i);
      m = med3(x, m - i, m, m + i);
      n = med3(x, n - 2 * i, n - i, n);
    } 
    m = med3(x, l, m, n);
    K v = x[m];
    int a = from, b = a, c = to - 1, d = c;
    while (true) {
      int comparison;
      if (b <= c && (comparison = ((Comparable<K>)x[b]).compareTo(v)) <= 0) {
        if (comparison == 0)
          swap(x, a++, b); 
        b++;
        continue;
      } 
      while (c >= b && (comparison = ((Comparable<K>)x[c]).compareTo(v)) >= 0) {
        if (comparison == 0)
          swap(x, c, d--); 
        c--;
      } 
      if (b > c)
        break; 
      swap(x, b++, c--);
    } 
    int s = Math.min(a - from, b - a);
    swap(x, from, b - s, s);
    s = Math.min(d - c, to - d - 1);
    swap(x, b, to - s, s);
    if ((s = b - a) > 1)
      quickSort(x, from, from + s); 
    if ((s = d - c) > 1)
      quickSort(x, to - s, to); 
  }
  
  public static <K> void quickSort(K[] x) {
    quickSort(x, 0, x.length);
  }
  
  protected static class ForkJoinQuickSort<K> extends RecursiveAction {
    private static final long serialVersionUID = 1L;
    
    private final int from;
    
    private final int to;
    
    private final K[] x;
    
    public ForkJoinQuickSort(K[] x, int from, int to) {
      this.from = from;
      this.to = to;
      this.x = x;
    }
    
    protected void compute() {
      K[] x = this.x;
      int len = this.to - this.from;
      if (len < 8192) {
        ObjectArrays.quickSort(x, this.from, this.to);
        return;
      } 
      int m = this.from + len / 2;
      int l = this.from;
      int n = this.to - 1;
      int s = len / 8;
      l = ObjectArrays.med3(x, l, l + s, l + 2 * s);
      m = ObjectArrays.med3(x, m - s, m, m + s);
      n = ObjectArrays.med3(x, n - 2 * s, n - s, n);
      m = ObjectArrays.med3(x, l, m, n);
      K v = x[m];
      int a = this.from, b = a, c = this.to - 1, d = c;
      while (true) {
        int comparison;
        if (b <= c && (comparison = ((Comparable<K>)x[b]).compareTo(v)) <= 0) {
          if (comparison == 0)
            ObjectArrays.swap(x, a++, b); 
          b++;
          continue;
        } 
        while (c >= b && (comparison = ((Comparable<K>)x[c]).compareTo(v)) >= 0) {
          if (comparison == 0)
            ObjectArrays.swap(x, c, d--); 
          c--;
        } 
        if (b > c)
          break; 
        ObjectArrays.swap(x, b++, c--);
      } 
      s = Math.min(a - this.from, b - a);
      ObjectArrays.swap(x, this.from, b - s, s);
      s = Math.min(d - c, this.to - d - 1);
      ObjectArrays.swap(x, b, this.to - s, s);
      s = b - a;
      int t = d - c;
      if (s > 1 && t > 1) {
        invokeAll(new ForkJoinQuickSort(x, this.from, this.from + s), new ForkJoinQuickSort(x, this.to - t, this.to));
      } else if (s > 1) {
        invokeAll((ForkJoinTask<?>[])new ForkJoinTask[] { new ForkJoinQuickSort(x, this.from, this.from + s) });
      } else {
        invokeAll((ForkJoinTask<?>[])new ForkJoinTask[] { new ForkJoinQuickSort(x, this.to - t, this.to) });
      } 
    }
  }
  
  public static <K> void parallelQuickSort(K[] x, int from, int to) {
    if (to - from < 8192) {
      quickSort(x, from, to);
    } else {
      ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
      pool.invoke(new ForkJoinQuickSort<>(x, from, to));
      pool.shutdown();
    } 
  }
  
  public static <K> void parallelQuickSort(K[] x) {
    parallelQuickSort(x, 0, x.length);
  }
  
  private static <K> int med3Indirect(int[] perm, K[] x, int a, int b, int c) {
    K aa = x[perm[a]];
    K bb = x[perm[b]];
    K cc = x[perm[c]];
    int ab = ((Comparable<K>)aa).compareTo(bb);
    int ac = ((Comparable<K>)aa).compareTo(cc);
    int bc = ((Comparable<K>)bb).compareTo(cc);
    return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
  }
  
  private static <K> void insertionSortIndirect(int[] perm, K[] a, int from, int to) {
    for (int i = from; ++i < to; ) {
      int t = perm[i];
      int j = i;
      int u;
      for (u = perm[j - 1]; ((Comparable<K>)a[t]).compareTo(a[u]) < 0; u = perm[--j - 1]) {
        perm[j] = u;
        if (from == j - 1) {
          j--;
          break;
        } 
      } 
      perm[j] = t;
    } 
  }
  
  public static <K> void quickSortIndirect(int[] perm, K[] x, int from, int to) {
    int len = to - from;
    if (len < 16) {
      insertionSortIndirect(perm, x, from, to);
      return;
    } 
    int m = from + len / 2;
    int l = from;
    int n = to - 1;
    if (len > 128) {
      int i = len / 8;
      l = med3Indirect(perm, x, l, l + i, l + 2 * i);
      m = med3Indirect(perm, x, m - i, m, m + i);
      n = med3Indirect(perm, x, n - 2 * i, n - i, n);
    } 
    m = med3Indirect(perm, x, l, m, n);
    K v = x[perm[m]];
    int a = from, b = a, c = to - 1, d = c;
    while (true) {
      int comparison;
      if (b <= c && (comparison = ((Comparable<K>)x[perm[b]]).compareTo(v)) <= 0) {
        if (comparison == 0)
          IntArrays.swap(perm, a++, b); 
        b++;
        continue;
      } 
      while (c >= b && (comparison = ((Comparable<K>)x[perm[c]]).compareTo(v)) >= 0) {
        if (comparison == 0)
          IntArrays.swap(perm, c, d--); 
        c--;
      } 
      if (b > c)
        break; 
      IntArrays.swap(perm, b++, c--);
    } 
    int s = Math.min(a - from, b - a);
    IntArrays.swap(perm, from, b - s, s);
    s = Math.min(d - c, to - d - 1);
    IntArrays.swap(perm, b, to - s, s);
    if ((s = b - a) > 1)
      quickSortIndirect(perm, x, from, from + s); 
    if ((s = d - c) > 1)
      quickSortIndirect(perm, x, to - s, to); 
  }
  
  public static <K> void quickSortIndirect(int[] perm, K[] x) {
    quickSortIndirect(perm, x, 0, x.length);
  }
  
  protected static class ForkJoinQuickSortIndirect<K> extends RecursiveAction {
    private static final long serialVersionUID = 1L;
    
    private final int from;
    
    private final int to;
    
    private final int[] perm;
    
    private final K[] x;
    
    public ForkJoinQuickSortIndirect(int[] perm, K[] x, int from, int to) {
      this.from = from;
      this.to = to;
      this.x = x;
      this.perm = perm;
    }
    
    protected void compute() {
      K[] x = this.x;
      int len = this.to - this.from;
      if (len < 8192) {
        ObjectArrays.quickSortIndirect(this.perm, x, this.from, this.to);
        return;
      } 
      int m = this.from + len / 2;
      int l = this.from;
      int n = this.to - 1;
      int s = len / 8;
      l = ObjectArrays.med3Indirect(this.perm, x, l, l + s, l + 2 * s);
      m = ObjectArrays.med3Indirect(this.perm, x, m - s, m, m + s);
      n = ObjectArrays.med3Indirect(this.perm, x, n - 2 * s, n - s, n);
      m = ObjectArrays.med3Indirect(this.perm, x, l, m, n);
      K v = x[this.perm[m]];
      int a = this.from, b = a, c = this.to - 1, d = c;
      while (true) {
        int comparison;
        if (b <= c && (comparison = ((Comparable<K>)x[this.perm[b]]).compareTo(v)) <= 0) {
          if (comparison == 0)
            IntArrays.swap(this.perm, a++, b); 
          b++;
          continue;
        } 
        while (c >= b && (comparison = ((Comparable<K>)x[this.perm[c]]).compareTo(v)) >= 0) {
          if (comparison == 0)
            IntArrays.swap(this.perm, c, d--); 
          c--;
        } 
        if (b > c)
          break; 
        IntArrays.swap(this.perm, b++, c--);
      } 
      s = Math.min(a - this.from, b - a);
      IntArrays.swap(this.perm, this.from, b - s, s);
      s = Math.min(d - c, this.to - d - 1);
      IntArrays.swap(this.perm, b, this.to - s, s);
      s = b - a;
      int t = d - c;
      if (s > 1 && t > 1) {
        invokeAll(new ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s), new ForkJoinQuickSortIndirect(this.perm, x, this.to - t, this.to));
      } else if (s > 1) {
        invokeAll((ForkJoinTask<?>[])new ForkJoinTask[] { new ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s) });
      } else {
        invokeAll((ForkJoinTask<?>[])new ForkJoinTask[] { new ForkJoinQuickSortIndirect(this.perm, x, this.to - t, this.to) });
      } 
    }
  }
  
  public static <K> void parallelQuickSortIndirect(int[] perm, K[] x, int from, int to) {
    if (to - from < 8192) {
      quickSortIndirect(perm, x, from, to);
    } else {
      ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
      pool.invoke(new ForkJoinQuickSortIndirect<>(perm, x, from, to));
      pool.shutdown();
    } 
  }
  
  public static <K> void parallelQuickSortIndirect(int[] perm, K[] x) {
    parallelQuickSortIndirect(perm, x, 0, x.length);
  }
  
  public static <K> void stabilize(int[] perm, K[] x, int from, int to) {
    int curr = from;
    for (int i = from + 1; i < to; i++) {
      if (x[perm[i]] != x[perm[curr]]) {
        if (i - curr > 1)
          IntArrays.parallelQuickSort(perm, curr, i); 
        curr = i;
      } 
    } 
    if (to - curr > 1)
      IntArrays.parallelQuickSort(perm, curr, to); 
  }
  
  public static <K> void stabilize(int[] perm, K[] x) {
    stabilize(perm, x, 0, perm.length);
  }
  
  private static <K> int med3(K[] x, K[] y, int a, int b, int c) {
    int t, ab = ((t = ((Comparable<K>)x[a]).compareTo(x[b])) == 0) ? ((Comparable<K>)y[a]).compareTo(y[b]) : t;
    int ac = ((t = ((Comparable<K>)x[a]).compareTo(x[c])) == 0) ? ((Comparable<K>)y[a]).compareTo(y[c]) : t;
    int bc = ((t = ((Comparable<K>)x[b]).compareTo(x[c])) == 0) ? ((Comparable<K>)y[b]).compareTo(y[c]) : t;
    return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
  }
  
  private static <K> void swap(K[] x, K[] y, int a, int b) {
    K t = x[a];
    K u = y[a];
    x[a] = x[b];
    y[a] = y[b];
    x[b] = t;
    y[b] = u;
  }
  
  private static <K> void swap(K[] x, K[] y, int a, int b, int n) {
    for (int i = 0; i < n; i++, a++, b++)
      swap(x, y, a, b); 
  }
  
  private static <K> void selectionSort(K[] a, K[] b, int from, int to) {
    for (int i = from; i < to - 1; i++) {
      int m = i;
      for (int j = i + 1; j < to; j++) {
        int u;
        if ((u = ((Comparable<K>)a[j]).compareTo(a[m])) < 0 || (u == 0 && ((Comparable<K>)b[j])
          .compareTo(b[m]) < 0))
          m = j; 
      } 
      if (m != i) {
        K t = a[i];
        a[i] = a[m];
        a[m] = t;
        t = b[i];
        b[i] = b[m];
        b[m] = t;
      } 
    } 
  }
  
  public static <K> void quickSort(K[] x, K[] y, int from, int to) {
    int len = to - from;
    if (len < 16) {
      selectionSort(x, y, from, to);
      return;
    } 
    int m = from + len / 2;
    int l = from;
    int n = to - 1;
    if (len > 128) {
      int i = len / 8;
      l = med3(x, y, l, l + i, l + 2 * i);
      m = med3(x, y, m - i, m, m + i);
      n = med3(x, y, n - 2 * i, n - i, n);
    } 
    m = med3(x, y, l, m, n);
    K v = x[m], w = y[m];
    int a = from, b = a, c = to - 1, d = c;
    while (true) {
      if (b <= c) {
        int comparison;
        int t;
        if ((comparison = ((t = ((Comparable<K>)x[b]).compareTo(v)) == 0) ? ((Comparable<K>)y[b]).compareTo(w) : t) <= 0) {
          if (comparison == 0)
            swap(x, y, a++, b); 
          b++;
          continue;
        } 
      } 
      while (c >= b) {
        int comparison;
        int t;
        if ((comparison = ((t = ((Comparable<K>)x[c]).compareTo(v)) == 0) ? ((Comparable<K>)y[c]).compareTo(w) : t) >= 0) {
          if (comparison == 0)
            swap(x, y, c, d--); 
          c--;
        } 
      } 
      if (b > c)
        break; 
      swap(x, y, b++, c--);
    } 
    int s = Math.min(a - from, b - a);
    swap(x, y, from, b - s, s);
    s = Math.min(d - c, to - d - 1);
    swap(x, y, b, to - s, s);
    if ((s = b - a) > 1)
      quickSort(x, y, from, from + s); 
    if ((s = d - c) > 1)
      quickSort(x, y, to - s, to); 
  }
  
  public static <K> void quickSort(K[] x, K[] y) {
    ensureSameLength(x, y);
    quickSort(x, y, 0, x.length);
  }
  
  protected static class ForkJoinQuickSort2<K> extends RecursiveAction {
    private static final long serialVersionUID = 1L;
    
    private final int from;
    
    private final int to;
    
    private final K[] x;
    
    private final K[] y;
    
    public ForkJoinQuickSort2(K[] x, K[] y, int from, int to) {
      this.from = from;
      this.to = to;
      this.x = x;
      this.y = y;
    }
    
    protected void compute() {
      K[] x = this.x;
      K[] y = this.y;
      int len = this.to - this.from;
      if (len < 8192) {
        ObjectArrays.quickSort(x, y, this.from, this.to);
        return;
      } 
      int m = this.from + len / 2;
      int l = this.from;
      int n = this.to - 1;
      int s = len / 8;
      l = ObjectArrays.med3(x, y, l, l + s, l + 2 * s);
      m = ObjectArrays.med3(x, y, m - s, m, m + s);
      n = ObjectArrays.med3(x, y, n - 2 * s, n - s, n);
      m = ObjectArrays.med3(x, y, l, m, n);
      K v = x[m], w = y[m];
      int a = this.from, b = a, c = this.to - 1, d = c;
      while (true) {
        if (b <= c) {
          int comparison;
          int i;
          if ((comparison = ((i = ((Comparable<K>)x[b]).compareTo(v)) == 0) ? ((Comparable<K>)y[b]).compareTo(w) : i) <= 0) {
            if (comparison == 0)
              ObjectArrays.swap(x, y, a++, b); 
            b++;
            continue;
          } 
        } 
        while (c >= b) {
          int comparison;
          int i;
          if ((comparison = ((i = ((Comparable<K>)x[c]).compareTo(v)) == 0) ? ((Comparable<K>)y[c]).compareTo(w) : i) >= 0) {
            if (comparison == 0)
              ObjectArrays.swap(x, y, c, d--); 
            c--;
          } 
        } 
        if (b > c)
          break; 
        ObjectArrays.swap(x, y, b++, c--);
      } 
      s = Math.min(a - this.from, b - a);
      ObjectArrays.swap(x, y, this.from, b - s, s);
      s = Math.min(d - c, this.to - d - 1);
      ObjectArrays.swap(x, y, b, this.to - s, s);
      s = b - a;
      int t = d - c;
      if (s > 1 && t > 1) {
        invokeAll(new ForkJoinQuickSort2(x, y, this.from, this.from + s), new ForkJoinQuickSort2(x, y, this.to - t, this.to));
      } else if (s > 1) {
        invokeAll((ForkJoinTask<?>[])new ForkJoinTask[] { new ForkJoinQuickSort2(x, y, this.from, this.from + s) });
      } else {
        invokeAll((ForkJoinTask<?>[])new ForkJoinTask[] { new ForkJoinQuickSort2(x, y, this.to - t, this.to) });
      } 
    }
  }
  
  public static <K> void parallelQuickSort(K[] x, K[] y, int from, int to) {
    if (to - from < 8192)
      quickSort(x, y, from, to); 
    ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    pool.invoke(new ForkJoinQuickSort2<>(x, y, from, to));
    pool.shutdown();
  }
  
  public static <K> void parallelQuickSort(K[] x, K[] y) {
    ensureSameLength(x, y);
    parallelQuickSort(x, y, 0, x.length);
  }
  
  public static <K> void unstableSort(K[] a, int from, int to) {
    quickSort(a, from, to);
  }
  
  public static <K> void unstableSort(K[] a) {
    unstableSort(a, 0, a.length);
  }
  
  public static <K> void unstableSort(K[] a, int from, int to, Comparator<K> comp) {
    quickSort(a, from, to, comp);
  }
  
  public static <K> void unstableSort(K[] a, Comparator<K> comp) {
    unstableSort(a, 0, a.length, comp);
  }
  
  public static <K> void mergeSort(K[] a, int from, int to, K[] supp) {
    int len = to - from;
    if (len < 16) {
      insertionSort(a, from, to);
      return;
    } 
    int mid = from + to >>> 1;
    mergeSort(supp, from, mid, a);
    mergeSort(supp, mid, to, a);
    if (((Comparable<K>)supp[mid - 1]).compareTo(supp[mid]) <= 0) {
      System.arraycopy(supp, from, a, from, len);
      return;
    } 
    for (int i = from, p = from, q = mid; i < to; i++) {
      if (q >= to || (p < mid && ((Comparable<K>)supp[p]).compareTo(supp[q]) <= 0)) {
        a[i] = supp[p++];
      } else {
        a[i] = supp[q++];
      } 
    } 
  }
  
  public static <K> void mergeSort(K[] a, int from, int to) {
    mergeSort(a, from, to, (K[])a.clone());
  }
  
  public static <K> void mergeSort(K[] a) {
    mergeSort(a, 0, a.length);
  }
  
  public static <K> void mergeSort(K[] a, int from, int to, Comparator<K> comp, K[] supp) {
    int len = to - from;
    if (len < 16) {
      insertionSort(a, from, to, comp);
      return;
    } 
    int mid = from + to >>> 1;
    mergeSort(supp, from, mid, comp, a);
    mergeSort(supp, mid, to, comp, a);
    if (comp.compare(supp[mid - 1], supp[mid]) <= 0) {
      System.arraycopy(supp, from, a, from, len);
      return;
    } 
    for (int i = from, p = from, q = mid; i < to; i++) {
      if (q >= to || (p < mid && comp.compare(supp[p], supp[q]) <= 0)) {
        a[i] = supp[p++];
      } else {
        a[i] = supp[q++];
      } 
    } 
  }
  
  public static <K> void mergeSort(K[] a, int from, int to, Comparator<K> comp) {
    mergeSort(a, from, to, comp, (K[])a.clone());
  }
  
  public static <K> void mergeSort(K[] a, Comparator<K> comp) {
    mergeSort(a, 0, a.length, comp);
  }
  
  public static <K> void stableSort(K[] a, int from, int to) {
    Arrays.sort((Object[])a, from, to);
  }
  
  public static <K> void stableSort(K[] a) {
    stableSort(a, 0, a.length);
  }
  
  public static <K> void stableSort(K[] a, int from, int to, Comparator<K> comp) {
    Arrays.sort(a, from, to, comp);
  }
  
  public static <K> void stableSort(K[] a, Comparator<K> comp) {
    stableSort(a, 0, a.length, comp);
  }
  
  public static <K> int binarySearch(K[] a, int from, int to, K key) {
    to--;
    while (from <= to) {
      int mid = from + to >>> 1;
      K midVal = a[mid];
      int cmp = ((Comparable<K>)midVal).compareTo(key);
      if (cmp < 0) {
        from = mid + 1;
        continue;
      } 
      if (cmp > 0) {
        to = mid - 1;
        continue;
      } 
      return mid;
    } 
    return -(from + 1);
  }
  
  public static <K> int binarySearch(K[] a, K key) {
    return binarySearch(a, 0, a.length, key);
  }
  
  public static <K> int binarySearch(K[] a, int from, int to, K key, Comparator<K> c) {
    to--;
    while (from <= to) {
      int mid = from + to >>> 1;
      K midVal = a[mid];
      int cmp = c.compare(midVal, key);
      if (cmp < 0) {
        from = mid + 1;
        continue;
      } 
      if (cmp > 0) {
        to = mid - 1;
        continue;
      } 
      return mid;
    } 
    return -(from + 1);
  }
  
  public static <K> int binarySearch(K[] a, K key, Comparator<K> c) {
    return binarySearch(a, 0, a.length, key, c);
  }
  
  public static <K> K[] shuffle(K[] a, int from, int to, Random random) {
    for (int i = to - from; i-- != 0; ) {
      int p = random.nextInt(i + 1);
      K t = a[from + i];
      a[from + i] = a[from + p];
      a[from + p] = t;
    } 
    return a;
  }
  
  public static <K> K[] shuffle(K[] a, Random random) {
    for (int i = a.length; i-- != 0; ) {
      int p = random.nextInt(i + 1);
      K t = a[i];
      a[i] = a[p];
      a[p] = t;
    } 
    return a;
  }
  
  public static <K> K[] reverse(K[] a) {
    int length = a.length;
    for (int i = length / 2; i-- != 0; ) {
      K t = a[length - i - 1];
      a[length - i - 1] = a[i];
      a[i] = t;
    } 
    return a;
  }
  
  public static <K> K[] reverse(K[] a, int from, int to) {
    int length = to - from;
    for (int i = length / 2; i-- != 0; ) {
      K t = a[from + length - i - 1];
      a[from + length - i - 1] = a[from + i];
      a[from + i] = t;
    } 
    return a;
  }
  
  private static final class ArrayHashStrategy<K> implements Hash.Strategy<K[]>, Serializable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    private ArrayHashStrategy() {}
    
    public int hashCode(K[] o) {
      return Arrays.hashCode((Object[])o);
    }
    
    public boolean equals(K[] a, K[] b) {
      return Arrays.equals((Object[])a, (Object[])b);
    }
  }
  
  public static final Hash.Strategy HASH_STRATEGY = new ArrayHashStrategy();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectArrays.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */