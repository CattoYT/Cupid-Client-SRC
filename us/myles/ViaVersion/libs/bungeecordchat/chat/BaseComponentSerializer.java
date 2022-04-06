package us.myles.viaversion.libs.bungeecordchat.chat;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import us.myles.viaversion.libs.bungeecordchat.api.ChatColor;
import us.myles.viaversion.libs.bungeecordchat.api.chat.BaseComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.ClickEvent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.HoverEvent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.hover.content.Content;
import us.myles.viaversion.libs.gson.JsonDeserializationContext;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.gson.JsonParseException;
import us.myles.viaversion.libs.gson.JsonSerializationContext;

public class BaseComponentSerializer {
  protected void deserialize(JsonObject object, BaseComponent component, JsonDeserializationContext context) {
    HoverEvent hoverEvent;
    if (object.has("color"))
      component.setColor(ChatColor.of(object.get("color").getAsString())); 
    if (object.has("font"))
      component.setFont(object.get("font").getAsString()); 
    if (object.has("bold"))
      component.setBold(Boolean.valueOf(object.get("bold").getAsBoolean())); 
    if (object.has("italic"))
      component.setItalic(Boolean.valueOf(object.get("italic").getAsBoolean())); 
    if (object.has("underlined"))
      component.setUnderlined(Boolean.valueOf(object.get("underlined").getAsBoolean())); 
    if (object.has("strikethrough"))
      component.setStrikethrough(Boolean.valueOf(object.get("strikethrough").getAsBoolean())); 
    if (object.has("obfuscated"))
      component.setObfuscated(Boolean.valueOf(object.get("obfuscated").getAsBoolean())); 
    if (object.has("insertion"))
      component.setInsertion(object.get("insertion").getAsString()); 
    if (object.has("extra"))
      component.setExtra(Arrays.asList((Object[])context.deserialize(object.get("extra"), BaseComponent[].class))); 
    if (object.has("clickEvent")) {
      JsonObject event = object.getAsJsonObject("clickEvent");
      component.setClickEvent(new ClickEvent(
            ClickEvent.Action.valueOf(event.get("action").getAsString().toUpperCase(Locale.ROOT)), 
            event.has("value") ? event.get("value").getAsString() : ""));
    } 
    if (object.has("hoverEvent")) {
      JsonObject event = object.getAsJsonObject("hoverEvent");
      hoverEvent = null;
      HoverEvent.Action action = HoverEvent.Action.valueOf(event.get("action").getAsString().toUpperCase(Locale.ROOT));
      Iterator<String> iterator = Arrays.<String>asList(new String[] { "value", "contents" }).iterator();
      while (true) {
        if (iterator.hasNext()) {
          String type = iterator.next();
          if (!event.has(type))
            continue; 
          JsonElement contents = event.get(type);
          try {
            BaseComponent[] components;
            if (contents.isJsonArray()) {
              components = (BaseComponent[])context.deserialize(contents, BaseComponent[].class);
            } else {
              components = new BaseComponent[] { (BaseComponent)context.deserialize(contents, BaseComponent.class) };
            } 
            hoverEvent = new HoverEvent(action, components);
            break;
          } catch (JsonParseException ex) {
            Content[] list;
            if (contents.isJsonArray()) {
              list = (Content[])context.deserialize(contents, HoverEvent.getClass(action, true));
            } else {
              list = new Content[] { (Content)context.deserialize(contents, HoverEvent.getClass(action, false)) };
            } 
            hoverEvent = new HoverEvent(action, new ArrayList(Arrays.asList((Object[])list)));
          } 
        } else {
          break;
        } 
        if (hoverEvent != null)
          component.setHoverEvent(hoverEvent); 
        return;
      } 
    } else {
      return;
    } 
    if (hoverEvent != null)
      component.setHoverEvent(hoverEvent); 
  }
  
  protected void serialize(JsonObject object, BaseComponent component, JsonSerializationContext context) {
    boolean first = false;
    if (ComponentSerializer.serializedComponents.get() == null) {
      first = true;
      ComponentSerializer.serializedComponents.set(Collections.newSetFromMap(new IdentityHashMap<>()));
    } 
    try {
      Preconditions.checkArgument(!((Set)ComponentSerializer.serializedComponents.get()).contains(component), "Component loop");
      ((Set<BaseComponent>)ComponentSerializer.serializedComponents.get()).add(component);
      if (component.getColorRaw() != null)
        object.addProperty("color", component.getColorRaw().getName()); 
      if (component.getFontRaw() != null)
        object.addProperty("font", component.getFontRaw()); 
      if (component.isBoldRaw() != null)
        object.addProperty("bold", component.isBoldRaw()); 
      if (component.isItalicRaw() != null)
        object.addProperty("italic", component.isItalicRaw()); 
      if (component.isUnderlinedRaw() != null)
        object.addProperty("underlined", component.isUnderlinedRaw()); 
      if (component.isStrikethroughRaw() != null)
        object.addProperty("strikethrough", component.isStrikethroughRaw()); 
      if (component.isObfuscatedRaw() != null)
        object.addProperty("obfuscated", component.isObfuscatedRaw()); 
      if (component.getInsertion() != null)
        object.addProperty("insertion", component.getInsertion()); 
      if (component.getExtra() != null)
        object.add("extra", context.serialize(component.getExtra())); 
      if (component.getClickEvent() != null) {
        JsonObject clickEvent = new JsonObject();
        clickEvent.addProperty("action", component.getClickEvent().getAction().toString().toLowerCase(Locale.ROOT));
        clickEvent.addProperty("value", component.getClickEvent().getValue());
        object.add("clickEvent", (JsonElement)clickEvent);
      } 
      if (component.getHoverEvent() != null) {
        JsonObject hoverEvent = new JsonObject();
        hoverEvent.addProperty("action", component.getHoverEvent().getAction().toString().toLowerCase(Locale.ROOT));
        if (component.getHoverEvent().isLegacy()) {
          hoverEvent.add("value", context.serialize(component.getHoverEvent().getContents().get(0)));
        } else {
          hoverEvent.add("contents", context.serialize((component.getHoverEvent().getContents().size() == 1) ? component
                .getHoverEvent().getContents().get(0) : component.getHoverEvent().getContents()));
        } 
        object.add("hoverEvent", (JsonElement)hoverEvent);
      } 
    } finally {
      ((Set)ComponentSerializer.serializedComponents.get()).remove(component);
      if (first)
        ComponentSerializer.serializedComponents.set(null); 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\bungeecordchat\chat\BaseComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */