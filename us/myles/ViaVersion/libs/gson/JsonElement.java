package us.myles.viaversion.libs.gson;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import us.myles.viaversion.libs.gson.internal.Streams;
import us.myles.viaversion.libs.gson.stream.JsonWriter;

public abstract class JsonElement {
  public abstract JsonElement deepCopy();
  
  public boolean isJsonArray() {
    return this instanceof JsonArray;
  }
  
  public boolean isJsonObject() {
    return this instanceof JsonObject;
  }
  
  public boolean isJsonPrimitive() {
    return this instanceof JsonPrimitive;
  }
  
  public boolean isJsonNull() {
    return this instanceof JsonNull;
  }
  
  public JsonObject getAsJsonObject() {
    if (isJsonObject())
      return (JsonObject)this; 
    throw new IllegalStateException("Not a JSON Object: " + this);
  }
  
  public JsonArray getAsJsonArray() {
    if (isJsonArray())
      return (JsonArray)this; 
    throw new IllegalStateException("Not a JSON Array: " + this);
  }
  
  public JsonPrimitive getAsJsonPrimitive() {
    if (isJsonPrimitive())
      return (JsonPrimitive)this; 
    throw new IllegalStateException("Not a JSON Primitive: " + this);
  }
  
  public JsonNull getAsJsonNull() {
    if (isJsonNull())
      return (JsonNull)this; 
    throw new IllegalStateException("Not a JSON Null: " + this);
  }
  
  public boolean getAsBoolean() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public Number getAsNumber() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public String getAsString() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public double getAsDouble() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public float getAsFloat() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public long getAsLong() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public int getAsInt() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public byte getAsByte() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  @Deprecated
  public char getAsCharacter() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public BigDecimal getAsBigDecimal() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public BigInteger getAsBigInteger() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public short getAsShort() {
    throw new UnsupportedOperationException(getClass().getSimpleName());
  }
  
  public String toString() {
    try {
      StringWriter stringWriter = new StringWriter();
      JsonWriter jsonWriter = new JsonWriter(stringWriter);
      jsonWriter.setLenient(true);
      Streams.write(this, jsonWriter);
      return stringWriter.toString();
    } catch (IOException e) {
      throw new AssertionError(e);
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\JsonElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */