package us.myles.ViaVersion.velocity.platform;

import com.velocitypowered.api.network.ProtocolVersion;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Method;
import us.myles.ViaVersion.VelocityPlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.platform.ViaInjector;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.util.ReflectionUtil;
import us.myles.ViaVersion.velocity.handlers.VelocityChannelInitializer;
import us.myles.viaversion.libs.gson.JsonObject;

public class VelocityViaInjector implements ViaInjector {
  public static Method getPlayerInfoForwardingMode;
  
  static {
    try {
      getPlayerInfoForwardingMode = Class.forName("com.velocitypowered.proxy.config.VelocityConfiguration").getMethod("getPlayerInfoForwardingMode", new Class[0]);
    } catch (NoSuchMethodException|ClassNotFoundException e) {
      e.printStackTrace();
    } 
  }
  
  private ChannelInitializer getInitializer() throws Exception {
    Object connectionManager = ReflectionUtil.get(VelocityPlugin.PROXY, "cm", Object.class);
    Object channelInitializerHolder = ReflectionUtil.invoke(connectionManager, "getServerChannelInitializer");
    return (ChannelInitializer)ReflectionUtil.invoke(channelInitializerHolder, "get");
  }
  
  private ChannelInitializer getBackendInitializer() throws Exception {
    Object connectionManager = ReflectionUtil.get(VelocityPlugin.PROXY, "cm", Object.class);
    Object channelInitializerHolder = ReflectionUtil.invoke(connectionManager, "getBackendChannelInitializer");
    return (ChannelInitializer)ReflectionUtil.invoke(channelInitializerHolder, "get");
  }
  
  public void inject() throws Exception {
    Object connectionManager = ReflectionUtil.get(VelocityPlugin.PROXY, "cm", Object.class);
    Object channelInitializerHolder = ReflectionUtil.invoke(connectionManager, "getServerChannelInitializer");
    ChannelInitializer originalInitializer = getInitializer();
    channelInitializerHolder.getClass().getMethod("set", new Class[] { ChannelInitializer.class }).invoke(channelInitializerHolder, new Object[] { new VelocityChannelInitializer(originalInitializer, false) });
    Object backendInitializerHolder = ReflectionUtil.invoke(connectionManager, "getBackendChannelInitializer");
    ChannelInitializer backendInitializer = getBackendInitializer();
    backendInitializerHolder.getClass().getMethod("set", new Class[] { ChannelInitializer.class }).invoke(backendInitializerHolder, new Object[] { new VelocityChannelInitializer(backendInitializer, true) });
  }
  
  public void uninject() {
    Via.getPlatform().getLogger().severe("ViaVersion cannot remove itself from Velocity without a reboot!");
  }
  
  public int getServerProtocolVersion() throws Exception {
    return getLowestSupportedProtocolVersion();
  }
  
  public static int getLowestSupportedProtocolVersion() {
    try {
      if (getPlayerInfoForwardingMode != null && ((Enum)getPlayerInfoForwardingMode
        .invoke(VelocityPlugin.PROXY.getConfiguration(), new Object[0]))
        .name().equals("MODERN"))
        return ProtocolVersion.v1_13.getVersion(); 
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException illegalAccessException) {}
    return ProtocolVersion.MINIMUM_VERSION.getProtocol();
  }
  
  public String getEncoderName() {
    return "via-encoder";
  }
  
  public String getDecoderName() {
    return "via-decoder";
  }
  
  public JsonObject getDump() {
    JsonObject data = new JsonObject();
    try {
      data.addProperty("currentInitializer", getInitializer().getClass().getName());
    } catch (Exception exception) {}
    return data;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\platform\VelocityViaInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */