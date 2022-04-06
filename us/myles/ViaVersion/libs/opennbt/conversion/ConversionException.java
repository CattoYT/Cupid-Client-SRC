package us.myles.viaversion.libs.opennbt.conversion;

public class ConversionException extends RuntimeException {
  private static final long serialVersionUID = -2022049594558041160L;
  
  public ConversionException() {}
  
  public ConversionException(String message) {
    super(message);
  }
  
  public ConversionException(Throwable cause) {
    super(cause);
  }
  
  public ConversionException(String message, Throwable cause) {
    super(message, cause);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\conversion\ConversionException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */