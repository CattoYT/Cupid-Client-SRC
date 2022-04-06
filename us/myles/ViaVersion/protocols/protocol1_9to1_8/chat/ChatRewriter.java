package us.myles.ViaVersion.protocols.protocol1_9to1_8.chat;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import us.myles.viaversion.libs.gson.JsonArray;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;

public class ChatRewriter {
  public static void toClient(JsonObject obj, UserConnection user) {
    if (obj.get("translate") != null && obj.get("translate").getAsString().equals("gameMode.changed")) {
      String gameMode = ((EntityTracker1_9)user.get(EntityTracker1_9.class)).getGameMode().getText();
      JsonObject gameModeObject = new JsonObject();
      gameModeObject.addProperty("text", gameMode);
      gameModeObject.addProperty("color", "gray");
      gameModeObject.addProperty("italic", Boolean.valueOf(true));
      JsonArray array = new JsonArray();
      array.add((JsonElement)gameModeObject);
      obj.add("with", (JsonElement)array);
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\chat\ChatRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */