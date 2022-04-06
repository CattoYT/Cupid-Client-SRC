package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.blockentities;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage.BlockStorage;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class BedHandler implements BlockEntityProvider.BlockEntityHandler {
  public int transform(UserConnection user, CompoundTag tag) {
    BlockStorage storage = (BlockStorage)user.get(BlockStorage.class);
    Position position = new Position((int)getLong(tag.get("x")), (short)(int)getLong(tag.get("y")), (int)getLong(tag.get("z")));
    if (!storage.contains(position)) {
      Via.getPlatform().getLogger().warning("Received an bed color update packet, but there is no bed! O_o " + tag);
      return -1;
    } 
    int blockId = storage.get(position).getOriginal() - 972 + 748;
    Tag color = tag.get("color");
    if (color != null)
      blockId += ((Number)color.getValue()).intValue() * 16; 
    return blockId;
  }
  
  private long getLong(Tag tag) {
    return ((Integer)tag.getValue()).longValue();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\providers\blockentities\BedHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */