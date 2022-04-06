package us.myles.viaversion.libs.bungeecordchat.api.chat.hover.content;

import java.lang.reflect.Type;
import us.myles.viaversion.libs.bungeecordchat.api.chat.BaseComponent;
import us.myles.viaversion.libs.gson.JsonDeserializationContext;
import us.myles.viaversion.libs.gson.JsonDeserializer;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonParseException;
import us.myles.viaversion.libs.gson.JsonSerializationContext;
import us.myles.viaversion.libs.gson.JsonSerializer;

public class TextSerializer implements JsonSerializer<Text>, JsonDeserializer<Text> {
  public Text deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
    if (element.isJsonArray())
      return new Text((BaseComponent[])context.deserialize(element, BaseComponent[].class)); 
    if (element.isJsonPrimitive())
      return new Text(element.getAsJsonPrimitive().getAsString()); 
    return new Text(new BaseComponent[] { (BaseComponent)context
          
          .deserialize(element, BaseComponent.class) });
  }
  
  public JsonElement serialize(Text content, Type type, JsonSerializationContext context) {
    return context.serialize(content.getValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\bungeecordchat\api\chat\hover\content\TextSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */