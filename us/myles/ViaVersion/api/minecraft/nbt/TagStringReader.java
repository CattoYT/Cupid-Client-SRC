package us.myles.ViaVersion.api.minecraft.nbt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import us.myles.viaversion.libs.opennbt.tag.builtin.ByteArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ByteTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.FloatTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.IntTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ListTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.LongTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ShortTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

final class TagStringReader {
  private static final Field NAME_FIELD = getNameField();
  
  private final CharBuffer buffer;
  
  private static Field getNameField() {
    try {
      return Tag.class.getDeclaredField("name");
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e);
    } 
  }
  
  public TagStringReader(CharBuffer buffer) {
    this.buffer = buffer;
  }
  
  public CompoundTag compound() throws StringTagParseException {
    this.buffer.expect('{');
    CompoundTag compoundTag = new CompoundTag("");
    if (this.buffer.peek() == '}') {
      this.buffer.take();
      return compoundTag;
    } 
    while (this.buffer.hasMore()) {
      String key = key();
      Tag tag = tag();
      try {
        if (!NAME_FIELD.isAccessible())
          NAME_FIELD.setAccessible(true); 
        NAME_FIELD.set(tag, key);
      } catch (IllegalAccessException e) {
        throw new IllegalArgumentException(e);
      } 
      compoundTag.put(tag);
      if (separatorOrCompleteWith('}'))
        return compoundTag; 
    } 
    throw this.buffer.makeError("Unterminated compound tag!");
  }
  
  public ListTag list() throws StringTagParseException {
    ListTag listTag = new ListTag("");
    this.buffer.expect('[');
    boolean prefixedIndex = (this.buffer.peek() == '0' && this.buffer.peek(1) == ':');
    while (this.buffer.hasMore()) {
      if (prefixedIndex)
        this.buffer.takeUntil(':'); 
      Tag next = tag();
      listTag.add(next);
      if (separatorOrCompleteWith(']'))
        return listTag; 
    } 
    throw this.buffer.makeError("Reached end of file without end of list tag!");
  }
  
  public Tag array(char elementType) throws StringTagParseException {
    this.buffer.expect('[')
      .expect(elementType)
      .expect(';');
    if (elementType == 'B')
      return (Tag)new ByteArrayTag("", byteArray()); 
    if (elementType == 'I')
      return (Tag)new IntArrayTag("", intArray()); 
    if (elementType == 'L')
      return (Tag)new LongArrayTag("", longArray()); 
    throw this.buffer.makeError("Type " + elementType + " is not a valid element type in an array!");
  }
  
  private byte[] byteArray() throws StringTagParseException {
    List<Byte> bytes = new ArrayList<>();
    while (this.buffer.hasMore()) {
      CharSequence value = this.buffer.skipWhitespace().takeUntil('B');
      try {
        bytes.add(Byte.valueOf(value.toString()));
      } catch (NumberFormatException ex) {
        throw this.buffer.makeError("All elements of a byte array must be bytes!");
      } 
      if (separatorOrCompleteWith(']')) {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
          result[i] = ((Byte)bytes.get(i)).byteValue(); 
        return result;
      } 
    } 
    throw this.buffer.makeError("Reached end of document without array close");
  }
  
  private int[] intArray() throws StringTagParseException {
    IntStream.Builder builder = IntStream.builder();
    while (this.buffer.hasMore()) {
      Tag value = tag();
      if (!(value instanceof IntTag))
        throw this.buffer.makeError("All elements of an int array must be ints!"); 
      builder.add(((IntTag)value).getValue().intValue());
      if (separatorOrCompleteWith(']'))
        return builder.build().toArray(); 
    } 
    throw this.buffer.makeError("Reached end of document without array close");
  }
  
  private long[] longArray() throws StringTagParseException {
    List<Long> longs = new ArrayList<>();
    while (this.buffer.hasMore()) {
      CharSequence value = this.buffer.skipWhitespace().takeUntil('L');
      try {
        longs.add(Long.valueOf(value.toString()));
      } catch (NumberFormatException ex) {
        throw this.buffer.makeError("All elements of a long array must be longs!");
      } 
      if (separatorOrCompleteWith(']')) {
        long[] result = new long[longs.size()];
        for (int i = 0; i < longs.size(); i++)
          result[i] = ((Long)longs.get(i)).longValue(); 
        return result;
      } 
    } 
    throw this.buffer.makeError("Reached end of document without array close");
  }
  
  public String key() throws StringTagParseException {
    this.buffer.skipWhitespace();
    char starChar = this.buffer.peek();
    try {
      if (starChar == '\'' || starChar == '"')
        return unescape(this.buffer.takeUntil(this.buffer.take()).toString()); 
      StringBuilder builder = new StringBuilder();
      while (this.buffer.peek() != ':')
        builder.append(this.buffer.take()); 
      return builder.toString();
    } finally {
      this.buffer.expect(':');
    } 
  }
  
  public Tag tag() throws StringTagParseException {
    char startToken = this.buffer.skipWhitespace().peek();
    switch (startToken) {
      case '{':
        return (Tag)compound();
      case '[':
        if (this.buffer.peek(2) == ';')
          return array(this.buffer.peek(1)); 
        return (Tag)list();
      case '"':
      case '\'':
        this.buffer.advance();
        return (Tag)new StringTag("", unescape(this.buffer.takeUntil(startToken).toString()));
    } 
    return scalar();
  }
  
  private Tag scalar() {
    StringBuilder builder = new StringBuilder();
    boolean possiblyNumeric = true;
    while (this.buffer.hasMore()) {
      char current = this.buffer.peek();
      if (possiblyNumeric && !Tokens.numeric(current) && 
        builder.length() != 0) {
        DoubleTag doubleTag;
        Tag result = null;
        try {
          ByteTag byteTag;
          ShortTag shortTag;
          LongTag longTag;
          FloatTag floatTag;
          switch (Character.toUpperCase(current)) {
            case 'B':
              byteTag = new ByteTag("", Byte.parseByte(builder.toString()));
              break;
            case 'S':
              shortTag = new ShortTag("", Short.parseShort(builder.toString()));
              break;
            case 'L':
              longTag = new LongTag("", Long.parseLong(builder.toString()));
              break;
            case 'F':
              floatTag = new FloatTag("", Float.parseFloat(builder.toString()));
              break;
            case 'D':
              doubleTag = new DoubleTag("", Double.parseDouble(builder.toString()));
              break;
          } 
        } catch (NumberFormatException ex) {
          possiblyNumeric = false;
        } 
        if (doubleTag != null) {
          this.buffer.take();
          return (Tag)doubleTag;
        } 
      } 
      if (current == '\\') {
        this.buffer.advance();
        builder.append(this.buffer.take());
        continue;
      } 
      if (Tokens.id(current))
        builder.append(this.buffer.take()); 
    } 
    String built = builder.toString();
    if (possiblyNumeric)
      try {
        return (Tag)new IntTag("", Integer.parseInt(built));
      } catch (NumberFormatException numberFormatException) {} 
    return (Tag)new StringTag("", built);
  }
  
  private boolean separatorOrCompleteWith(char endCharacter) throws StringTagParseException {
    if (this.buffer.skipWhitespace().peek() == endCharacter) {
      this.buffer.take();
      return true;
    } 
    this.buffer.expect(',');
    return false;
  }
  
  private static String unescape(String withEscapes) {
    int escapeIdx = withEscapes.indexOf('\\');
    if (escapeIdx == -1)
      return withEscapes; 
    int lastEscape = 0;
    StringBuilder output = new StringBuilder(withEscapes.length());
    do {
      output.append(withEscapes, lastEscape, escapeIdx);
      lastEscape = escapeIdx + 1;
    } while ((escapeIdx = withEscapes.indexOf('\\', lastEscape + 1)) != -1);
    output.append(withEscapes.substring(lastEscape));
    return output.toString();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\minecraft\nbt\TagStringReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */