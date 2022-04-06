package us.myles.viaversion.libs.opennbt.tag;

public class TagCreateException extends Exception {
  private static final long serialVersionUID = -2022049594558041160L;
  
  public TagCreateException() {}
  
  public TagCreateException(String message) {
    super(message);
  }
  
  public TagCreateException(Throwable cause) {
    super(cause);
  }
  
  public TagCreateException(String message, Throwable cause) {
    super(message, cause);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\tag\TagCreateException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */