package us.myles.ViaVersion.sponge.platform;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;
import us.myles.ViaVersion.api.Pair;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.platform.ViaInjector;
import us.myles.ViaVersion.sponge.handlers.SpongeChannelInitializer;
import us.myles.ViaVersion.util.ListWrapper;
import us.myles.ViaVersion.util.ReflectionUtil;
import us.myles.viaversion.libs.gson.JsonArray;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;

public class SpongeViaInjector implements ViaInjector {
  private List<ChannelFuture> injectedFutures = new ArrayList<>();
  
  private List<Pair<Field, Object>> injectedLists = new ArrayList<>();
  
  public void inject() throws Exception {
    try {
      Object connection = getServerConnection();
      if (connection == null)
        throw new Exception("We failed to find the core component 'ServerConnection', please file an issue on our GitHub."); 
      for (Field field : connection.getClass().getDeclaredFields()) {
        field.setAccessible(true);
        Object value = field.get(connection);
        if (value instanceof List) {
          ListWrapper listWrapper = new ListWrapper((List)value) {
              public void handleAdd(Object o) {
                if (o instanceof ChannelFuture)
                  try {
                    SpongeViaInjector.this.injectChannelFuture((ChannelFuture)o);
                  } catch (Exception e) {
                    e.printStackTrace();
                  }  
              }
            };
          this.injectedLists.add(new Pair(field, connection));
          field.set(connection, listWrapper);
          synchronized (listWrapper) {
            for (Object o : value) {
              if (o instanceof ChannelFuture)
                injectChannelFuture((ChannelFuture)o); 
            } 
          } 
        } 
      } 
    } catch (Exception e) {
      Via.getPlatform().getLogger().severe("Unable to inject ViaVersion, please post these details on our GitHub and ensure you're using a compatible server version.");
      throw e;
    } 
  }
  
  private void injectChannelFuture(ChannelFuture future) throws Exception {
    try {
      List<String> names = future.channel().pipeline().names();
      ChannelHandler bootstrapAcceptor = null;
      for (String name : names) {
        ChannelHandler handler = future.channel().pipeline().get(name);
        try {
          ReflectionUtil.get(handler, "childHandler", ChannelInitializer.class);
          bootstrapAcceptor = handler;
        } catch (Exception exception) {}
      } 
      if (bootstrapAcceptor == null)
        bootstrapAcceptor = future.channel().pipeline().first(); 
      try {
        ChannelInitializer<Channel> oldInit = (ChannelInitializer<Channel>)ReflectionUtil.get(bootstrapAcceptor, "childHandler", ChannelInitializer.class);
        SpongeChannelInitializer spongeChannelInitializer = new SpongeChannelInitializer(oldInit);
        ReflectionUtil.set(bootstrapAcceptor, "childHandler", spongeChannelInitializer);
        this.injectedFutures.add(future);
      } catch (NoSuchFieldException e) {
        throw new Exception("Unable to find core component 'childHandler', please check your plugins. issue: " + bootstrapAcceptor.getClass().getName());
      } 
    } catch (Exception e) {
      Via.getPlatform().getLogger().severe("We failed to inject ViaVersion, have you got late-bind enabled with something else?");
      throw e;
    } 
  }
  
  public void uninject() {
    for (ChannelFuture future : this.injectedFutures) {
      List<String> names = future.channel().pipeline().names();
      ChannelHandler bootstrapAcceptor = null;
      for (String name : names) {
        ChannelHandler handler = future.channel().pipeline().get(name);
        try {
          ChannelInitializer<Channel> oldInit = (ChannelInitializer<Channel>)ReflectionUtil.get(handler, "childHandler", ChannelInitializer.class);
          if (oldInit instanceof SpongeChannelInitializer)
            bootstrapAcceptor = handler; 
        } catch (Exception exception) {}
      } 
      if (bootstrapAcceptor == null)
        bootstrapAcceptor = future.channel().pipeline().first(); 
      try {
        ChannelInitializer<Channel> oldInit = (ChannelInitializer<Channel>)ReflectionUtil.get(bootstrapAcceptor, "childHandler", ChannelInitializer.class);
        if (oldInit instanceof SpongeChannelInitializer)
          ReflectionUtil.set(bootstrapAcceptor, "childHandler", ((SpongeChannelInitializer)oldInit).getOriginal()); 
      } catch (Exception e) {
        Via.getPlatform().getLogger().severe("Failed to remove injection handler, reload won't work with connections, please reboot!");
      } 
    } 
    this.injectedFutures.clear();
    for (Pair<Field, Object> pair : this.injectedLists) {
      try {
        Object o = ((Field)pair.getKey()).get(pair.getValue());
        if (o instanceof ListWrapper)
          ((Field)pair.getKey()).set(pair.getValue(), ((ListWrapper)o).getOriginalList()); 
      } catch (IllegalAccessException e) {
        Via.getPlatform().getLogger().severe("Failed to remove injection, reload won't work with connections, please reboot!");
      } 
    } 
    this.injectedLists.clear();
  }
  
  public static Object getServer() throws Exception {
    return Sponge.getServer();
  }
  
  public int getServerProtocolVersion() throws Exception {
    MinecraftVersion mcv = Sponge.getPlatform().getMinecraftVersion();
    try {
      return ((Integer)mcv.getClass().getDeclaredMethod("getProtocol", new Class[0]).invoke(mcv, new Object[0])).intValue();
    } catch (Exception e) {
      throw new Exception("Failed to get server protocol", e);
    } 
  }
  
  public String getEncoderName() {
    return "encoder";
  }
  
  public String getDecoderName() {
    return "decoder";
  }
  
  public static Object getServerConnection() throws Exception {
    Class<?> serverClazz = Class.forName("net.minecraft.server.MinecraftServer");
    Object server = getServer();
    Object connection = null;
    for (Method m : serverClazz.getDeclaredMethods()) {
      if (m.getReturnType() != null && 
        m.getReturnType().getSimpleName().equals("NetworkSystem") && (
        m.getParameterTypes()).length == 0)
        connection = m.invoke(server, new Object[0]); 
    } 
    return connection;
  }
  
  public JsonObject getDump() {
    JsonObject data = new JsonObject();
    JsonArray injectedChannelInitializers = new JsonArray();
    for (ChannelFuture cf : this.injectedFutures) {
      JsonObject info = new JsonObject();
      info.addProperty("futureClass", cf.getClass().getName());
      info.addProperty("channelClass", cf.channel().getClass().getName());
      JsonArray pipeline = new JsonArray();
      for (String pipeName : cf.channel().pipeline().names()) {
        JsonObject pipe = new JsonObject();
        pipe.addProperty("name", pipeName);
        if (cf.channel().pipeline().get(pipeName) != null) {
          pipe.addProperty("class", cf.channel().pipeline().get(pipeName).getClass().getName());
          try {
            Object child = ReflectionUtil.get(cf.channel().pipeline().get(pipeName), "childHandler", ChannelInitializer.class);
            pipe.addProperty("childClass", child.getClass().getName());
            if (child instanceof SpongeChannelInitializer)
              pipe.addProperty("oldInit", ((SpongeChannelInitializer)child).getOriginal().getClass().getName()); 
          } catch (Exception exception) {}
        } 
        pipeline.add((JsonElement)pipe);
      } 
      info.add("pipeline", (JsonElement)pipeline);
      injectedChannelInitializers.add((JsonElement)info);
    } 
    data.add("injectedChannelInitializers", (JsonElement)injectedChannelInitializers);
    JsonObject wrappedLists = new JsonObject();
    JsonObject currentLists = new JsonObject();
    try {
      for (Pair<Field, Object> pair : this.injectedLists) {
        Object list = ((Field)pair.getKey()).get(pair.getValue());
        currentLists.addProperty(((Field)pair.getKey()).getName(), list.getClass().getName());
        if (list instanceof ListWrapper)
          wrappedLists.addProperty(((Field)pair.getKey()).getName(), ((ListWrapper)list).getOriginalList().getClass().getName()); 
      } 
      data.add("wrappedLists", (JsonElement)wrappedLists);
      data.add("currentLists", (JsonElement)currentLists);
    } catch (Exception exception) {}
    return data;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\platform\SpongeViaInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */