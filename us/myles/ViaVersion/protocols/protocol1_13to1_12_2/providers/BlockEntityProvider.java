package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers;

import java.util.HashMap;
import java.util.Map;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.platform.providers.Provider;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.blockentities.BannerHandler;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.blockentities.BedHandler;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.blockentities.CommandBlockHandler;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.blockentities.FlowerPotHandler;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.blockentities.SkullHandler;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers.blockentities.SpawnerHandler;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class BlockEntityProvider implements Provider {
  private final Map<String, BlockEntityHandler> handlers = new HashMap<>();
  
  public BlockEntityProvider() {
    this.handlers.put("minecraft:flower_pot", new FlowerPotHandler());
    this.handlers.put("minecraft:bed", new BedHandler());
    this.handlers.put("minecraft:banner", new BannerHandler());
    this.handlers.put("minecraft:skull", new SkullHandler());
    this.handlers.put("minecraft:mob_spawner", new SpawnerHandler());
    this.handlers.put("minecraft:command_block", new CommandBlockHandler());
  }
  
  public int transform(UserConnection user, Position position, CompoundTag tag, boolean sendUpdate) throws Exception {
    Tag idTag = tag.get("id");
    if (idTag == null)
      return -1; 
    String id = (String)idTag.getValue();
    BlockEntityHandler handler = this.handlers.get(id);
    if (handler == null) {
      if (Via.getManager().isDebug())
        Via.getPlatform().getLogger().warning("Unhandled BlockEntity " + id + " full tag: " + tag); 
      return -1;
    } 
    int newBlock = handler.transform(user, tag);
    if (sendUpdate && newBlock != -1)
      sendBlockChange(user, position, newBlock); 
    return newBlock;
  }
  
  private void sendBlockChange(UserConnection user, Position position, int blockId) throws Exception {
    PacketWrapper wrapper = new PacketWrapper(11, null, user);
    wrapper.write(Type.POSITION, position);
    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(blockId));
    wrapper.send(Protocol1_13To1_12_2.class, true, true);
  }
  
  public static interface BlockEntityHandler {
    int transform(UserConnection param1UserConnection, CompoundTag param1CompoundTag);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\providers\BlockEntityProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */