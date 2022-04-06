package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import us.myles.viaversion.libs.fastutil.BigArrays;

public final class ObjectIterators {
  public static class EmptyIterator<K> implements ObjectListIterator<K>, Serializable, Cloneable {
    private static final long serialVersionUID = -7046029254386353129L;
    
    public boolean hasNext() {
      return false;
    }
    
    public boolean hasPrevious() {
      return false;
    }
    
    public K next() {
      throw new NoSuchElementException();
    }
    
    public K previous() {
      throw new NoSuchElementException();
    }
    
    public int nextIndex() {
      return 0;
    }
    
    public int previousIndex() {
      return -1;
    }
    
    public int skip(int n) {
      return 0;
    }
    
    public int back(int n) {
      return 0;
    }
    
    public Object clone() {
      return ObjectIterators.EMPTY_ITERATOR;
    }
    
    private Object readResolve() {
      return ObjectIterators.EMPTY_ITERATOR;
    }
  }
  
  public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();
  
  public static <K> ObjectIterator<K> emptyIterator() {
    return EMPTY_ITERATOR;
  }
  
  private static class SingletonIterator<K> implements ObjectListIterator<K> {
    private final K element;
    
    private int curr;
    
    public SingletonIterator(K element) {
      this.element = element;
    }
    
    public boolean hasNext() {
      return (this.curr == 0);
    }
    
    public boolean hasPrevious() {
      return (this.curr == 1);
    }
    
    public K next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.curr = 1;
      return this.element;
    }
    
    public K previous() {
      if (!hasPrevious())
        throw new NoSuchElementException(); 
      this.curr = 0;
      return this.element;
    }
    
    public int nextIndex() {
      return this.curr;
    }
    
    public int previousIndex() {
      return this.curr - 1;
    }
  }
  
  public static <K> ObjectListIterator<K> singleton(K element) {
    return new SingletonIterator<>(element);
  }
  
  private static class ArrayIterator<K> implements ObjectListIterator<K> {
    private final K[] array;
    
    private final int offset;
    
    private final int length;
    
    private int curr;
    
    public ArrayIterator(K[] array, int offset, int length) {
      this.array = array;
      this.offset = offset;
      this.length = length;
    }
    
    public boolean hasNext() {
      return (this.curr < this.length);
    }
    
    public boolean hasPrevious() {
      return (this.curr > 0);
    }
    
    public K next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      return this.array[this.offset + this.curr++];
    }
    
    public K previous() {
      if (!hasPrevious())
        throw new NoSuchElementException(); 
      return this.array[this.offset + --this.curr];
    }
    
    public int skip(int n) {
      if (n <= this.length - this.curr) {
        this.curr += n;
        return n;
      } 
      n = this.length - this.curr;
      this.curr = this.length;
      return n;
    }
    
    public int back(int n) {
      if (n <= this.curr) {
        this.curr -= n;
        return n;
      } 
      n = this.curr;
      this.curr = 0;
      return n;
    }
    
    public int nextIndex() {
      return this.curr;
    }
    
    public int previousIndex() {
      return this.curr - 1;
    }
  }
  
  public static <K> ObjectListIterator<K> wrap(K[] array, int offset, int length) {
    ObjectArrays.ensureOffsetLength(array, offset, length);
    return new ArrayIterator<>(array, offset, length);
  }
  
  public static <K> ObjectListIterator<K> wrap(K[] array) {
    return new ArrayIterator<>(array, 0, array.length);
  }
  
  public static <K> int unwrap(Iterator<? extends K> i, K[] array, int offset, int max) {
    if (max < 0)
      throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative"); 
    if (offset < 0 || offset + max > array.length)
      throw new IllegalArgumentException(); 
    int j = max;
    while (j-- != 0 && i.hasNext())
      array[offset++] = i.next(); 
    return max - j - 1;
  }
  
  public static <K> int unwrap(Iterator<? extends K> i, K[] array) {
    return unwrap(i, array, 0, array.length);
  }
  
  public static <K> K[] unwrap(Iterator<? extends K> i, int max) {
    if (max < 0)
      throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative"); 
    K[] array = (K[])new Object[16];
    int j = 0;
    while (max-- != 0 && i.hasNext()) {
      if (j == array.length)
        array = ObjectArrays.grow(array, j + 1); 
      array[j++] = i.next();
    } 
    return ObjectArrays.trim(array, j);
  }
  
  public static <K> K[] unwrap(Iterator<? extends K> i) {
    return unwrap(i, 2147483647);
  }
  
  public static <K> long unwrap(Iterator<? extends K> i, K[][] array, long offset, long max) {
    // Byte code:
    //   0: lload #4
    //   2: lconst_0
    //   3: lcmp
    //   4: ifge -> 40
    //   7: new java/lang/IllegalArgumentException
    //   10: dup
    //   11: new java/lang/StringBuilder
    //   14: dup
    //   15: invokespecial <init> : ()V
    //   18: ldc 'The maximum number of elements ('
    //   20: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: lload #4
    //   25: invokevirtual append : (J)Ljava/lang/StringBuilder;
    //   28: ldc ') is negative'
    //   30: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   33: invokevirtual toString : ()Ljava/lang/String;
    //   36: invokespecial <init> : (Ljava/lang/String;)V
    //   39: athrow
    //   40: lload_2
    //   41: lconst_0
    //   42: lcmp
    //   43: iflt -> 58
    //   46: lload_2
    //   47: lload #4
    //   49: ladd
    //   50: aload_1
    //   51: invokestatic length : ([[Ljava/lang/Object;)J
    //   54: lcmp
    //   55: ifle -> 66
    //   58: new java/lang/IllegalArgumentException
    //   61: dup
    //   62: invokespecial <init> : ()V
    //   65: athrow
    //   66: lload #4
    //   68: lstore #6
    //   70: lload #6
    //   72: dup2
    //   73: lconst_1
    //   74: lsub
    //   75: lstore #6
    //   77: lconst_0
    //   78: lcmp
    //   79: ifeq -> 109
    //   82: aload_0
    //   83: invokeinterface hasNext : ()Z
    //   88: ifeq -> 109
    //   91: aload_1
    //   92: lload_2
    //   93: dup2
    //   94: lconst_1
    //   95: ladd
    //   96: lstore_2
    //   97: aload_0
    //   98: invokeinterface next : ()Ljava/lang/Object;
    //   103: invokestatic set : ([[Ljava/lang/Object;JLjava/lang/Object;)V
    //   106: goto -> 70
    //   109: lload #4
    //   111: lload #6
    //   113: lsub
    //   114: lconst_1
    //   115: lsub
    //   116: lreturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #365	-> 0
    //   #366	-> 7
    //   #367	-> 40
    //   #368	-> 58
    //   #369	-> 66
    //   #370	-> 70
    //   #371	-> 91
    //   #372	-> 109
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   0	117	0	i	Ljava/util/Iterator;
    //   0	117	1	array	[[Ljava/lang/Object;
    //   0	117	2	offset	J
    //   0	117	4	max	J
    //   70	47	6	j	J
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	117	0	i	Ljava/util/Iterator<+TK;>;
    //   0	117	1	array	[[TK;
  }
  
  public static <K> long unwrap(Iterator<? extends K> i, K[][] array) {
    return unwrap(i, array, 0L, BigArrays.length((Object[][])array));
  }
  
  public static <K> int unwrap(Iterator<K> i, ObjectCollection<? super K> c, int max) {
    if (max < 0)
      throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative"); 
    int j = max;
    while (j-- != 0 && i.hasNext())
      c.add(i.next()); 
    return max - j - 1;
  }
  
  public static <K> K[][] unwrapBig(Iterator<? extends K> i, long max) {
    // Byte code:
    //   0: lload_1
    //   1: lconst_0
    //   2: lcmp
    //   3: ifge -> 38
    //   6: new java/lang/IllegalArgumentException
    //   9: dup
    //   10: new java/lang/StringBuilder
    //   13: dup
    //   14: invokespecial <init> : ()V
    //   17: ldc 'The maximum number of elements ('
    //   19: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   22: lload_1
    //   23: invokevirtual append : (J)Ljava/lang/StringBuilder;
    //   26: ldc ') is negative'
    //   28: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   31: invokevirtual toString : ()Ljava/lang/String;
    //   34: invokespecial <init> : (Ljava/lang/String;)V
    //   37: athrow
    //   38: ldc2_w 16
    //   41: invokestatic newBigArray : (J)[[Ljava/lang/Object;
    //   44: astore_3
    //   45: lconst_0
    //   46: lstore #4
    //   48: lload_1
    //   49: dup2
    //   50: lconst_1
    //   51: lsub
    //   52: lstore_1
    //   53: lconst_0
    //   54: lcmp
    //   55: ifeq -> 106
    //   58: aload_0
    //   59: invokeinterface hasNext : ()Z
    //   64: ifeq -> 106
    //   67: lload #4
    //   69: aload_3
    //   70: invokestatic length : ([[Ljava/lang/Object;)J
    //   73: lcmp
    //   74: ifne -> 86
    //   77: aload_3
    //   78: lload #4
    //   80: lconst_1
    //   81: ladd
    //   82: invokestatic grow : ([[Ljava/lang/Object;J)[[Ljava/lang/Object;
    //   85: astore_3
    //   86: aload_3
    //   87: lload #4
    //   89: dup2
    //   90: lconst_1
    //   91: ladd
    //   92: lstore #4
    //   94: aload_0
    //   95: invokeinterface next : ()Ljava/lang/Object;
    //   100: invokestatic set : ([[Ljava/lang/Object;JLjava/lang/Object;)V
    //   103: goto -> 48
    //   106: aload_3
    //   107: lload #4
    //   109: invokestatic trim : ([[Ljava/lang/Object;J)[[Ljava/lang/Object;
    //   112: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #439	-> 0
    //   #440	-> 6
    //   #441	-> 38
    //   #442	-> 45
    //   #443	-> 48
    //   #444	-> 67
    //   #445	-> 77
    //   #446	-> 86
    //   #448	-> 106
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   0	113	0	i	Ljava/util/Iterator;
    //   0	113	1	max	J
    //   45	68	3	array	[[Ljava/lang/Object;
    //   48	65	4	j	J
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	113	0	i	Ljava/util/Iterator<+TK;>;
    //   45	68	3	array	[[TK;
  }
  
  public static <K> K[][] unwrapBig(Iterator<? extends K> i) {
    return unwrapBig(i, Long.MAX_VALUE);
  }
  
  public static <K> long unwrap(Iterator<K> i, ObjectCollection<? super K> c) {
    long n = 0L;
    while (i.hasNext()) {
      c.add(i.next());
      n++;
    } 
    return n;
  }
  
  public static <K> int pour(Iterator<K> i, ObjectCollection<? super K> s, int max) {
    if (max < 0)
      throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative"); 
    int j = max;
    while (j-- != 0 && i.hasNext())
      s.add(i.next()); 
    return max - j - 1;
  }
  
  public static <K> int pour(Iterator<K> i, ObjectCollection<? super K> s) {
    return pour(i, s, 2147483647);
  }
  
  public static <K> ObjectList<K> pour(Iterator<K> i, int max) {
    ObjectArrayList<K> l = new ObjectArrayList<>();
    pour(i, l, max);
    l.trim();
    return l;
  }
  
  public static <K> ObjectList<K> pour(Iterator<K> i) {
    return pour(i, 2147483647);
  }
  
  private static class IteratorWrapper<K> implements ObjectIterator<K> {
    final Iterator<K> i;
    
    public IteratorWrapper(Iterator<K> i) {
      this.i = i;
    }
    
    public boolean hasNext() {
      return this.i.hasNext();
    }
    
    public void remove() {
      this.i.remove();
    }
    
    public K next() {
      return this.i.next();
    }
  }
  
  public static <K> ObjectIterator<K> asObjectIterator(Iterator<K> i) {
    if (i instanceof ObjectIterator)
      return (ObjectIterator<K>)i; 
    return new IteratorWrapper<>(i);
  }
  
  private static class ListIteratorWrapper<K> implements ObjectListIterator<K> {
    final ListIterator<K> i;
    
    public ListIteratorWrapper(ListIterator<K> i) {
      this.i = i;
    }
    
    public boolean hasNext() {
      return this.i.hasNext();
    }
    
    public boolean hasPrevious() {
      return this.i.hasPrevious();
    }
    
    public int nextIndex() {
      return this.i.nextIndex();
    }
    
    public int previousIndex() {
      return this.i.previousIndex();
    }
    
    public void set(K k) {
      this.i.set(k);
    }
    
    public void add(K k) {
      this.i.add(k);
    }
    
    public void remove() {
      this.i.remove();
    }
    
    public K next() {
      return this.i.next();
    }
    
    public K previous() {
      return this.i.previous();
    }
  }
  
  public static <K> ObjectListIterator<K> asObjectIterator(ListIterator<K> i) {
    if (i instanceof ObjectListIterator)
      return (ObjectListIterator<K>)i; 
    return new ListIteratorWrapper<>(i);
  }
  
  public static <K> boolean any(ObjectIterator<K> iterator, Predicate<? super K> predicate) {
    return (indexOf(iterator, predicate) != -1);
  }
  
  public static <K> boolean all(ObjectIterator<K> iterator, Predicate<? super K> predicate) {
    Objects.requireNonNull(predicate);
    while (true) {
      if (!iterator.hasNext())
        return true; 
      if (!predicate.test(iterator.next()))
        return false; 
    } 
  }
  
  public static <K> int indexOf(ObjectIterator<K> iterator, Predicate<? super K> predicate) {
    Objects.requireNonNull(predicate);
    for (int i = 0; iterator.hasNext(); i++) {
      if (predicate.test(iterator.next()))
        return i; 
    } 
    return -1;
  }
  
  private static class IteratorConcatenator<K> implements ObjectIterator<K> {
    final ObjectIterator<? extends K>[] a;
    
    int offset;
    
    int length;
    
    int lastOffset = -1;
    
    public IteratorConcatenator(ObjectIterator<? extends K>[] a, int offset, int length) {
      this.a = a;
      this.offset = offset;
      this.length = length;
      advance();
    }
    
    private void advance() {
      while (this.length != 0 && 
        !this.a[this.offset].hasNext()) {
        this.length--;
        this.offset++;
      } 
    }
    
    public boolean hasNext() {
      return (this.length > 0);
    }
    
    public K next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      K next = this.a[this.lastOffset = this.offset].next();
      advance();
      return next;
    }
    
    public void remove() {
      if (this.lastOffset == -1)
        throw new IllegalStateException(); 
      this.a[this.lastOffset].remove();
    }
    
    public int skip(int n) {
      this.lastOffset = -1;
      int skipped = 0;
      while (skipped < n && this.length != 0) {
        skipped += this.a[this.offset].skip(n - skipped);
        if (this.a[this.offset].hasNext())
          break; 
        this.length--;
        this.offset++;
      } 
      return skipped;
    }
  }
  
  public static <K> ObjectIterator<K> concat(ObjectIterator<? extends K>[] a) {
    return concat(a, 0, a.length);
  }
  
  public static <K> ObjectIterator<K> concat(ObjectIterator<? extends K>[] a, int offset, int length) {
    return new IteratorConcatenator<>(a, offset, length);
  }
  
  public static <K> ObjectIterator<K> unmodifiable(ObjectIterator<K> i) {
    return (ObjectIterator<K>)new UnmodifiableIterator(i);
  }
  
  public static <K> ObjectBidirectionalIterator<K> unmodifiable(ObjectBidirectionalIterator<K> i) {
    return (ObjectBidirectionalIterator<K>)new UnmodifiableBidirectionalIterator(i);
  }
  
  public static <K> ObjectListIterator<K> unmodifiable(ObjectListIterator<K> i) {
    return (ObjectListIterator<K>)new UnmodifiableListIterator(i);
  }
  
  public static class ObjectIterators {}
  
  public static class ObjectIterators {}
  
  public static class ObjectIterators {}
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\ObjectIterators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */