package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections.providers.BlockConnectionProvider;

public abstract class ConnectionHandler {
  public abstract int connect(UserConnection paramUserConnection, Position paramPosition, int paramInt);
  
  public int getBlockData(UserConnection user, Position position) {
    return ((BlockConnectionProvider)Via.getManager().getProviders().get(BlockConnectionProvider.class)).getBlockData(user, position.getX(), position.getY(), position.getZ());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\blockconnections\ConnectionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */