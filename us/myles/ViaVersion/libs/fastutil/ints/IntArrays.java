package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import us.myles.viaversion.libs.fastutil.Arrays;
import us.myles.viaversion.libs.fastutil.Hash;

public final class IntArrays {
  public static final int[] EMPTY_ARRAY = new int[0];
  
  public static final int[] DEFAULT_EMPTY_ARRAY = new int[0];
  
  private static final int QUICKSORT_NO_REC = 16;
  
  private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
  
  private static final int QUICKSORT_MEDIAN_OF_9 = 128;
  
  private static final int MERGESORT_NO_REC = 16;
  
  private static final int DIGIT_BITS = 8;
  
  private static final int DIGIT_MASK = 255;
  
  private static final int DIGITS_PER_ELEMENT = 4;
  
  private static final int RADIXSORT_NO_REC = 1024;
  
  private static final int PARALLEL_RADIXSORT_NO_FORK = 1024;
  
  static final int RADIX_SORT_MIN_THRESHOLD = 2000;
  
  public static int[] forceCapacity(int[] array, int length, int preserve) {
    int[] t = new int[length];
    System.arraycopy(array, 0, t, 0, preserve);
    return t;
  }
  
  public static int[] ensureCapacity(int[] array, int length) {
    return ensureCapacity(array, length, array.length);
  }
  
  public static int[] ensureCapacity(int[] array, int length, int preserve) {
    return (length > array.length) ? forceCapacity(array, length, preserve) : array;
  }
  
  public static int[] grow(int[] array, int length) {
    return grow(array, length, array.length);
  }
  
  public static int[] grow(int[] array, int length, int preserve) {
    if (length > array.length) {
      int newLength = (int)Math.max(Math.min(array.length + (array.length >> 1), 2147483639L), length);
      int[] t = new int[newLength];
      System.arraycopy(array, 0, t, 0, preserve);
      return t;
    } 
    return array;
  }
  
  public static int[] trim(int[] array, int length) {
    if (length >= array.length)
      return array; 
    int[] t = (length == 0) ? EMPTY_ARRAY : new int[length];
    System.arraycopy(array, 0, t, 0, length);
    return t;
  }
  
  public static int[] setLength(int[] array, int length) {
    if (length == array.length)
      return array; 
    if (length < array.length)
      return trim(array, length); 
    return ensureCapacity(array, length);
  }
  
  public static int[] copy(int[] array, int offset, int length) {
    ensureOffsetLength(array, offset, length);
    int[] a = (length == 0) ? EMPTY_ARRAY : new int[length];
    System.arraycopy(array, offset, a, 0, length);
    return a;
  }
  
  public static int[] copy(int[] array) {
    return (int[])array.clone();
  }
  
  @Deprecated
  public static void fill(int[] array, int value) {
    int i = array.length;
    while (i-- != 0)
      array[i] = value; 
  }
  
  @Deprecated
  public static void fill(int[] array, int from, int to, int value) {
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
  public static boolean equals(int[] a1, int[] a2) {
    int i = a1.length;
    if (i != a2.length)
      return false; 
    while (i-- != 0) {
      if (a1[i] != a2[i])
        return false; 
    } 
    return true;
  }
  
  public static void ensureFromTo(int[] a, int from, int to) {
    Arrays.ensureFromTo(a.length, from, to);
  }
  
  public static void ensureOffsetLength(int[] a, int offset, int length) {
    Arrays.ensureOffsetLength(a.length, offset, length);
  }
  
  public static void ensureSameLength(int[] a, int[] b) {
    if (a.length != b.length)
      throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length); 
  }
  
  public static void swap(int[] x, int a, int b) {
    int t = x[a];
    x[a] = x[b];
    x[b] = t;
  }
  
  public static void swap(int[] x, int a, int b, int n) {
    for (int i = 0; i < n; i++, a++, b++)
      swap(x, a, b); 
  }
  
  private static int med3(int[] x, int a, int b, int c, IntComparator comp) {
    int ab = comp.compare(x[a], x[b]);
    int ac = comp.compare(x[a], x[c]);
    int bc = comp.compare(x[b], x[c]);
    return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
  }
  
  private static void selectionSort(int[] a, int from, int to, IntComparator comp) {
    for (int i = from; i < to - 1; i++) {
      int m = i;
      for (int j = i + 1; j < to; j++) {
        if (comp.compare(a[j], a[m]) < 0)
          m = j; 
      } 
      if (m != i) {
        int u = a[i];
        a[i] = a[m];
        a[m] = u;
      } 
    } 
  }
  
  private static void insertionSort(int[] a, int from, int to, IntComparator comp) {
    for (int i = from; ++i < to; ) {
      int t = a[i];
      int j = i;
      int u;
      for (u = a[j - 1]; comp.compare(t, u) < 0; u = a[--j - 1]) {
        a[j] = u;
        if (from == j - 1) {
          j--;
          break;
        } 
      } 
      a[j] = t;
    } 
  }
  
  public static void quickSort(int[] x, int from, int to, IntComparator comp) {
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
    int v = x[m];
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
  
  public static void quickSort(int[] x, IntComparator comp) {
    quickSort(x, 0, x.length, comp);
  }
  
  protected static class ForkJoinQuickSortComp extends RecursiveAction {
    private static final long serialVersionUID = 1L;
    
    private final int from;
    
    private final int to;
    
    private final int[] x;
    
    private final IntComparator comp;
    
    public ForkJoinQuickSortComp(int[] x, int from, int to, IntComparator comp) {
      this.from = from;
      this.to = to;
      this.x = x;
      this.comp = comp;
    }
    
    protected void compute() {
      int[] x = this.x;
      int len = this.to - this.from;
      if (len < 8192) {
        IntArrays.quickSort(x, this.from, this.to, this.comp);
        return;
      } 
      int m = this.from + len / 2;
      int l = this.from;
      int n = this.to - 1;
      int s = len / 8;
      l = IntArrays.med3(x, l, l + s, l + 2 * s, this.comp);
      m = IntArrays.med3(x, m - s, m, m + s, this.comp);
      n = IntArrays.med3(x, n - 2 * s, n - s, n, this.comp);
      m = IntArrays.med3(x, l, m, n, this.comp);
      int v = x[m];
      int a = this.from, b = a, c = this.to - 1, d = c;
      while (true) {
        int comparison;
        if (b <= c && (comparison = this.comp.compare(x[b], v)) <= 0) {
          if (comparison == 0)
            IntArrays.swap(x, a++, b); 
          b++;
          continue;
        } 
        while (c >= b && (comparison = this.comp.compare(x[c], v)) >= 0) {
          if (comparison == 0)
            IntArrays.swap(x, c, d--); 
          c--;
        } 
        if (b > c)
          break; 
        IntArrays.swap(x, b++, c--);
      } 
      s = Math.min(a - this.from, b - a);
      IntArrays.swap(x, this.from, b - s, s);
      s = Math.min(d - c, this.to - d - 1);
      IntArrays.swap(x, b, this.to - s, s);
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
  
  public static void parallelQuickSort(int[] x, int from, int to, IntComparator comp) {
    if (to - from < 8192) {
      quickSort(x, from, to, comp);
    } else {
      ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
      pool.invoke(new ForkJoinQuickSortComp(x, from, to, comp));
      pool.shutdown();
    } 
  }
  
  public static void parallelQuickSort(int[] x, IntComparator comp) {
    parallelQuickSort(x, 0, x.length, comp);
  }
  
  private static int med3(int[] x, int a, int b, int c) {
    int ab = Integer.compare(x[a], x[b]);
    int ac = Integer.compare(x[a], x[c]);
    int bc = Integer.compare(x[b], x[c]);
    return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
  }
  
  private static void selectionSort(int[] a, int from, int to) {
    for (int i = from; i < to - 1; i++) {
      int m = i;
      for (int j = i + 1; j < to; j++) {
        if (a[j] < a[m])
          m = j; 
      } 
      if (m != i) {
        int u = a[i];
        a[i] = a[m];
        a[m] = u;
      } 
    } 
  }
  
  private static void insertionSort(int[] a, int from, int to) {
    for (int i = from; ++i < to; ) {
      int t = a[i];
      int j = i;
      int u;
      for (u = a[j - 1]; t < u; u = a[--j - 1]) {
        a[j] = u;
        if (from == j - 1) {
          j--;
          break;
        } 
      } 
      a[j] = t;
    } 
  }
  
  public static void quickSort(int[] x, int from, int to) {
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
    int v = x[m];
    int a = from, b = a, c = to - 1, d = c;
    while (true) {
      int comparison;
      if (b <= c && (comparison = Integer.compare(x[b], v)) <= 0) {
        if (comparison == 0)
          swap(x, a++, b); 
        b++;
        continue;
      } 
      while (c >= b && (comparison = Integer.compare(x[c], v)) >= 0) {
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
  
  public static void quickSort(int[] x) {
    quickSort(x, 0, x.length);
  }
  
  protected static class ForkJoinQuickSort extends RecursiveAction {
    private static final long serialVersionUID = 1L;
    
    private final int from;
    
    private final int to;
    
    private final int[] x;
    
    public ForkJoinQuickSort(int[] x, int from, int to) {
      this.from = from;
      this.to = to;
      this.x = x;
    }
    
    protected void compute() {
      int[] x = this.x;
      int len = this.to - this.from;
      if (len < 8192) {
        IntArrays.quickSort(x, this.from, this.to);
        return;
      } 
      int m = this.from + len / 2;
      int l = this.from;
      int n = this.to - 1;
      int s = len / 8;
      l = IntArrays.med3(x, l, l + s, l + 2 * s);
      m = IntArrays.med3(x, m - s, m, m + s);
      n = IntArrays.med3(x, n - 2 * s, n - s, n);
      m = IntArrays.med3(x, l, m, n);
      int v = x[m];
      int a = this.from, b = a, c = this.to - 1, d = c;
      while (true) {
        int comparison;
        if (b <= c && (comparison = Integer.compare(x[b], v)) <= 0) {
          if (comparison == 0)
            IntArrays.swap(x, a++, b); 
          b++;
          continue;
        } 
        while (c >= b && (comparison = Integer.compare(x[c], v)) >= 0) {
          if (comparison == 0)
            IntArrays.swap(x, c, d--); 
          c--;
        } 
        if (b > c)
          break; 
        IntArrays.swap(x, b++, c--);
      } 
      s = Math.min(a - this.from, b - a);
      IntArrays.swap(x, this.from, b - s, s);
      s = Math.min(d - c, this.to - d - 1);
      IntArrays.swap(x, b, this.to - s, s);
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
  
  public static void parallelQuickSort(int[] x, int from, int to) {
    if (to - from < 8192) {
      quickSort(x, from, to);
    } else {
      ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
      pool.invoke(new ForkJoinQuickSort(x, from, to));
      pool.shutdown();
    } 
  }
  
  public static void parallelQuickSort(int[] x) {
    parallelQuickSort(x, 0, x.length);
  }
  
  private static int med3Indirect(int[] perm, int[] x, int a, int b, int c) {
    int aa = x[perm[a]];
    int bb = x[perm[b]];
    int cc = x[perm[c]];
    int ab = Integer.compare(aa, bb);
    int ac = Integer.compare(aa, cc);
    int bc = Integer.compare(bb, cc);
    return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
  }
  
  private static void insertionSortIndirect(int[] perm, int[] a, int from, int to) {
    for (int i = from; ++i < to; ) {
      int t = perm[i];
      int j = i;
      int u;
      for (u = perm[j - 1]; a[t] < a[u]; u = perm[--j - 1]) {
        perm[j] = u;
        if (from == j - 1) {
          j--;
          break;
        } 
      } 
      perm[j] = t;
    } 
  }
  
  public static void quickSortIndirect(int[] perm, int[] x, int from, int to) {
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
    int v = x[perm[m]];
    int a = from, b = a, c = to - 1, d = c;
    while (true) {
      int comparison;
      if (b <= c && (comparison = Integer.compare(x[perm[b]], v)) <= 0) {
        if (comparison == 0)
          swap(perm, a++, b); 
        b++;
        continue;
      } 
      while (c >= b && (comparison = Integer.compare(x[perm[c]], v)) >= 0) {
        if (comparison == 0)
          swap(perm, c, d--); 
        c--;
      } 
      if (b > c)
        break; 
      swap(perm, b++, c--);
    } 
    int s = Math.min(a - from, b - a);
    swap(perm, from, b - s, s);
    s = Math.min(d - c, to - d - 1);
    swap(perm, b, to - s, s);
    if ((s = b - a) > 1)
      quickSortIndirect(perm, x, from, from + s); 
    if ((s = d - c) > 1)
      quickSortIndirect(perm, x, to - s, to); 
  }
  
  public static void quickSortIndirect(int[] perm, int[] x) {
    quickSortIndirect(perm, x, 0, x.length);
  }
  
  protected static class ForkJoinQuickSortIndirect extends RecursiveAction {
    private static final long serialVersionUID = 1L;
    
    private final int from;
    
    private final int to;
    
    private final int[] perm;
    
    private final int[] x;
    
    public ForkJoinQuickSortIndirect(int[] perm, int[] x, int from, int to) {
      this.from = from;
      this.to = to;
      this.x = x;
      this.perm = perm;
    }
    
    protected void compute() {
      int[] x = this.x;
      int len = this.to - this.from;
      if (len < 8192) {
        IntArrays.quickSortIndirect(this.perm, x, this.from, this.to);
        return;
      } 
      int m = this.from + len / 2;
      int l = this.from;
      int n = this.to - 1;
      int s = len / 8;
      l = IntArrays.med3Indirect(this.perm, x, l, l + s, l + 2 * s);
      m = IntArrays.med3Indirect(this.perm, x, m - s, m, m + s);
      n = IntArrays.med3Indirect(this.perm, x, n - 2 * s, n - s, n);
      m = IntArrays.med3Indirect(this.perm, x, l, m, n);
      int v = x[this.perm[m]];
      int a = this.from, b = a, c = this.to - 1, d = c;
      while (true) {
        int comparison;
        if (b <= c && (comparison = Integer.compare(x[this.perm[b]], v)) <= 0) {
          if (comparison == 0)
            IntArrays.swap(this.perm, a++, b); 
          b++;
          continue;
        } 
        while (c >= b && (comparison = Integer.compare(x[this.perm[c]], v)) >= 0) {
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
  
  public static void parallelQuickSortIndirect(int[] perm, int[] x, int from, int to) {
    if (to - from < 8192) {
      quickSortIndirect(perm, x, from, to);
    } else {
      ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
      pool.invoke(new ForkJoinQuickSortIndirect(perm, x, from, to));
      pool.shutdown();
    } 
  }
  
  public static void parallelQuickSortIndirect(int[] perm, int[] x) {
    parallelQuickSortIndirect(perm, x, 0, x.length);
  }
  
  public static void stabilize(int[] perm, int[] x, int from, int to) {
    int curr = from;
    for (int i = from + 1; i < to; i++) {
      if (x[perm[i]] != x[perm[curr]]) {
        if (i - curr > 1)
          parallelQuickSort(perm, curr, i); 
        curr = i;
      } 
    } 
    if (to - curr > 1)
      parallelQuickSort(perm, curr, to); 
  }
  
  public static void stabilize(int[] perm, int[] x) {
    stabilize(perm, x, 0, perm.length);
  }
  
  private static int med3(int[] x, int[] y, int a, int b, int c) {
    int t, ab = ((t = Integer.compare(x[a], x[b])) == 0) ? Integer.compare(y[a], y[b]) : t;
    int ac = ((t = Integer.compare(x[a], x[c])) == 0) ? Integer.compare(y[a], y[c]) : t;
    int bc = ((t = Integer.compare(x[b], x[c])) == 0) ? Integer.compare(y[b], y[c]) : t;
    return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
  }
  
  private static void swap(int[] x, int[] y, int a, int b) {
    int t = x[a];
    int u = y[a];
    x[a] = x[b];
    y[a] = y[b];
    x[b] = t;
    y[b] = u;
  }
  
  private static void swap(int[] x, int[] y, int a, int b, int n) {
    for (int i = 0; i < n; i++, a++, b++)
      swap(x, y, a, b); 
  }
  
  private static void selectionSort(int[] a, int[] b, int from, int to) {
    for (int i = from; i < to - 1; i++) {
      int m = i;
      for (int j = i + 1; j < to; j++) {
        int u;
        if ((u = Integer.compare(a[j], a[m])) < 0 || (u == 0 && b[j] < b[m]))
          m = j; 
      } 
      if (m != i) {
        int t = a[i];
        a[i] = a[m];
        a[m] = t;
        t = b[i];
        b[i] = b[m];
        b[m] = t;
      } 
    } 
  }
  
  public static void quickSort(int[] x, int[] y, int from, int to) {
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
    int v = x[m], w = y[m];
    int a = from, b = a, c = to - 1, d = c;
    while (true) {
      if (b <= c) {
        int comparison;
        int t;
        if ((comparison = ((t = Integer.compare(x[b], v)) == 0) ? Integer.compare(y[b], w) : t) <= 0) {
          if (comparison == 0)
            swap(x, y, a++, b); 
          b++;
          continue;
        } 
      } 
      while (c >= b) {
        int comparison;
        int t;
        if ((comparison = ((t = Integer.compare(x[c], v)) == 0) ? Integer.compare(y[c], w) : t) >= 0) {
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
  
  public static void quickSort(int[] x, int[] y) {
    ensureSameLength(x, y);
    quickSort(x, y, 0, x.length);
  }
  
  protected static class ForkJoinQuickSort2 extends RecursiveAction {
    private static final long serialVersionUID = 1L;
    
    private final int from;
    
    private final int to;
    
    private final int[] x;
    
    private final int[] y;
    
    public ForkJoinQuickSort2(int[] x, int[] y, int from, int to) {
      this.from = from;
      this.to = to;
      this.x = x;
      this.y = y;
    }
    
    protected void compute() {
      int[] x = this.x;
      int[] y = this.y;
      int len = this.to - this.from;
      if (len < 8192) {
        IntArrays.quickSort(x, y, this.from, this.to);
        return;
      } 
      int m = this.from + len / 2;
      int l = this.from;
      int n = this.to - 1;
      int s = len / 8;
      l = IntArrays.med3(x, y, l, l + s, l + 2 * s);
      m = IntArrays.med3(x, y, m - s, m, m + s);
      n = IntArrays.med3(x, y, n - 2 * s, n - s, n);
      m = IntArrays.med3(x, y, l, m, n);
      int v = x[m], w = y[m];
      int a = this.from, b = a, c = this.to - 1, d = c;
      while (true) {
        if (b <= c) {
          int comparison;
          int i;
          if ((comparison = ((i = Integer.compare(x[b], v)) == 0) ? Integer.compare(y[b], w) : i) <= 0) {
            if (comparison == 0)
              IntArrays.swap(x, y, a++, b); 
            b++;
            continue;
          } 
        } 
        while (c >= b) {
          int comparison;
          int i;
          if ((comparison = ((i = Integer.compare(x[c], v)) == 0) ? Integer.compare(y[c], w) : i) >= 0) {
            if (comparison == 0)
              IntArrays.swap(x, y, c, d--); 
            c--;
          } 
        } 
        if (b > c)
          break; 
        IntArrays.swap(x, y, b++, c--);
      } 
      s = Math.min(a - this.from, b - a);
      IntArrays.swap(x, y, this.from, b - s, s);
      s = Math.min(d - c, this.to - d - 1);
      IntArrays.swap(x, y, b, this.to - s, s);
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
  
  public static void parallelQuickSort(int[] x, int[] y, int from, int to) {
    if (to - from < 8192)
      quickSort(x, y, from, to); 
    ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    pool.invoke(new ForkJoinQuickSort2(x, y, from, to));
    pool.shutdown();
  }
  
  public static void parallelQuickSort(int[] x, int[] y) {
    ensureSameLength(x, y);
    parallelQuickSort(x, y, 0, x.length);
  }
  
  public static void unstableSort(int[] a, int from, int to) {
    if (to - from >= 2000) {
      radixSort(a, from, to);
    } else {
      quickSort(a, from, to);
    } 
  }
  
  public static void unstableSort(int[] a) {
    unstableSort(a, 0, a.length);
  }
  
  public static void unstableSort(int[] a, int from, int to, IntComparator comp) {
    quickSort(a, from, to, comp);
  }
  
  public static void unstableSort(int[] a, IntComparator comp) {
    unstableSort(a, 0, a.length, comp);
  }
  
  public static void mergeSort(int[] a, int from, int to, int[] supp) {
    int len = to - from;
    if (len < 16) {
      insertionSort(a, from, to);
      return;
    } 
    int mid = from + to >>> 1;
    mergeSort(supp, from, mid, a);
    mergeSort(supp, mid, to, a);
    if (supp[mid - 1] <= supp[mid]) {
      System.arraycopy(supp, from, a, from, len);
      return;
    } 
    for (int i = from, p = from, q = mid; i < to; i++) {
      if (q >= to || (p < mid && supp[p] <= supp[q])) {
        a[i] = supp[p++];
      } else {
        a[i] = supp[q++];
      } 
    } 
  }
  
  public static void mergeSort(int[] a, int from, int to) {
    mergeSort(a, from, to, (int[])a.clone());
  }
  
  public static void mergeSort(int[] a) {
    mergeSort(a, 0, a.length);
  }
  
  public static void mergeSort(int[] a, int from, int to, IntComparator comp, int[] supp) {
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
  
  public static void mergeSort(int[] a, int from, int to, IntComparator comp) {
    mergeSort(a, from, to, comp, (int[])a.clone());
  }
  
  public static void mergeSort(int[] a, IntComparator comp) {
    mergeSort(a, 0, a.length, comp);
  }
  
  public static void stableSort(int[] a, int from, int to) {
    unstableSort(a, from, to);
  }
  
  public static void stableSort(int[] a) {
    stableSort(a, 0, a.length);
  }
  
  public static void stableSort(int[] a, int from, int to, IntComparator comp) {
    mergeSort(a, from, to, comp);
  }
  
  public static void stableSort(int[] a, IntComparator comp) {
    stableSort(a, 0, a.length, comp);
  }
  
  public static int binarySearch(int[] a, int from, int to, int key) {
    to--;
    while (from <= to) {
      int mid = from + to >>> 1;
      int midVal = a[mid];
      if (midVal < key) {
        from = mid + 1;
        continue;
      } 
      if (midVal > key) {
        to = mid - 1;
        continue;
      } 
      return mid;
    } 
    return -(from + 1);
  }
  
  public static int binarySearch(int[] a, int key) {
    return binarySearch(a, 0, a.length, key);
  }
  
  public static int binarySearch(int[] a, int from, int to, int key, IntComparator c) {
    to--;
    while (from <= to) {
      int mid = from + to >>> 1;
      int midVal = a[mid];
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
  
  public static int binarySearch(int[] a, int key, IntComparator c) {
    return binarySearch(a, 0, a.length, key, c);
  }
  
  public static void radixSort(int[] a) {
    radixSort(a, 0, a.length);
  }
  
  public static void radixSort(int[] a, int from, int to) {
    if (to - from < 1024) {
      quickSort(a, from, to);
      return;
    } 
    int maxLevel = 3;
    int stackSize = 766;
    int stackPos = 0;
    int[] offsetStack = new int[766];
    int[] lengthStack = new int[766];
    int[] levelStack = new int[766];
    offsetStack[stackPos] = from;
    lengthStack[stackPos] = to - from;
    levelStack[stackPos++] = 0;
    int[] count = new int[256];
    int[] pos = new int[256];
    while (stackPos > 0) {
      int first = offsetStack[--stackPos];
      int length = lengthStack[stackPos];
      int level = levelStack[stackPos];
      int signMask = (level % 4 == 0) ? 128 : 0;
      int shift = (3 - level % 4) * 8;
      for (int i = first + length; i-- != first;)
        count[a[i] >>> shift & 0xFF ^ signMask] = count[a[i] >>> shift & 0xFF ^ signMask] + 1; 
      int lastUsed = -1;
      for (int j = 0, p = first; j < 256; j++) {
        if (count[j] != 0)
          lastUsed = j; 
        pos[j] = p += count[j];
      } 
      int end = first + length - count[lastUsed];
      for (int k = first, c = -1; k <= end; k += count[c], count[c] = 0) {
        int t = a[k];
        c = t >>> shift & 0xFF ^ signMask;
        if (k < end) {
          int d;
          for (pos[c] = pos[c] - 1; (d = pos[c] - 1) > k; ) {
            int z = t;
            t = a[d];
            a[d] = z;
            c = t >>> shift & 0xFF ^ signMask;
          } 
          a[k] = t;
        } 
        if (level < 3 && count[c] > 1)
          if (count[c] < 1024) {
            quickSort(a, k, k + count[c]);
          } else {
            offsetStack[stackPos] = k;
            lengthStack[stackPos] = count[c];
            levelStack[stackPos++] = level + 1;
          }  
      } 
    } 
  }
  
  protected static final class Segment {
    protected final int offset;
    
    protected final int length;
    
    protected final int level;
    
    protected Segment(int offset, int length, int level) {
      this.offset = offset;
      this.length = length;
      this.level = level;
    }
    
    public String toString() {
      return "Segment [offset=" + this.offset + ", length=" + this.length + ", level=" + this.level + "]";
    }
  }
  
  protected static final Segment POISON_PILL = new Segment(-1, -1, -1);
  
  public static void parallelRadixSort(int[] a, int from, int to) {
    if (to - from < 1024) {
      quickSort(a, from, to);
      return;
    } 
    int maxLevel = 3;
    LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<>();
    queue.add(new Segment(from, to - from, 0));
    AtomicInteger queueSize = new AtomicInteger(1);
    int numberOfThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, 
        Executors.defaultThreadFactory());
    ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<>(executorService);
    for (int j = numberOfThreads; j-- != 0;) {
      executorCompletionService.submit(() -> {
            int[] count = new int[256];
            int[] pos = new int[256];
            while (true) {
              if (queueSize.get() == 0) {
                int m = numberOfThreads;
                while (m-- != 0)
                  queue.add(POISON_PILL); 
              } 
              Segment segment = queue.take();
              if (segment == POISON_PILL)
                return null; 
              int first = segment.offset;
              int length = segment.length;
              int level = segment.level;
              int signMask = (level % 4 == 0) ? 128 : 0;
              int shift = (3 - level % 4) * 8;
              int i = first + length;
              while (i-- != first)
                count[a[i] >>> shift & 0xFF ^ signMask] = count[a[i] >>> shift & 0xFF ^ signMask] + 1; 
              int lastUsed = -1;
              int j = 0;
              int p = first;
              while (j < 256) {
                if (count[j] != 0)
                  lastUsed = j; 
                pos[j] = p += count[j];
                j++;
              } 
              int end = first + length - count[lastUsed];
              int k = first;
              int c = -1;
              while (k <= end) {
                int t = a[k];
                c = t >>> shift & 0xFF ^ signMask;
                if (k < end) {
                  pos[c] = pos[c] - 1;
                  int d;
                  while ((d = pos[c] - 1) > k) {
                    int z = t;
                    t = a[d];
                    a[d] = z;
                    c = t >>> shift & 0xFF ^ signMask;
                  } 
                  a[k] = t;
                } 
                if (level < 3 && count[c] > 1)
                  if (count[c] < 1024) {
                    quickSort(a, k, k + count[c]);
                  } else {
                    queueSize.incrementAndGet();
                    queue.add(new Segment(k, count[c], level + 1));
                  }  
                k += count[c];
                count[c] = 0;
              } 
              queueSize.decrementAndGet();
            } 
          });
    } 
    Throwable problem = null;
    for (int i = numberOfThreads; i-- != 0;) {
      try {
        executorCompletionService.take().get();
      } catch (Exception e) {
        problem = e.getCause();
      } 
    } 
    executorService.shutdown();
    if (problem != null)
      throw (problem instanceof RuntimeException) ? (RuntimeException)problem : new RuntimeException(problem); 
  }
  
  public static void parallelRadixSort(int[] a) {
    parallelRadixSort(a, 0, a.length);
  }
  
  public static void radixSortIndirect(int[] perm, int[] a, boolean stable) {
    radixSortIndirect(perm, a, 0, perm.length, stable);
  }
  
  public static void radixSortIndirect(int[] perm, int[] a, int from, int to, boolean stable) {
    if (to - from < 1024) {
      insertionSortIndirect(perm, a, from, to);
      return;
    } 
    int maxLevel = 3;
    int stackSize = 766;
    int stackPos = 0;
    int[] offsetStack = new int[766];
    int[] lengthStack = new int[766];
    int[] levelStack = new int[766];
    offsetStack[stackPos] = from;
    lengthStack[stackPos] = to - from;
    levelStack[stackPos++] = 0;
    int[] count = new int[256];
    int[] pos = new int[256];
    int[] support = stable ? new int[perm.length] : null;
    while (stackPos > 0) {
      int first = offsetStack[--stackPos];
      int length = lengthStack[stackPos];
      int level = levelStack[stackPos];
      int signMask = (level % 4 == 0) ? 128 : 0;
      int shift = (3 - level % 4) * 8;
      for (int i = first + length; i-- != first;)
        count[a[perm[i]] >>> shift & 0xFF ^ signMask] = count[a[perm[i]] >>> shift & 0xFF ^ signMask] + 1; 
      int lastUsed = -1;
      int j, p;
      for (j = 0, p = stable ? 0 : first; j < 256; j++) {
        if (count[j] != 0)
          lastUsed = j; 
        pos[j] = p += count[j];
      } 
      if (stable) {
        for (j = first + length; j-- != first; ) {
          pos[a[perm[j]] >>> shift & 0xFF ^ signMask] = pos[a[perm[j]] >>> shift & 0xFF ^ signMask] - 1;
          support[pos[a[perm[j]] >>> shift & 0xFF ^ signMask] - 1] = perm[j];
        } 
        System.arraycopy(support, 0, perm, first, length);
        for (j = 0, p = first; j <= lastUsed; j++) {
          if (level < 3 && count[j] > 1)
            if (count[j] < 1024) {
              insertionSortIndirect(perm, a, p, p + count[j]);
            } else {
              offsetStack[stackPos] = p;
              lengthStack[stackPos] = count[j];
              levelStack[stackPos++] = level + 1;
            }  
          p += count[j];
        } 
        Arrays.fill(count, 0);
        continue;
      } 
      int end = first + length - count[lastUsed];
      for (int k = first, c = -1; k <= end; k += count[c], count[c] = 0) {
        int t = perm[k];
        c = a[t] >>> shift & 0xFF ^ signMask;
        if (k < end) {
          int d;
          for (pos[c] = pos[c] - 1; (d = pos[c] - 1) > k; ) {
            int z = t;
            t = perm[d];
            perm[d] = z;
            c = a[t] >>> shift & 0xFF ^ signMask;
          } 
          perm[k] = t;
        } 
        if (level < 3 && count[c] > 1)
          if (count[c] < 1024) {
            insertionSortIndirect(perm, a, k, k + count[c]);
          } else {
            offsetStack[stackPos] = k;
            lengthStack[stackPos] = count[c];
            levelStack[stackPos++] = level + 1;
          }  
      } 
    } 
  }
  
  public static void parallelRadixSortIndirect(int[] perm, int[] a, int from, int to, boolean stable) {
    if (to - from < 1024) {
      radixSortIndirect(perm, a, from, to, stable);
      return;
    } 
    int maxLevel = 3;
    LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<>();
    queue.add(new Segment(from, to - from, 0));
    AtomicInteger queueSize = new AtomicInteger(1);
    int numberOfThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, 
        Executors.defaultThreadFactory());
    ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<>(executorService);
    int[] support = stable ? new int[perm.length] : null;
    for (int j = numberOfThreads; j-- != 0;) {
      executorCompletionService.submit(() -> {
            int[] count = new int[256];
            int[] pos = new int[256];
            while (true) {
              if (queueSize.get() == 0) {
                int k = numberOfThreads;
                while (k-- != 0)
                  queue.add(POISON_PILL); 
              } 
              Segment segment = queue.take();
              if (segment == POISON_PILL)
                return null; 
              int first = segment.offset;
              int length = segment.length;
              int level = segment.level;
              int signMask = (level % 4 == 0) ? 128 : 0;
              int shift = (3 - level % 4) * 8;
              int i = first + length;
              while (i-- != first)
                count[a[perm[i]] >>> shift & 0xFF ^ signMask] = count[a[perm[i]] >>> shift & 0xFF ^ signMask] + 1; 
              int lastUsed = -1;
              int j = 0;
              int p = first;
              while (j < 256) {
                if (count[j] != 0)
                  lastUsed = j; 
                pos[j] = p += count[j];
                j++;
              } 
              if (stable) {
                j = first + length;
                while (j-- != first) {
                  pos[a[perm[j]] >>> shift & 0xFF ^ signMask] = pos[a[perm[j]] >>> shift & 0xFF ^ signMask] - 1;
                  support[pos[a[perm[j]] >>> shift & 0xFF ^ signMask] - 1] = perm[j];
                } 
                System.arraycopy(support, first, perm, first, length);
                j = 0;
                p = first;
                while (j <= lastUsed) {
                  if (level < 3 && count[j] > 1)
                    if (count[j] < 1024) {
                      radixSortIndirect(perm, a, p, p + count[j], stable);
                    } else {
                      queueSize.incrementAndGet();
                      queue.add(new Segment(p, count[j], level + 1));
                    }  
                  p += count[j];
                  j++;
                } 
                Arrays.fill(count, 0);
              } else {
                int end = first + length - count[lastUsed];
                int k = first;
                int c = -1;
                while (k <= end) {
                  int t = perm[k];
                  c = a[t] >>> shift & 0xFF ^ signMask;
                  if (k < end) {
                    pos[c] = pos[c] - 1;
                    int d;
                    while ((d = pos[c] - 1) > k) {
                      int z = t;
                      t = perm[d];
                      perm[d] = z;
                      c = a[t] >>> shift & 0xFF ^ signMask;
                    } 
                    perm[k] = t;
                  } 
                  if (level < 3 && count[c] > 1)
                    if (count[c] < 1024) {
                      radixSortIndirect(perm, a, k, k + count[c], stable);
                    } else {
                      queueSize.incrementAndGet();
                      queue.add(new Segment(k, count[c], level + 1));
                    }  
                  k += count[c];
                  count[c] = 0;
                } 
              } 
              queueSize.decrementAndGet();
            } 
          });
    } 
    Throwable problem = null;
    for (int i = numberOfThreads; i-- != 0;) {
      try {
        executorCompletionService.take().get();
      } catch (Exception e) {
        problem = e.getCause();
      } 
    } 
    executorService.shutdown();
    if (problem != null)
      throw (problem instanceof RuntimeException) ? (RuntimeException)problem : new RuntimeException(problem); 
  }
  
  public static void parallelRadixSortIndirect(int[] perm, int[] a, boolean stable) {
    parallelRadixSortIndirect(perm, a, 0, a.length, stable);
  }
  
  public static void radixSort(int[] a, int[] b) {
    ensureSameLength(a, b);
    radixSort(a, b, 0, a.length);
  }
  
  public static void radixSort(int[] a, int[] b, int from, int to) {
    if (to - from < 1024) {
      selectionSort(a, b, from, to);
      return;
    } 
    int layers = 2;
    int maxLevel = 7;
    int stackSize = 1786;
    int stackPos = 0;
    int[] offsetStack = new int[1786];
    int[] lengthStack = new int[1786];
    int[] levelStack = new int[1786];
    offsetStack[stackPos] = from;
    lengthStack[stackPos] = to - from;
    levelStack[stackPos++] = 0;
    int[] count = new int[256];
    int[] pos = new int[256];
    while (stackPos > 0) {
      int first = offsetStack[--stackPos];
      int length = lengthStack[stackPos];
      int level = levelStack[stackPos];
      int signMask = (level % 4 == 0) ? 128 : 0;
      int[] k = (level < 4) ? a : b;
      int shift = (3 - level % 4) * 8;
      for (int i = first + length; i-- != first;)
        count[k[i] >>> shift & 0xFF ^ signMask] = count[k[i] >>> shift & 0xFF ^ signMask] + 1; 
      int lastUsed = -1;
      for (int j = 0, p = first; j < 256; j++) {
        if (count[j] != 0)
          lastUsed = j; 
        pos[j] = p += count[j];
      } 
      int end = first + length - count[lastUsed];
      for (int m = first, c = -1; m <= end; m += count[c], count[c] = 0) {
        int t = a[m];
        int u = b[m];
        c = k[m] >>> shift & 0xFF ^ signMask;
        if (m < end) {
          int d;
          for (pos[c] = pos[c] - 1; (d = pos[c] - 1) > m; ) {
            c = k[d] >>> shift & 0xFF ^ signMask;
            int z = t;
            t = a[d];
            a[d] = z;
            z = u;
            u = b[d];
            b[d] = z;
          } 
          a[m] = t;
          b[m] = u;
        } 
        if (level < 7 && count[c] > 1)
          if (count[c] < 1024) {
            selectionSort(a, b, m, m + count[c]);
          } else {
            offsetStack[stackPos] = m;
            lengthStack[stackPos] = count[c];
            levelStack[stackPos++] = level + 1;
          }  
      } 
    } 
  }
  
  public static void parallelRadixSort(int[] a, int[] b, int from, int to) {
    if (to - from < 1024) {
      quickSort(a, b, from, to);
      return;
    } 
    int layers = 2;
    if (a.length != b.length)
      throw new IllegalArgumentException("Array size mismatch."); 
    int maxLevel = 7;
    LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<>();
    queue.add(new Segment(from, to - from, 0));
    AtomicInteger queueSize = new AtomicInteger(1);
    int numberOfThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, 
        Executors.defaultThreadFactory());
    ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<>(executorService);
    for (int j = numberOfThreads; j-- != 0;) {
      executorCompletionService.submit(() -> {
            int[] count = new int[256];
            int[] pos = new int[256];
            while (true) {
              if (queueSize.get() == 0) {
                int n = numberOfThreads;
                while (n-- != 0)
                  queue.add(POISON_PILL); 
              } 
              Segment segment = queue.take();
              if (segment == POISON_PILL)
                return null; 
              int first = segment.offset;
              int length = segment.length;
              int level = segment.level;
              int signMask = (level % 4 == 0) ? 128 : 0;
              int[] k = (level < 4) ? a : b;
              int shift = (3 - level % 4) * 8;
              int i = first + length;
              while (i-- != first)
                count[k[i] >>> shift & 0xFF ^ signMask] = count[k[i] >>> shift & 0xFF ^ signMask] + 1; 
              int lastUsed = -1;
              int j = 0;
              int p = first;
              while (j < 256) {
                if (count[j] != 0)
                  lastUsed = j; 
                pos[j] = p += count[j];
                j++;
              } 
              int end = first + length - count[lastUsed];
              int m = first;
              int c = -1;
              while (m <= end) {
                int t = a[m];
                int u = b[m];
                c = k[m] >>> shift & 0xFF ^ signMask;
                if (m < end) {
                  pos[c] = pos[c] - 1;
                  int d;
                  while ((d = pos[c] - 1) > m) {
                    c = k[d] >>> shift & 0xFF ^ signMask;
                    int z = t;
                    int w = u;
                    t = a[d];
                    u = b[d];
                    a[d] = z;
                    b[d] = w;
                  } 
                  a[m] = t;
                  b[m] = u;
                } 
                if (level < 7 && count[c] > 1)
                  if (count[c] < 1024) {
                    quickSort(a, b, m, m + count[c]);
                  } else {
                    queueSize.incrementAndGet();
                    queue.add(new Segment(m, count[c], level + 1));
                  }  
                m += count[c];
                count[c] = 0;
              } 
              queueSize.decrementAndGet();
            } 
          });
    } 
    Throwable problem = null;
    for (int i = numberOfThreads; i-- != 0;) {
      try {
        executorCompletionService.take().get();
      } catch (Exception e) {
        problem = e.getCause();
      } 
    } 
    executorService.shutdown();
    if (problem != null)
      throw (problem instanceof RuntimeException) ? (RuntimeException)problem : new RuntimeException(problem); 
  }
  
  public static void parallelRadixSort(int[] a, int[] b) {
    ensureSameLength(a, b);
    parallelRadixSort(a, b, 0, a.length);
  }
  
  private static void insertionSortIndirect(int[] perm, int[] a, int[] b, int from, int to) {
    for (int i = from; ++i < to; ) {
      int t = perm[i];
      int j = i;
      int u;
      for (u = perm[j - 1]; a[t] < a[u] || (a[t] == a[u] && b[t] < b[u]); u = perm[--j - 1]) {
        perm[j] = u;
        if (from == j - 1) {
          j--;
          break;
        } 
      } 
      perm[j] = t;
    } 
  }
  
  public static void radixSortIndirect(int[] perm, int[] a, int[] b, boolean stable) {
    ensureSameLength(a, b);
    radixSortIndirect(perm, a, b, 0, a.length, stable);
  }
  
  public static void radixSortIndirect(int[] perm, int[] a, int[] b, int from, int to, boolean stable) {
    if (to - from < 1024) {
      insertionSortIndirect(perm, a, b, from, to);
      return;
    } 
    int layers = 2;
    int maxLevel = 7;
    int stackSize = 1786;
    int stackPos = 0;
    int[] offsetStack = new int[1786];
    int[] lengthStack = new int[1786];
    int[] levelStack = new int[1786];
    offsetStack[stackPos] = from;
    lengthStack[stackPos] = to - from;
    levelStack[stackPos++] = 0;
    int[] count = new int[256];
    int[] pos = new int[256];
    int[] support = stable ? new int[perm.length] : null;
    while (stackPos > 0) {
      int first = offsetStack[--stackPos];
      int length = lengthStack[stackPos];
      int level = levelStack[stackPos];
      int signMask = (level % 4 == 0) ? 128 : 0;
      int[] k = (level < 4) ? a : b;
      int shift = (3 - level % 4) * 8;
      for (int i = first + length; i-- != first;)
        count[k[perm[i]] >>> shift & 0xFF ^ signMask] = count[k[perm[i]] >>> shift & 0xFF ^ signMask] + 1; 
      int lastUsed = -1;
      int j, p;
      for (j = 0, p = stable ? 0 : first; j < 256; j++) {
        if (count[j] != 0)
          lastUsed = j; 
        pos[j] = p += count[j];
      } 
      if (stable) {
        for (j = first + length; j-- != first; ) {
          pos[k[perm[j]] >>> shift & 0xFF ^ signMask] = pos[k[perm[j]] >>> shift & 0xFF ^ signMask] - 1;
          support[pos[k[perm[j]] >>> shift & 0xFF ^ signMask] - 1] = perm[j];
        } 
        System.arraycopy(support, 0, perm, first, length);
        for (j = 0, p = first; j < 256; j++) {
          if (level < 7 && count[j] > 1)
            if (count[j] < 1024) {
              insertionSortIndirect(perm, a, b, p, p + count[j]);
            } else {
              offsetStack[stackPos] = p;
              lengthStack[stackPos] = count[j];
              levelStack[stackPos++] = level + 1;
            }  
          p += count[j];
        } 
        Arrays.fill(count, 0);
        continue;
      } 
      int end = first + length - count[lastUsed];
      for (int m = first, c = -1; m <= end; m += count[c], count[c] = 0) {
        int t = perm[m];
        c = k[t] >>> shift & 0xFF ^ signMask;
        if (m < end) {
          int d;
          for (pos[c] = pos[c] - 1; (d = pos[c] - 1) > m; ) {
            int z = t;
            t = perm[d];
            perm[d] = z;
            c = k[t] >>> shift & 0xFF ^ signMask;
          } 
          perm[m] = t;
        } 
        if (level < 7 && count[c] > 1)
          if (count[c] < 1024) {
            insertionSortIndirect(perm, a, b, m, m + count[c]);
          } else {
            offsetStack[stackPos] = m;
            lengthStack[stackPos] = count[c];
            levelStack[stackPos++] = level + 1;
          }  
      } 
    } 
  }
  
  private static void selectionSort(int[][] a, int from, int to, int level) {
    int layers = a.length;
    int firstLayer = level / 4;
    for (int i = from; i < to - 1; i++) {
      int m = i;
      for (int j = i + 1; j < to; j++) {
        for (int p = firstLayer; p < layers; p++) {
          if (a[p][j] < a[p][m]) {
            m = j;
            break;
          } 
          if (a[p][j] > a[p][m])
            break; 
        } 
      } 
      if (m != i)
        for (int p = layers; p-- != 0; ) {
          int u = a[p][i];
          a[p][i] = a[p][m];
          a[p][m] = u;
        }  
    } 
  }
  
  public static void radixSort(int[][] a) {
    radixSort(a, 0, (a[0]).length);
  }
  
  public static void radixSort(int[][] a, int from, int to) {
    if (to - from < 1024) {
      selectionSort(a, from, to, 0);
      return;
    } 
    int layers = a.length;
    int maxLevel = 4 * layers - 1;
    for (int p = layers, l = (a[0]).length; p-- != 0;) {
      if ((a[p]).length != l)
        throw new IllegalArgumentException("The array of index " + p + " has not the same length of the array of index 0."); 
    } 
    int stackSize = 255 * (layers * 4 - 1) + 1;
    int stackPos = 0;
    int[] offsetStack = new int[stackSize];
    int[] lengthStack = new int[stackSize];
    int[] levelStack = new int[stackSize];
    offsetStack[stackPos] = from;
    lengthStack[stackPos] = to - from;
    levelStack[stackPos++] = 0;
    int[] count = new int[256];
    int[] pos = new int[256];
    int[] t = new int[layers];
    while (stackPos > 0) {
      int first = offsetStack[--stackPos];
      int length = lengthStack[stackPos];
      int level = levelStack[stackPos];
      int signMask = (level % 4 == 0) ? 128 : 0;
      int[] k = a[level / 4];
      int shift = (3 - level % 4) * 8;
      for (int i = first + length; i-- != first;)
        count[k[i] >>> shift & 0xFF ^ signMask] = count[k[i] >>> shift & 0xFF ^ signMask] + 1; 
      int lastUsed = -1;
      for (int j = 0, n = first; j < 256; j++) {
        if (count[j] != 0)
          lastUsed = j; 
        pos[j] = n += count[j];
      } 
      int end = first + length - count[lastUsed];
      for (int m = first, c = -1; m <= end; m += count[c], count[c] = 0) {
        int i1;
        for (i1 = layers; i1-- != 0;)
          t[i1] = a[i1][m]; 
        c = k[m] >>> shift & 0xFF ^ signMask;
        if (m < end) {
          int d;
          for (pos[c] = pos[c] - 1; (d = pos[c] - 1) > m; ) {
            c = k[d] >>> shift & 0xFF ^ signMask;
            for (i1 = layers; i1-- != 0; ) {
              int u = t[i1];
              t[i1] = a[i1][d];
              a[i1][d] = u;
            } 
          } 
          for (i1 = layers; i1-- != 0;)
            a[i1][m] = t[i1]; 
        } 
        if (level < maxLevel && count[c] > 1)
          if (count[c] < 1024) {
            selectionSort(a, m, m + count[c], level + 1);
          } else {
            offsetStack[stackPos] = m;
            lengthStack[stackPos] = count[c];
            levelStack[stackPos++] = level + 1;
          }  
      } 
    } 
  }
  
  public static int[] shuffle(int[] a, int from, int to, Random random) {
    for (int i = to - from; i-- != 0; ) {
      int p = random.nextInt(i + 1);
      int t = a[from + i];
      a[from + i] = a[from + p];
      a[from + p] = t;
    } 
    return a;
  }
  
  public static int[] shuffle(int[] a, Random random) {
    for (int i = a.length; i-- != 0; ) {
      int p = random.nextInt(i + 1);
      int t = a[i];
      a[i] = a[p];
      a[p] = t;
    } 
    return a;
  }
  
  public static int[] reverse(int[] a) {
    int length = a.length;
    for (int i = length / 2; i-- != 0; ) {
      int t = a[length - i - 1];
      a[length - i - 1] = a[i];
      a[i] = t;
    } 
    return a;
  }
  
  public static int[] reverse(int[] a, int from, int to) {
    int length = to - from;
    for (int i = length / 2; i-- != 0; ) {
      int t = a[from + length - i - 1];
      a[from + length - i - 1] = a[from + i];
      a[from + i] = t;
    } 
    return a;
  }
  
  private static final class ArrayHashStrategy implements Hash.Strategy<int[]>, Serializable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    private ArrayHashStrategy() {}
    
    public int hashCode(int[] o) {
      return Arrays.hashCode(o);
    }
    
    public boolean equals(int[] a, int[] b) {
      return Arrays.equals(a, b);
    }
  }
  
  public static final Hash.Strategy<int[]> HASH_STRATEGY = new ArrayHashStrategy();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntArrays.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */