package us.myles.ViaVersion.api.minecraft.nbt;

final class CharBuffer {
  private final CharSequence sequence;
  
  private int index;
  
  CharBuffer(CharSequence sequence) {
    this.sequence = sequence;
  }
  
  public char peek() {
    return this.sequence.charAt(this.index);
  }
  
  public char peek(int offset) {
    return this.sequence.charAt(this.index + offset);
  }
  
  public char take() {
    return this.sequence.charAt(this.index++);
  }
  
  public boolean advance() {
    this.index++;
    return hasMore();
  }
  
  public boolean hasMore() {
    return (this.index < this.sequence.length());
  }
  
  public CharSequence takeUntil(char until) throws StringTagParseException {
    until = Character.toLowerCase(until);
    int endIdx = -1;
    for (int idx = this.index; idx < this.sequence.length(); idx++) {
      if (this.sequence.charAt(idx) == '\\') {
        idx++;
      } else if (Character.toLowerCase(this.sequence.charAt(idx)) == until) {
        endIdx = idx;
        break;
      } 
    } 
    if (endIdx == -1)
      throw makeError("No occurrence of " + until + " was found"); 
    CharSequence result = this.sequence.subSequence(this.index, endIdx);
    this.index = endIdx + 1;
    return result;
  }
  
  public CharBuffer expect(char expectedChar) throws StringTagParseException {
    skipWhitespace();
    if (!hasMore())
      throw makeError("Expected character '" + expectedChar + "' but got EOF"); 
    if (peek() != expectedChar)
      throw makeError("Expected character '" + expectedChar + "' but got '" + peek() + "'"); 
    take();
    return this;
  }
  
  public CharBuffer skipWhitespace() {
    for (; hasMore() && Character.isWhitespace(peek()); advance());
    return this;
  }
  
  public StringTagParseException makeError(String message) {
    return new StringTagParseException(message, this.sequence, this.index);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\minecraft\nbt\CharBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */