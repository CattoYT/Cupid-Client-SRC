package us.myles.viaversion.libs.javassist.tools.rmi;

public class RemoteException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public RemoteException(String msg) {
    super(msg);
  }
  
  public RemoteException(Exception e) {
    super("by " + e.toString());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\tools\rmi\RemoteException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */