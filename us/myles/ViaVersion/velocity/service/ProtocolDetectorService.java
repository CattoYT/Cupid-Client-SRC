package us.myles.ViaVersion.velocity.service;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import us.myles.ViaVersion.VelocityPlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.velocity.platform.VelocityViaConfig;

public class ProtocolDetectorService implements Runnable {
  private static final Map<String, Integer> detectedProtocolIds = new ConcurrentHashMap<>();
  
  private static ProtocolDetectorService instance;
  
  public ProtocolDetectorService() {
    instance = this;
  }
  
  public static Integer getProtocolId(String serverName) {
    Map<String, Integer> servers = ((VelocityViaConfig)Via.getConfig()).getVelocityServerProtocols();
    Integer protocol = servers.get(serverName);
    if (protocol != null)
      return protocol; 
    Integer detectedProtocol = detectedProtocolIds.get(serverName);
    if (detectedProtocol != null)
      return detectedProtocol; 
    Integer defaultProtocol = servers.get("default");
    if (defaultProtocol != null)
      return defaultProtocol; 
    try {
      return Integer.valueOf(ProtocolVersion.getProtocol(Via.getManager().getInjector().getServerProtocolVersion()).getVersion());
    } catch (Exception e) {
      e.printStackTrace();
      return Integer.valueOf(ProtocolVersion.v1_8.getVersion());
    } 
  }
  
  public void run() {
    for (RegisteredServer serv : VelocityPlugin.PROXY.getAllServers())
      probeServer(serv); 
  }
  
  public static void probeServer(RegisteredServer serverInfo) {
    String key = serverInfo.getServerInfo().getName();
    serverInfo.ping().thenAccept(serverPing -> {
          if (serverPing != null && serverPing.getVersion() != null) {
            detectedProtocolIds.put(key, Integer.valueOf(serverPing.getVersion().getProtocol()));
            if (((VelocityViaConfig)Via.getConfig()).isVelocityPingSave()) {
              Map<String, Integer> servers = ((VelocityViaConfig)Via.getConfig()).getVelocityServerProtocols();
              Integer protocol = servers.get(key);
              if (protocol != null && protocol.intValue() == serverPing.getVersion().getProtocol())
                return; 
              synchronized (Via.getPlatform().getConfigurationProvider()) {
                servers.put(key, Integer.valueOf(serverPing.getVersion().getProtocol()));
              } 
              Via.getPlatform().getConfigurationProvider().saveConfig();
            } 
          } 
        });
  }
  
  public static Map<String, Integer> getDetectedIds() {
    return new HashMap<>(detectedProtocolIds);
  }
  
  public static ProtocolDetectorService getInstance() {
    return instance;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\service\ProtocolDetectorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */