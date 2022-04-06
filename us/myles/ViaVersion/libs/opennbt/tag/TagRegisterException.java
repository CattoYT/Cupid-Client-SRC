package us.myles.viaversion.libs.opennbt.tag;

public class TagRegisterException extends RuntimeException {
  private static final long serialVersionUID = -2022049594558041160L;
  
  public TagRegisterException() {}
  
  public TagRegisterException(String message) {
    super(message);
  }
  
  public TagRegisterException(Throwable cause) {
    super(cause);
  }
  
  public TagRegisterException(String message, Throwable cause) {
    super(message, cause);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\tag\TagRegisterException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */