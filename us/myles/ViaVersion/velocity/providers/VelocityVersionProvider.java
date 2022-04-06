package us.myles.ViaVersion.velocity.providers;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.ServerConnection;
import io.netty.channel.ChannelHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.IntStream;
import us.myles.ViaVersion.VelocityPlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.protocols.base.VersionProvider;
import us.myles.ViaVersion.velocity.platform.VelocityViaInjector;
import us.myles.ViaVersion.velocity.service.ProtocolDetectorService;

public class VelocityVersionProvider extends VersionProvider {
  private static Method getAssociation;
  
  static {
    try {
      getAssociation = Class.forName("com.velocitypowered.proxy.connection.MinecraftConnection").getMethod("getAssociation", new Class[0]);
    } catch (NoSuchMethodException|ClassNotFoundException e) {
      e.printStackTrace();
    } 
  }
  
  public int getServerProtocol(UserConnection user) throws Exception {
    return user.isClientSide() ? getBackProtocol(user) : getFrontProtocol(user);
  }
  
  private int getBackProtocol(UserConnection user) throws Exception {
    ChannelHandler mcHandler = user.getChannel().pipeline().get("handler");
    return ProtocolDetectorService.getProtocolId(((ServerConnection)getAssociation
        .invoke(mcHandler, new Object[0])).getServerInfo().getName()).intValue();
  }
  
  private int getFrontProtocol(UserConnection user) throws Exception {
    int playerVersion = user.getProtocolInfo().getProtocolVersion();
    IntStream versions = ProtocolVersion.SUPPORTED_VERSIONS.stream().mapToInt(ProtocolVersion::getProtocol);
    if (VelocityViaInjector.getPlayerInfoForwardingMode != null && ((Enum)VelocityViaInjector.getPlayerInfoForwardingMode
      .invoke(VelocityPlugin.PROXY.getConfiguration(), new Object[0]))
      .name().equals("MODERN"))
      versions = versions.filter(ver -> (ver >= ProtocolVersion.v1_13.getVersion())); 
    int[] compatibleProtocols = versions.toArray();
    if (Arrays.binarySearch(compatibleProtocols, playerVersion) >= 0)
      return playerVersion; 
    if (playerVersion < compatibleProtocols[0])
      return compatibleProtocols[0]; 
    for (int i = compatibleProtocols.length - 1; i >= 0; i--) {
      int protocol = compatibleProtocols[i];
      if (playerVersion > protocol && ProtocolVersion.isRegistered(protocol))
        return protocol; 
    } 
    Via.getPlatform().getLogger().severe("Panic, no protocol id found for " + playerVersion);
    return playerVersion;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\providers\VelocityVersionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */