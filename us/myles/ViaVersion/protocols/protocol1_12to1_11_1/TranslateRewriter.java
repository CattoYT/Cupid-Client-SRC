package us.myles.ViaVersion.protocols.protocol1_12to1_11_1;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.rewriters.ComponentRewriter;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.data.AchievementTranslationMapping;
import us.myles.viaversion.libs.gson.JsonArray;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;

public class TranslateRewriter {
  private static final ComponentRewriter achievementTextRewriter = new ComponentRewriter() {
      protected void handleTranslate(JsonObject object, String translate) {
        String text = AchievementTranslationMapping.get(translate);
        if (text != null)
          object.addProperty("translate", text); 
      }
      
      protected void handleHoverEvent(JsonObject hoverEvent) {
        String textValue, action = hoverEvent.getAsJsonPrimitive("action").getAsString();
        if (!action.equals("show_achievement")) {
          super.handleHoverEvent(hoverEvent);
          return;
        } 
        JsonElement value = hoverEvent.get("value");
        if (value.isJsonObject()) {
          textValue = value.getAsJsonObject().get("text").getAsString();
        } else {
          textValue = value.getAsJsonPrimitive().getAsString();
        } 
        if (AchievementTranslationMapping.get(textValue) == null) {
          JsonObject invalidText = new JsonObject();
          invalidText.addProperty("text", "Invalid statistic/achievement!");
          invalidText.addProperty("color", "red");
          hoverEvent.addProperty("action", "show_text");
          hoverEvent.add("value", (JsonElement)invalidText);
          super.handleHoverEvent(hoverEvent);
          return;
        } 
        try {
          JsonObject newLine = new JsonObject();
          newLine.addProperty("text", "\n");
          JsonArray baseArray = new JsonArray();
          baseArray.add("");
          JsonObject namePart = new JsonObject();
          JsonObject typePart = new JsonObject();
          baseArray.add((JsonElement)namePart);
          baseArray.add((JsonElement)newLine);
          baseArray.add((JsonElement)typePart);
          if (textValue.startsWith("achievement")) {
            namePart.addProperty("translate", textValue);
            namePart.addProperty("color", AchievementTranslationMapping.isSpecial(textValue) ? "dark_purple" : "green");
            typePart.addProperty("translate", "stats.tooltip.type.achievement");
            JsonObject description = new JsonObject();
            typePart.addProperty("italic", Boolean.valueOf(true));
            description.addProperty("translate", value + ".desc");
            baseArray.add((JsonElement)newLine);
            baseArray.add((JsonElement)description);
          } else if (textValue.startsWith("stat")) {
            namePart.addProperty("translate", textValue);
            namePart.addProperty("color", "gray");
            typePart.addProperty("translate", "stats.tooltip.type.statistic");
            typePart.addProperty("italic", Boolean.valueOf(true));
          } 
          hoverEvent.addProperty("action", "show_text");
          hoverEvent.add("value", (JsonElement)baseArray);
        } catch (Exception e) {
          Via.getPlatform().getLogger().warning("Error rewriting show_achievement: " + hoverEvent);
          e.printStackTrace();
          JsonObject invalidText = new JsonObject();
          invalidText.addProperty("text", "Invalid statistic/achievement!");
          invalidText.addProperty("color", "red");
          hoverEvent.addProperty("action", "show_text");
          hoverEvent.add("value", (JsonElement)invalidText);
        } 
        super.handleHoverEvent(hoverEvent);
      }
    };
  
  public static void toClient(JsonElement element, UserConnection user) {
    if (element instanceof JsonObject) {
      JsonObject obj = (JsonObject)element;
      JsonElement translate = obj.get("translate");
      if (translate != null && 
        translate.getAsString().startsWith("chat.type.achievement"))
        achievementTextRewriter.processText((JsonElement)obj); 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_12to1_11_1\TranslateRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */