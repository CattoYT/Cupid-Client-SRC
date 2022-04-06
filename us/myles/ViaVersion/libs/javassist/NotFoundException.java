package us.myles.viaversion.libs.javassist;

public class NotFoundException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public NotFoundException(String msg) {
    super(msg);
  }
  
  public NotFoundException(String msg, Exception e) {
    super(msg + " because of " + e.toString());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\NotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */