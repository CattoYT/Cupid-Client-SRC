package us.myles.viaversion.libs.gson;

public enum LongSerializationPolicy {
  DEFAULT {
    public JsonElement serialize(Long value) {
      return new JsonPrimitive(value);
    }
  },
  STRING {
    public JsonElement serialize(Long value) {
      return new JsonPrimitive(String.valueOf(value));
    }
  };
  
  public abstract JsonElement serialize(Long paramLong);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\LongSerializationPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */