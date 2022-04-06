package us.myles.viaversion.libs.bungeecordchat.chat;

import java.lang.reflect.Type;
import us.myles.viaversion.libs.bungeecordchat.api.chat.BaseComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.SelectorComponent;
import us.myles.viaversion.libs.gson.JsonDeserializationContext;
import us.myles.viaversion.libs.gson.JsonDeserializer;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.gson.JsonParseException;
import us.myles.viaversion.libs.gson.JsonSerializationContext;
import us.myles.viaversion.libs.gson.JsonSerializer;

public class SelectorComponentSerializer extends BaseComponentSerializer implements JsonSerializer<SelectorComponent>, JsonDeserializer<SelectorComponent> {
  public SelectorComponent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
    JsonObject object = element.getAsJsonObject();
    if (!object.has("selector"))
      throw new JsonParseException("Could not parse JSON: missing 'selector' property"); 
    SelectorComponent component = new SelectorComponent(object.get("selector").getAsString());
    deserialize(object, (BaseComponent)component, context);
    return component;
  }
  
  public JsonElement serialize(SelectorComponent component, Type type, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    serialize(object, (BaseComponent)component, context);
    object.addProperty("selector", component.getSelector());
    return (JsonElement)object;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\bungeecordchat\chat\SelectorComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */