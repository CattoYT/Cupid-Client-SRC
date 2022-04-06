package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections;

import java.util.HashSet;
import java.util.Set;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.BlockFace;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.viaversion.libs.fastutil.ints.Int2IntMap;
import us.myles.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public class FlowerConnectionHandler extends ConnectionHandler {
  private static final Int2IntMap flowers = (Int2IntMap)new Int2IntOpenHashMap();
  
  static ConnectionData.ConnectorInitAction init() {
    Set<String> baseFlower = new HashSet<>();
    baseFlower.add("minecraft:rose_bush");
    baseFlower.add("minecraft:sunflower");
    baseFlower.add("minecraft:peony");
    baseFlower.add("minecraft:tall_grass");
    baseFlower.add("minecraft:large_fern");
    baseFlower.add("minecraft:lilac");
    FlowerConnectionHandler handler = new FlowerConnectionHandler();
    return blockData -> {
        if (baseFlower.contains(blockData.getMinecraftKey())) {
          ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), handler);
          if (blockData.getValue("half").equals("lower")) {
            blockData.set("half", "upper");
            flowers.put(blockData.getSavedBlockStateId(), blockData.getBlockStateId());
          } 
        } 
      };
  }
  
  public int connect(UserConnection user, Position position, int blockState) {
    int blockBelowId = getBlockData(user, position.getRelative(BlockFace.BOTTOM));
    int connectBelow = flowers.get(blockBelowId);
    if (connectBelow != 0) {
      int blockAboveId = getBlockData(user, position.getRelative(BlockFace.TOP));
      if (Via.getConfig().isStemWhenBlockAbove()) {
        if (blockAboveId == 0)
          return connectBelow; 
      } else if (!flowers.containsKey(blockAboveId)) {
        return connectBelow;
      } 
    } 
    return blockState;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\blockconnections\FlowerConnectionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */