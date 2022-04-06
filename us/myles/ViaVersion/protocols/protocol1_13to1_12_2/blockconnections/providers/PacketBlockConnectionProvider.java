package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.providers;

import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage.BlockConnectionStorage;

public class PacketBlockConnectionProvider extends BlockConnectionProvider {
  public void storeBlock(UserConnection connection, int x, int y, int z, int blockState) {
    ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).store(x, y, z, blockState);
  }
  
  public void removeBlock(UserConnection connection, int x, int y, int z) {
    ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).remove(x, y, z);
  }
  
  public int getBlockData(UserConnection connection, int x, int y, int z) {
    return ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).get(x, y, z);
  }
  
  public void clearStorage(UserConnection connection) {
    ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).clear();
  }
  
  public void unloadChunk(UserConnection connection, int x, int z) {
    ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).unloadChunk(x, z);
  }
  
  public boolean storesBlocks() {
    return true;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\blockconnections\providers\PacketBlockConnectionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */