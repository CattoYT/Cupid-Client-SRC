package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import us.myles.ViaVersion.api.Pair;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.BlockFace;
import us.myles.ViaVersion.api.minecraft.Position;

public class SnowyGrassConnectionHandler extends ConnectionHandler {
  private static final Map<Pair<Integer, Boolean>, Integer> grassBlocks = new HashMap<>();
  
  private static final Set<Integer> snows = new HashSet<>();
  
  static ConnectionData.ConnectorInitAction init() {
    Set<String> snowyGrassBlocks = new HashSet<>();
    snowyGrassBlocks.add("minecraft:grass_block");
    snowyGrassBlocks.add("minecraft:podzol");
    snowyGrassBlocks.add("minecraft:mycelium");
    SnowyGrassConnectionHandler handler = new SnowyGrassConnectionHandler();
    return blockData -> {
        if (snowyGrassBlocks.contains(blockData.getMinecraftKey())) {
          ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), handler);
          blockData.set("snowy", "true");
          grassBlocks.put(new Pair(Integer.valueOf(blockData.getSavedBlockStateId()), Boolean.valueOf(true)), Integer.valueOf(blockData.getBlockStateId()));
          blockData.set("snowy", "false");
          grassBlocks.put(new Pair(Integer.valueOf(blockData.getSavedBlockStateId()), Boolean.valueOf(false)), Integer.valueOf(blockData.getBlockStateId()));
        } 
        if (blockData.getMinecraftKey().equals("minecraft:snow") || blockData.getMinecraftKey().equals("minecraft:snow_block")) {
          ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), handler);
          snows.add(Integer.valueOf(blockData.getSavedBlockStateId()));
        } 
      };
  }
  
  public int connect(UserConnection user, Position position, int blockState) {
    int blockUpId = getBlockData(user, position.getRelative(BlockFace.TOP));
    Integer newId = grassBlocks.get(new Pair(Integer.valueOf(blockState), Boolean.valueOf(snows.contains(Integer.valueOf(blockUpId)))));
    if (newId != null)
      return newId.intValue(); 
    return blockState;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\blockconnections\SnowyGrassConnectionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */