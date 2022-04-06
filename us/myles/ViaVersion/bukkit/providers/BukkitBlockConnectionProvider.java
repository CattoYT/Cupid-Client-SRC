package us.myles.ViaVersion.bukkit.providers;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.providers.BlockConnectionProvider;

public class BukkitBlockConnectionProvider extends BlockConnectionProvider {
  private Chunk lastChunk;
  
  public int getWorldBlockData(UserConnection user, int bx, int by, int bz) {
    UUID uuid = user.getProtocolInfo().getUuid();
    Player player = Bukkit.getPlayer(uuid);
    if (player != null) {
      World world = player.getWorld();
      int x = bx >> 4;
      int z = bz >> 4;
      if (world.isChunkLoaded(x, z)) {
        Chunk c = getChunk(world, x, z);
        Block b = c.getBlock(bx, by, bz);
        return b.getTypeId() << 4 | b.getData();
      } 
    } 
    return 0;
  }
  
  public Chunk getChunk(World world, int x, int z) {
    if (this.lastChunk != null && this.lastChunk.getX() == x && this.lastChunk.getZ() == z)
      return this.lastChunk; 
    return this.lastChunk = world.getChunkAt(x, z);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\providers\BukkitBlockConnectionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */