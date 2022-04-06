package us.myles.ViaVersion.protocols.protocol1_13to1_12_2;

import us.myles.ViaVersion.api.rewriters.ComponentRewriter;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data.ComponentRewriter1_13;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.bungeecordchat.api.ChatColor;
import us.myles.viaversion.libs.bungeecordchat.api.chat.BaseComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.TextComponent;
import us.myles.viaversion.libs.bungeecordchat.chat.ComponentSerializer;
import us.myles.viaversion.libs.gson.JsonElement;

public class ChatRewriter {
  private static final ComponentRewriter COMPONENT_REWRITER = (ComponentRewriter)new ComponentRewriter1_13();
  
  public static String fromLegacyTextAsString(String message, ChatColor defaultColor, boolean itemData) {
    TextComponent headComponent = new TextComponent();
    TextComponent component = new TextComponent();
    StringBuilder builder = new StringBuilder();
    if (itemData)
      headComponent.setItalic(Boolean.valueOf(false)); 
    for (int i = 0; i < message.length(); i++) {
      char c = message.charAt(i);
      if (c == '§') {
        if (++i >= message.length())
          break; 
        c = message.charAt(i);
        if (c >= 'A' && c <= 'Z')
          c = (char)(c + 32); 
        ChatColor format = ChatColor.getByChar(c);
        if (format != null) {
          if (builder.length() > 0) {
            TextComponent old = component;
            component = new TextComponent(old);
            old.setText(builder.toString());
            builder = new StringBuilder();
            headComponent.addExtra((BaseComponent)old);
          } 
          if (ChatColor.BOLD.equals(format)) {
            component.setBold(Boolean.valueOf(true));
          } else if (ChatColor.ITALIC.equals(format)) {
            component.setItalic(Boolean.valueOf(true));
          } else if (ChatColor.UNDERLINE.equals(format)) {
            component.setUnderlined(Boolean.valueOf(true));
          } else if (ChatColor.STRIKETHROUGH.equals(format)) {
            component.setStrikethrough(Boolean.valueOf(true));
          } else if (ChatColor.MAGIC.equals(format)) {
            component.setObfuscated(Boolean.valueOf(true));
          } else if (ChatColor.RESET.equals(format)) {
            format = defaultColor;
            component = new TextComponent();
            component.setColor(format);
          } else {
            component = new TextComponent();
            component.setColor(format);
          } 
        } 
      } else {
        builder.append(c);
      } 
    } 
    component.setText(builder.toString());
    headComponent.addExtra((BaseComponent)component);
    return ComponentSerializer.toString((BaseComponent)headComponent);
  }
  
  public static JsonElement fromLegacyText(String message, ChatColor defaultColor) {
    return GsonUtil.getJsonParser().parse(fromLegacyTextAsString(message, defaultColor, false));
  }
  
  public static JsonElement legacyTextToJson(String legacyText) {
    return fromLegacyText(legacyText, ChatColor.WHITE);
  }
  
  public static String legacyTextToJsonString(String legacyText) {
    return fromLegacyTextAsString(legacyText, ChatColor.WHITE, false);
  }
  
  public static String jsonTextToLegacy(String value) {
    return TextComponent.toLegacyText(ComponentSerializer.parse(value));
  }
  
  public static void processTranslate(JsonElement value) {
    COMPONENT_REWRITER.processText(value);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\ChatRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */