package us.myles.viaversion.libs.javassist.tools.reflect;

public class CannotCreateException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public CannotCreateException(String s) {
    super(s);
  }
  
  public CannotCreateException(Exception e) {
    super("by " + e.toString());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\tools\reflect\CannotCreateException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */