package us.myles.viaversion.libs.bungeecordchat.chat;

import java.lang.reflect.Type;
import us.myles.viaversion.libs.bungeecordchat.api.chat.BaseComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.KeybindComponent;
import us.myles.viaversion.libs.gson.JsonDeserializationContext;
import us.myles.viaversion.libs.gson.JsonDeserializer;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.gson.JsonParseException;
import us.myles.viaversion.libs.gson.JsonSerializationContext;
import us.myles.viaversion.libs.gson.JsonSerializer;

public class KeybindComponentSerializer extends BaseComponentSerializer implements JsonSerializer<KeybindComponent>, JsonDeserializer<KeybindComponent> {
  public KeybindComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    if (!object.has("keybind"))
      throw new JsonParseException("Could not parse JSON: missing 'keybind' property"); 
    KeybindComponent component = new KeybindComponent();
    deserialize(object, (BaseComponent)component, context);
    component.setKeybind(object.get("keybind").getAsString());
    return component;
  }
  
  public JsonElement serialize(KeybindComponent src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    serialize(object, (BaseComponent)src, context);
    object.addProperty("keybind", src.getKeybind());
    return (JsonElement)object;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\bungeecordchat\chat\KeybindComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */