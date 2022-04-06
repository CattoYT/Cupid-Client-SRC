package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.blockentities;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ChatRewriter;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class CommandBlockHandler implements BlockEntityProvider.BlockEntityHandler {
  public int transform(UserConnection user, CompoundTag tag) {
    Tag name = tag.get("CustomName");
    if (name instanceof StringTag)
      ((StringTag)name).setValue(ChatRewriter.legacyTextToJsonString(((StringTag)name).getValue())); 
    Tag out = tag.get("LastOutput");
    if (out instanceof StringTag) {
      JsonElement value = GsonUtil.getJsonParser().parse(((StringTag)out).getValue());
      ChatRewriter.processTranslate(value);
      ((StringTag)out).setValue(value.toString());
    } 
    return -1;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\providers\blockentities\CommandBlockHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */