package us.myles.viaversion.libs.gson.internal;

import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonIOException;
import us.myles.viaversion.libs.gson.JsonNull;
import us.myles.viaversion.libs.gson.JsonParseException;
import us.myles.viaversion.libs.gson.JsonSyntaxException;
import us.myles.viaversion.libs.gson.internal.bind.TypeAdapters;
import us.myles.viaversion.libs.gson.stream.JsonReader;
import us.myles.viaversion.libs.gson.stream.JsonWriter;
import us.myles.viaversion.libs.gson.stream.MalformedJsonException;

public final class Streams {
  private Streams() {
    throw new UnsupportedOperationException();
  }
  
  public static JsonElement parse(JsonReader reader) throws JsonParseException {
    boolean isEmpty = true;
    try {
      reader.peek();
      isEmpty = false;
      return (JsonElement)TypeAdapters.JSON_ELEMENT.read(reader);
    } catch (EOFException e) {
      if (isEmpty)
        return (JsonElement)JsonNull.INSTANCE; 
      throw new JsonSyntaxException(e);
    } catch (MalformedJsonException e) {
      throw new JsonSyntaxException(e);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } catch (NumberFormatException e) {
      throw new JsonSyntaxException(e);
    } 
  }
  
  public static void write(JsonElement element, JsonWriter writer) throws IOException {
    TypeAdapters.JSON_ELEMENT.write(writer, element);
  }
  
  public static Writer writerForAppendable(Appendable appendable) {
    return (appendable instanceof Writer) ? (Writer)appendable : new AppendableWriter(appendable);
  }
  
  private static final class AppendableWriter extends Writer {
    private final Appendable appendable;
    
    private final CurrentWrite currentWrite = new CurrentWrite();
    
    AppendableWriter(Appendable appendable) {
      this.appendable = appendable;
    }
    
    public void write(char[] chars, int offset, int length) throws IOException {
      this.currentWrite.chars = chars;
      this.appendable.append(this.currentWrite, offset, offset + length);
    }
    
    public void write(int i) throws IOException {
      this.appendable.append((char)i);
    }
    
    public void flush() {}
    
    public void close() {}
    
    static class CurrentWrite implements CharSequence {
      char[] chars;
      
      public int length() {
        return this.chars.length;
      }
      
      public char charAt(int i) {
        return this.chars[i];
      }
      
      public CharSequence subSequence(int start, int end) {
        return new String(this.chars, start, end - start);
      }
    }
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\internal\Streams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */