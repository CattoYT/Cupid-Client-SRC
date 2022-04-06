package us.myles.viaversion.libs.bungeecordchat.chat;

import java.lang.reflect.Type;
import java.util.Arrays;
import us.myles.viaversion.libs.bungeecordchat.api.chat.BaseComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.TranslatableComponent;
import us.myles.viaversion.libs.gson.JsonDeserializationContext;
import us.myles.viaversion.libs.gson.JsonDeserializer;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.gson.JsonParseException;
import us.myles.viaversion.libs.gson.JsonSerializationContext;
import us.myles.viaversion.libs.gson.JsonSerializer;

public class TranslatableComponentSerializer extends BaseComponentSerializer implements JsonSerializer<TranslatableComponent>, JsonDeserializer<TranslatableComponent> {
  public TranslatableComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    TranslatableComponent component = new TranslatableComponent();
    JsonObject object = json.getAsJsonObject();
    deserialize(object, (BaseComponent)component, context);
    if (!object.has("translate"))
      throw new JsonParseException("Could not parse JSON: missing 'translate' property"); 
    component.setTranslate(object.get("translate").getAsString());
    if (object.has("with"))
      component.setWith(Arrays.asList((Object[])context.deserialize(object.get("with"), BaseComponent[].class))); 
    return component;
  }
  
  public JsonElement serialize(TranslatableComponent src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    serialize(object, (BaseComponent)src, context);
    object.addProperty("translate", src.getTranslate());
    if (src.getWith() != null)
      object.add("with", context.serialize(src.getWith())); 
    return (JsonElement)object;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\bungeecordchat\chat\TranslatableComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */