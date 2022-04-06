package us.myles.ViaVersion.util;

import us.myles.viaversion.libs.gson.Gson;
import us.myles.viaversion.libs.gson.GsonBuilder;
import us.myles.viaversion.libs.gson.JsonParser;

public final class GsonUtil {
  private static final JsonParser JSON_PARSER = new JsonParser();
  
  private static final Gson GSON = getGsonBuilder().create();
  
  public static Gson getGson() {
    return GSON;
  }
  
  public static GsonBuilder getGsonBuilder() {
    return new GsonBuilder();
  }
  
  public static JsonParser getJsonParser() {
    return JSON_PARSER;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersio\\util\GsonUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */