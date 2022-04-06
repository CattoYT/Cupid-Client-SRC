package us.myles.ViaVersion.protocols.protocol1_12to1_11_1;

import java.util.regex.Pattern;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.viaversion.libs.gson.JsonArray;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.gson.JsonPrimitive;

public class ChatItemRewriter {
  private static final Pattern indexRemoval = Pattern.compile("(?<![\\w-.+])\\d+:(?=([^\"\\\\]*(\\\\.|\"([^\"\\\\]*\\\\.)*[^\"\\\\]*\"))*[^\"]*$)");
  
  public static void toClient(JsonElement element, UserConnection user) {
    if (element instanceof JsonObject) {
      JsonObject obj = (JsonObject)element;
      if (obj.has("hoverEvent")) {
        if (obj.get("hoverEvent") instanceof JsonObject) {
          JsonObject hoverEvent = (JsonObject)obj.get("hoverEvent");
          if (hoverEvent.has("action") && hoverEvent.has("value")) {
            String type = hoverEvent.get("action").getAsString();
            if (type.equals("show_item") || type.equals("show_entity")) {
              JsonElement value = hoverEvent.get("value");
              if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                String newValue = indexRemoval.matcher(value.getAsString()).replaceAll("");
                hoverEvent.addProperty("value", newValue);
              } else if (value.isJsonArray()) {
                JsonArray newArray = new JsonArray();
                for (JsonElement valueElement : value.getAsJsonArray()) {
                  if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
                    String newValue = indexRemoval.matcher(valueElement.getAsString()).replaceAll("");
                    newArray.add((JsonElement)new JsonPrimitive(newValue));
                  } 
                } 
                hoverEvent.add("value", (JsonElement)newArray);
              } 
            } 
          } 
        } 
      } else if (obj.has("extra")) {
        toClient(obj.get("extra"), user);
      } 
    } else if (element instanceof JsonArray) {
      JsonArray array = (JsonArray)element;
      for (JsonElement value : array)
        toClient(value, user); 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_12to1_11_1\ChatItemRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */