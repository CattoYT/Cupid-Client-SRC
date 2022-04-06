package us.myles.viaversion.libs.fastutil;

import java.util.Comparator;
import java.util.NoSuchElementException;

public class IndirectPriorityQueues {
  public static class EmptyIndirectPriorityQueue implements IndirectPriorityQueue {
    public void enqueue(int i) {
      throw new UnsupportedOperationException();
    }
    
    public int dequeue() {
      throw new NoSuchElementException();
    }
    
    public boolean isEmpty() {
      return true;
    }
    
    public int size() {
      return 0;
    }
    
    public boolean contains(int index) {
      return false;
    }
    
    public void clear() {}
    
    public int first() {
      throw new NoSuchElementException();
    }
    
    public int last() {
      throw new NoSuchElementException();
    }
    
    public void changed() {
      throw new NoSuchElementException();
    }
    
    public void allChanged() {}
    
    public Comparator<?> comparator() {
      return null;
    }
    
    public void changed(int i) {
      throw new IllegalArgumentException("Index " + i + " is not in the queue");
    }
    
    public boolean remove(int i) {
      return false;
    }
    
    public int front(int[] a) {
      return 0;
    }
  }
  
  public static final EmptyIndirectPriorityQueue EMPTY_QUEUE = new EmptyIndirectPriorityQueue();
  
  public static class SynchronizedIndirectPriorityQueue<K> implements IndirectPriorityQueue<K> {
    public static final long serialVersionUID = -7046029254386353129L;
    
    protected final IndirectPriorityQueue<K> q;
    
    protected final Object sync;
    
    protected SynchronizedIndirectPriorityQueue(IndirectPriorityQueue<K> q, Object sync) {
      this.q = q;
      this.sync = sync;
    }
    
    protected SynchronizedIndirectPriorityQueue(IndirectPriorityQueue<K> q) {
      this.q = q;
      this.sync = this;
    }
    
    public void enqueue(int x) {
      synchronized (this.sync) {
        this.q.enqueue(x);
      } 
    }
    
    public int dequeue() {
      synchronized (this.sync) {
        return this.q.dequeue();
      } 
    }
    
    public boolean contains(int index) {
      synchronized (this.sync) {
        return this.q.contains(index);
      } 
    }
    
    public int first() {
      synchronized (this.sync) {
        return this.q.first();
      } 
    }
    
    public int last() {
      synchronized (this.sync) {
        return this.q.last();
      } 
    }
    
    public boolean isEmpty() {
      synchronized (this.sync) {
        return this.q.isEmpty();
      } 
    }
    
    public int size() {
      synchronized (this.sync) {
        return this.q.size();
      } 
    }
    
    public void clear() {
      synchronized (this.sync) {
        this.q.clear();
      } 
    }
    
    public void changed() {
      synchronized (this.sync) {
        this.q.changed();
      } 
    }
    
    public void allChanged() {
      synchronized (this.sync) {
        this.q.allChanged();
      } 
    }
    
    public void changed(int i) {
      synchronized (this.sync) {
        this.q.changed(i);
      } 
    }
    
    public boolean remove(int i) {
      synchronized (this.sync) {
        return this.q.remove(i);
      } 
    }
    
    public Comparator<? super K> comparator() {
      synchronized (this.sync) {
        return this.q.comparator();
      } 
    }
    
    public int front(int[] a) {
      return this.q.front(a);
    }
  }
  
  public static <K> IndirectPriorityQueue<K> synchronize(IndirectPriorityQueue<K> q) {
    return new SynchronizedIndirectPriorityQueue<>(q);
  }
  
  public static <K> IndirectPriorityQueue<K> synchronize(IndirectPriorityQueue<K> q, Object sync) {
    return new SynchronizedIndirectPriorityQueue<>(q, sync);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\IndirectPriorityQueues.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */