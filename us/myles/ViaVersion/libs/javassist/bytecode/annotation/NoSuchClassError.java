package us.myles.viaversion.libs.javassist.bytecode.annotation;

public class NoSuchClassError extends Error {
  private static final long serialVersionUID = 1L;
  
  private String className;
  
  public NoSuchClassError(String className, Error cause) {
    super(cause.toString(), cause);
    this.className = className;
  }
  
  public String getClassName() {
    return this.className;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\annotation\NoSuchClassError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */