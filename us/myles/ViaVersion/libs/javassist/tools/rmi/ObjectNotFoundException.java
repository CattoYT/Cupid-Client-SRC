package us.myles.viaversion.libs.javassist.tools.rmi;

public class ObjectNotFoundException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public ObjectNotFoundException(String name) {
    super(name + " is not exported");
  }
  
  public ObjectNotFoundException(String name, Exception e) {
    super(name + " because of " + e.toString());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\tools\rmi\ObjectNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */