package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.blockentities;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage.BlockStorage;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class SkullHandler implements BlockEntityProvider.BlockEntityHandler {
  private static final int SKULL_WALL_START = 5447;
  
  private static final int SKULL_END = 5566;
  
  public int transform(UserConnection user, CompoundTag tag) {
    BlockStorage storage = (BlockStorage)user.get(BlockStorage.class);
    Position position = new Position((int)getLong(tag.get("x")), (short)(int)getLong(tag.get("y")), (int)getLong(tag.get("z")));
    if (!storage.contains(position)) {
      Via.getPlatform().getLogger().warning("Received an head update packet, but there is no head! O_o " + tag);
      return -1;
    } 
    int id = storage.get(position).getOriginal();
    if (id >= 5447 && id <= 5566) {
      Tag skullType = tag.get("SkullType");
      if (skullType != null)
        id += ((Number)tag.get("SkullType").getValue()).intValue() * 20; 
      if (tag.contains("Rot"))
        id += ((Number)tag.get("Rot").getValue()).intValue(); 
    } else {
      Via.getPlatform().getLogger().warning("Why does this block have the skull block entity? " + tag);
      return -1;
    } 
    return id;
  }
  
  private long getLong(Tag tag) {
    return ((Integer)tag.getValue()).longValue();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\providers\blockentities\SkullHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */