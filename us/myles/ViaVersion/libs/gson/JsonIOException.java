package us.myles.viaversion.libs.gson;

public final class JsonIOException extends JsonParseException {
  private static final long serialVersionUID = 1L;
  
  public JsonIOException(String msg) {
    super(msg);
  }
  
  public JsonIOException(String msg, Throwable cause) {
    super(msg, cause);
  }
  
  public JsonIOException(Throwable cause) {
    super(cause);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\JsonIOException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */