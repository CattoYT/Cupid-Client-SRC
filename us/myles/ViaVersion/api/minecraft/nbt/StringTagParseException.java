package us.myles.ViaVersion.api.minecraft.nbt;

import java.io.IOException;

class StringTagParseException extends IOException {
  private static final long serialVersionUID = -3001637554903912905L;
  
  private final CharSequence buffer;
  
  private final int position;
  
  public StringTagParseException(String message, CharSequence buffer, int position) {
    super(message);
    this.buffer = buffer;
    this.position = position;
  }
  
  public String getMessage() {
    return super.getMessage() + "(at position " + this.position + ")";
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\minecraft\nbt\StringTagParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */