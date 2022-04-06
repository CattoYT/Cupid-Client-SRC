package us.myles.viaversion.libs.bungeecordchat.chat;

import java.lang.reflect.Type;
import java.util.Set;
import us.myles.viaversion.libs.bungeecordchat.api.chat.BaseComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.ItemTag;
import us.myles.viaversion.libs.bungeecordchat.api.chat.KeybindComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.ScoreComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.SelectorComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.TextComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.TranslatableComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.hover.content.Entity;
import us.myles.viaversion.libs.bungeecordchat.api.chat.hover.content.EntitySerializer;
import us.myles.viaversion.libs.bungeecordchat.api.chat.hover.content.Item;
import us.myles.viaversion.libs.bungeecordchat.api.chat.hover.content.ItemSerializer;
import us.myles.viaversion.libs.bungeecordchat.api.chat.hover.content.Text;
import us.myles.viaversion.libs.bungeecordchat.api.chat.hover.content.TextSerializer;
import us.myles.viaversion.libs.gson.Gson;
import us.myles.viaversion.libs.gson.GsonBuilder;
import us.myles.viaversion.libs.gson.JsonDeserializationContext;
import us.myles.viaversion.libs.gson.JsonDeserializer;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.gson.JsonParseException;
import us.myles.viaversion.libs.gson.JsonParser;

public class ComponentSerializer implements JsonDeserializer<BaseComponent> {
  private static final JsonParser JSON_PARSER = new JsonParser();
  
  private static final Gson gson = (new GsonBuilder())
    .registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
    .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
    .registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
    .registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer())
    .registerTypeAdapter(ScoreComponent.class, new ScoreComponentSerializer())
    .registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer())
    .registerTypeAdapter(Entity.class, new EntitySerializer())
    .registerTypeAdapter(Text.class, new TextSerializer())
    .registerTypeAdapter(Item.class, new ItemSerializer())
    .registerTypeAdapter(ItemTag.class, new ItemTag.Serializer())
    .create();
  
  public static final ThreadLocal<Set<BaseComponent>> serializedComponents = new ThreadLocal<>();
  
  public static BaseComponent[] parse(String json) {
    JsonElement jsonElement = JSON_PARSER.parse(json);
    if (jsonElement.isJsonArray())
      return (BaseComponent[])gson.fromJson(jsonElement, BaseComponent[].class); 
    return new BaseComponent[] { (BaseComponent)gson
        
        .fromJson(jsonElement, BaseComponent.class) };
  }
  
  public static String toString(Object object) {
    return gson.toJson(object);
  }
  
  public static String toString(BaseComponent component) {
    return gson.toJson(component);
  }
  
  public static String toString(BaseComponent... components) {
    if (components.length == 1)
      return gson.toJson(components[0]); 
    return gson.toJson(new TextComponent(components));
  }
  
  public BaseComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (json.isJsonPrimitive())
      return (BaseComponent)new TextComponent(json.getAsString()); 
    JsonObject object = json.getAsJsonObject();
    if (object.has("translate"))
      return (BaseComponent)context.deserialize(json, TranslatableComponent.class); 
    if (object.has("keybind"))
      return (BaseComponent)context.deserialize(json, KeybindComponent.class); 
    if (object.has("score"))
      return (BaseComponent)context.deserialize(json, ScoreComponent.class); 
    if (object.has("selector"))
      return (BaseComponent)context.deserialize(json, SelectorComponent.class); 
    return (BaseComponent)context.deserialize(json, TextComponent.class);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\bungeecordchat\chat\ComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */