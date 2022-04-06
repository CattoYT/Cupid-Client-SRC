package us.myles.viaversion.libs.gson;

public final class JsonSyntaxException extends JsonParseException {
  private static final long serialVersionUID = 1L;
  
  public JsonSyntaxException(String msg) {
    super(msg);
  }
  
  public JsonSyntaxException(String msg, Throwable cause) {
    super(msg, cause);
  }
  
  public JsonSyntaxException(Throwable cause) {
    super(cause);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\JsonSyntaxException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */