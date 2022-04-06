package us.myles.viaversion.libs.fastutil.ints;

public interface IntHash {
  public static interface Strategy {
    int hashCode(int param1Int);
    
    boolean equals(int param1Int1, int param1Int2);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\IntHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */