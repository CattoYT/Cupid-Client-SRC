package us.myles.ViaVersion.api.minecraft.nbt;

import java.io.IOException;
import java.io.Writer;
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

final class TagStringWriter implements AutoCloseable {
  private final Appendable out;
  
  private final String indent = "  ";
  
  private int level;
  
  private boolean needsSeparator;
  
  public TagStringWriter(Appendable out) {
    this.out = out;
  }
  
  public TagStringWriter writeTag(Tag tag) throws IOException {
    if (tag instanceof CompoundTag)
      return writeCompound((CompoundTag)tag); 
    if (tag instanceof ListTag)
      return writeList((ListTag)tag); 
    if (tag instanceof ByteArrayTag)
      return writeByteArray((ByteArrayTag)tag); 
    if (tag instanceof IntArrayTag)
      return writeIntArray((IntArrayTag)tag); 
    if (tag instanceof LongArrayTag)
      return writeLongArray((LongArrayTag)tag); 
    if (tag instanceof StringTag)
      return value(((StringTag)tag).getValue(), false); 
    if (tag instanceof ByteTag)
      return value(Byte.toString(((ByteTag)tag).getValue().byteValue()), 'B'); 
    if (tag instanceof ShortTag)
      return value(Short.toString(((ShortTag)tag).getValue().shortValue()), 'S'); 
    if (tag instanceof IntTag)
      return value(Integer.toString(((IntTag)tag).getValue().intValue()), 'I'); 
    if (tag instanceof LongTag)
      return value(Long.toString(((LongTag)tag).getValue().longValue()), 'L'); 
    if (tag instanceof FloatTag)
      return value(Float.toString(((FloatTag)tag).getValue().floatValue()), 'F'); 
    if (tag instanceof DoubleTag)
      return value(Double.toString(((DoubleTag)tag).getValue().doubleValue()), 'D'); 
    throw new IOException("Unknown tag type: " + tag.getClass().getSimpleName());
  }
  
  private TagStringWriter writeCompound(CompoundTag tag) throws IOException {
    beginCompound();
    for (Tag t : tag) {
      key(t.getName());
      writeTag(t);
    } 
    endCompound();
    return this;
  }
  
  private TagStringWriter writeList(ListTag tag) throws IOException {
    beginList();
    for (Tag el : tag) {
      printAndResetSeparator();
      writeTag(el);
    } 
    endList();
    return this;
  }
  
  private TagStringWriter writeByteArray(ByteArrayTag tag) throws IOException {
    beginArray('B');
    byte[] value = tag.getValue();
    for (int i = 0, length = value.length; i < length; i++) {
      printAndResetSeparator();
      value(Byte.toString(value[i]), 'B');
    } 
    endArray();
    return this;
  }
  
  private TagStringWriter writeIntArray(IntArrayTag tag) throws IOException {
    beginArray('I');
    int[] value = tag.getValue();
    for (int i = 0, length = value.length; i < length; i++) {
      printAndResetSeparator();
      value(Integer.toString(value[i]), 'I');
    } 
    endArray();
    return this;
  }
  
  private TagStringWriter writeLongArray(LongArrayTag tag) throws IOException {
    beginArray('L');
    long[] value = tag.getValue();
    for (int i = 0, length = value.length; i < length; i++) {
      printAndResetSeparator();
      value(Long.toString(value[i]), 'L');
    } 
    endArray();
    return this;
  }
  
  public TagStringWriter beginCompound() throws IOException {
    printAndResetSeparator();
    this.level++;
    this.out.append('{');
    return this;
  }
  
  public TagStringWriter endCompound() throws IOException {
    this.out.append('}');
    this.level--;
    this.needsSeparator = true;
    return this;
  }
  
  public TagStringWriter key(String key) throws IOException {
    printAndResetSeparator();
    writeMaybeQuoted(key, false);
    this.out.append(':');
    return this;
  }
  
  public TagStringWriter value(String value, char valueType) throws IOException {
    if (valueType == '\000') {
      writeMaybeQuoted(value, true);
    } else {
      this.out.append(value);
      if (valueType != 'I')
        this.out.append(valueType); 
    } 
    this.needsSeparator = true;
    return this;
  }
  
  public TagStringWriter beginList() throws IOException {
    printAndResetSeparator();
    this.level++;
    this.out.append('[');
    return this;
  }
  
  public TagStringWriter endList() throws IOException {
    this.out.append(']');
    this.level--;
    this.needsSeparator = true;
    return this;
  }
  
  private TagStringWriter beginArray(char type) throws IOException {
    (beginList()).out
      .append(type)
      .append(';');
    return this;
  }
  
  private TagStringWriter endArray() throws IOException {
    return endList();
  }
  
  private void writeMaybeQuoted(String content, boolean requireQuotes) throws IOException {
    if (!requireQuotes)
      for (int i = 0; i < content.length(); i++) {
        if (!Tokens.id(content.charAt(i))) {
          requireQuotes = true;
          break;
        } 
      }  
    if (requireQuotes) {
      this.out.append('"');
      this.out.append(escape(content, '"'));
      this.out.append('"');
    } else {
      this.out.append(content);
    } 
  }
  
  private static String escape(String content, char quoteChar) {
    StringBuilder output = new StringBuilder(content.length());
    for (int i = 0; i < content.length(); i++) {
      char c = content.charAt(i);
      if (c == quoteChar || c == '\\')
        output.append('\\'); 
      output.append(c);
    } 
    return output.toString();
  }
  
  private void printAndResetSeparator() throws IOException {
    if (this.needsSeparator) {
      this.out.append(',');
      this.needsSeparator = false;
    } 
  }
  
  public void close() throws IOException {
    if (this.level != 0)
      throw new IllegalStateException("Document finished with unbalanced start and end objects"); 
    if (this.out instanceof Writer)
      ((Writer)this.out).flush(); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\minecraft\nbt\TagStringWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */