package us.myles.ViaVersion.api.rewriters;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.gson.JsonPrimitive;
import us.myles.viaversion.libs.gson.JsonSyntaxException;

public class ComponentRewriter {
  protected final Protocol protocol;
  
  public ComponentRewriter(Protocol protocol) {
    this.protocol = protocol;
  }
  
  public ComponentRewriter() {
    this.protocol = null;
  }
  
  public void registerChatMessage(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> ComponentRewriter.this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT)));
          }
        });
  }
  
  public void registerBossBar(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  if (action == 0 || action == 3)
                    ComponentRewriter.this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT)); 
                });
          }
        });
  }
  
  public void registerCombatEvent(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  if (((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue() == 2) {
                    wrapper.passthrough((Type)Type.VAR_INT);
                    wrapper.passthrough(Type.INT);
                    ComponentRewriter.this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT));
                  } 
                });
          }
        });
  }
  
  public void registerTitle(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int action = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  if (action >= 0 && action <= 2)
                    ComponentRewriter.this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT)); 
                });
          }
        });
  }
  
  public JsonElement processText(String value) {
    try {
      JsonElement root = GsonUtil.getJsonParser().parse(value);
      processText(root);
      return root;
    } catch (JsonSyntaxException e) {
      if (Via.getManager().isDebug()) {
        Via.getPlatform().getLogger().severe("Error when trying to parse json: " + value);
        throw e;
      } 
      return (JsonElement)new JsonPrimitive(value);
    } 
  }
  
  public void processText(JsonElement element) {
    if (element == null || element.isJsonNull())
      return; 
    if (element.isJsonArray()) {
      processAsArray(element);
      return;
    } 
    if (element.isJsonPrimitive()) {
      handleText(element.getAsJsonPrimitive());
      return;
    } 
    JsonObject object = element.getAsJsonObject();
    JsonPrimitive text = object.getAsJsonPrimitive("text");
    if (text != null)
      handleText(text); 
    JsonElement translate = object.get("translate");
    if (translate != null) {
      handleTranslate(object, translate.getAsString());
      JsonElement with = object.get("with");
      if (with != null)
        processAsArray(with); 
    } 
    JsonElement extra = object.get("extra");
    if (extra != null)
      processAsArray(extra); 
    JsonObject hoverEvent = object.getAsJsonObject("hoverEvent");
    if (hoverEvent != null)
      handleHoverEvent(hoverEvent); 
  }
  
  protected void handleText(JsonPrimitive text) {}
  
  protected void handleTranslate(JsonObject object, String translate) {}
  
  protected void handleHoverEvent(JsonObject hoverEvent) {
    String action = hoverEvent.getAsJsonPrimitive("action").getAsString();
    if (action.equals("show_text")) {
      JsonElement value = hoverEvent.get("value");
      processText((value != null) ? value : hoverEvent.get("contents"));
    } else if (action.equals("show_entity")) {
      JsonObject contents = hoverEvent.getAsJsonObject("contents");
      if (contents != null)
        processText(contents.get("name")); 
    } 
  }
  
  private void processAsArray(JsonElement element) {
    for (JsonElement jsonElement : element.getAsJsonArray())
      processText(jsonElement); 
  }
  
  public <T extends Protocol> T getProtocol() {
    return (T)this.protocol;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\rewriters\ComponentRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */