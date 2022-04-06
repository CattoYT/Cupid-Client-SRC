package us.myles.viaversion.libs.fastutil;

public interface Size64 {
  long size64();
  
  @Deprecated
  default int size() {
    return (int)Math.min(2147483647L, size64());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\Size64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */