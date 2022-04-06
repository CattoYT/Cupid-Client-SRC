package us.myles.ViaVersion.exception;

import io.netty.handler.codec.DecoderException;
import us.myles.ViaVersion.api.Via;

public class CancelDecoderException extends DecoderException implements CancelCodecException {
  public static final CancelDecoderException CACHED = new CancelDecoderException("This packet is supposed to be cancelled; If you have debug enabled, you can ignore these") {
      public Throwable fillInStackTrace() {
        return (Throwable)this;
      }
    };
  
  public CancelDecoderException() {}
  
  public CancelDecoderException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public CancelDecoderException(String message) {
    super(message);
  }
  
  public CancelDecoderException(Throwable cause) {
    super(cause);
  }
  
  public static CancelDecoderException generate(Throwable cause) {
    return Via.getManager().isDebug() ? new CancelDecoderException(cause) : CACHED;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\exception\CancelDecoderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */