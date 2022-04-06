package us.myles.viaversion.libs.javassist.runtime;

public class DotClass {
  public static NoClassDefFoundError fail(ClassNotFoundException e) {
    return new NoClassDefFoundError(e.getMessage());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\runtime\DotClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */