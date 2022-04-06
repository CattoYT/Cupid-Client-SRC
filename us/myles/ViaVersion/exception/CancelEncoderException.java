package us.myles.ViaVersion.exception;

import io.netty.handler.codec.EncoderException;
import us.myles.ViaVersion.api.Via;

public class CancelEncoderException extends EncoderException implements CancelCodecException {
  public static final CancelEncoderException CACHED = new CancelEncoderException("This packet is supposed to be cancelled; If you have debug enabled, you can ignore these") {
      public Throwable fillInStackTrace() {
        return (Throwable)this;
      }
    };
  
  public CancelEncoderException() {}
  
  public CancelEncoderException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public CancelEncoderException(String message) {
    super(message);
  }
  
  public CancelEncoderException(Throwable cause) {
    super(cause);
  }
  
  public static CancelEncoderException generate(Throwable cause) {
    return Via.getManager().isDebug() ? new CancelEncoderException(cause) : CACHED;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\exception\CancelEncoderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */