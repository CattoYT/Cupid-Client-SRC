package us.myles.ViaVersion.bukkit.platform;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.bukkit.plugin.PluginDescriptionFile;
import us.myles.ViaVersion.api.Pair;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.platform.ViaInjector;
import us.myles.ViaVersion.bukkit.handlers.BukkitChannelInitializer;
import us.myles.ViaVersion.bukkit.util.NMSUtil;
import us.myles.ViaVersion.util.ConcurrentList;
import us.myles.ViaVersion.util.ListWrapper;
import us.myles.ViaVersion.util.ReflectionUtil;
import us.myles.viaversion.libs.gson.JsonArray;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;

public class BukkitViaInjector implements ViaInjector {
  private final List<ChannelFuture> injectedFutures = new ArrayList<>();
  
  private final List<Pair<Field, Object>> injectedLists = new ArrayList<>();
  
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
                    BukkitViaInjector.this.injectChannelFuture((ChannelFuture)o);
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
        ChannelInitializer<SocketChannel> oldInit = (ChannelInitializer<SocketChannel>)ReflectionUtil.get(bootstrapAcceptor, "childHandler", ChannelInitializer.class);
        BukkitChannelInitializer bukkitChannelInitializer = new BukkitChannelInitializer(oldInit);
        ReflectionUtil.set(bootstrapAcceptor, "childHandler", bukkitChannelInitializer);
        this.injectedFutures.add(future);
      } catch (NoSuchFieldException e) {
        ClassLoader cl = bootstrapAcceptor.getClass().getClassLoader();
        if (cl.getClass().getName().equals("org.bukkit.plugin.java.PluginClassLoader")) {
          PluginDescriptionFile yaml = (PluginDescriptionFile)ReflectionUtil.get(cl, "description", PluginDescriptionFile.class);
          throw new Exception("Unable to inject, due to " + bootstrapAcceptor.getClass().getName() + ", try without the plugin " + yaml.getName() + "?");
        } 
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
          ChannelInitializer<SocketChannel> oldInit = (ChannelInitializer<SocketChannel>)ReflectionUtil.get(handler, "childHandler", ChannelInitializer.class);
          if (oldInit instanceof BukkitChannelInitializer)
            bootstrapAcceptor = handler; 
        } catch (Exception exception) {}
      } 
      if (bootstrapAcceptor == null)
        bootstrapAcceptor = future.channel().pipeline().first(); 
      try {
        ChannelInitializer<SocketChannel> oldInit = (ChannelInitializer<SocketChannel>)ReflectionUtil.get(bootstrapAcceptor, "childHandler", ChannelInitializer.class);
        if (oldInit instanceof BukkitChannelInitializer)
          ReflectionUtil.set(bootstrapAcceptor, "childHandler", ((BukkitChannelInitializer)oldInit).getOriginal()); 
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
  
  public int getServerProtocolVersion() throws Exception {
    try {
      Class<?> serverClazz = NMSUtil.nms("MinecraftServer");
      Object server = ReflectionUtil.invokeStatic(serverClazz, "getServer");
      Class<?> pingClazz = NMSUtil.nms("ServerPing");
      Object ping = null;
      for (Field f : serverClazz.getDeclaredFields()) {
        if (f.getType() != null && 
          f.getType().getSimpleName().equals("ServerPing")) {
          f.setAccessible(true);
          ping = f.get(server);
        } 
      } 
      if (ping != null) {
        Object serverData = null;
        for (Field f : pingClazz.getDeclaredFields()) {
          if (f.getType() != null && 
            f.getType().getSimpleName().endsWith("ServerData")) {
            f.setAccessible(true);
            serverData = f.get(ping);
          } 
        } 
        if (serverData != null) {
          int protocolVersion = -1;
          for (Field f : serverData.getClass().getDeclaredFields()) {
            if (f.getType() != null && 
              f.getType() == int.class) {
              f.setAccessible(true);
              protocolVersion = ((Integer)f.get(serverData)).intValue();
            } 
          } 
          if (protocolVersion != -1)
            return protocolVersion; 
        } 
      } 
    } catch (Exception e) {
      throw new Exception("Failed to get server", e);
    } 
    throw new Exception("Failed to get server");
  }
  
  public String getEncoderName() {
    return "encoder";
  }
  
  public String getDecoderName() {
    return "decoder";
  }
  
  public static Object getServerConnection() throws Exception {
    Class<?> serverClazz = NMSUtil.nms("MinecraftServer");
    Object server = ReflectionUtil.invokeStatic(serverClazz, "getServer");
    Object connection = null;
    for (Method m : serverClazz.getDeclaredMethods()) {
      if (m.getReturnType() != null && 
        m.getReturnType().getSimpleName().equals("ServerConnection") && (
        m.getParameterTypes()).length == 0)
        connection = m.invoke(server, new Object[0]); 
    } 
    return connection;
  }
  
  public static boolean isBinded() {
    try {
      Object connection = getServerConnection();
      if (connection == null)
        return false; 
      for (Field field : connection.getClass().getDeclaredFields()) {
        field.setAccessible(true);
        Object value = field.get(connection);
        if (value instanceof List)
          synchronized (value) {
            Iterator iterator = ((List)value).iterator();
            if (iterator.hasNext()) {
              Object o = iterator.next();
              if (o instanceof ChannelFuture)
                return true; 
            } 
          }  
      } 
    } catch (Exception exception) {}
    return false;
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
            if (child instanceof BukkitChannelInitializer)
              pipe.addProperty("oldInit", ((BukkitChannelInitializer)child).getOriginal().getClass().getName()); 
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
    data.addProperty("binded", Boolean.valueOf(isBinded()));
    return data;
  }
  
  public static void patchLists() throws Exception {
    Object connection = getServerConnection();
    if (connection == null) {
      Via.getPlatform().getLogger().warning("We failed to find the core component 'ServerConnection', please file an issue on our GitHub.");
      return;
    } 
    for (Field field : connection.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      Object value = field.get(connection);
      if (value instanceof List && 
        !(value instanceof ConcurrentList)) {
        ConcurrentList list = new ConcurrentList();
        list.addAll((Collection)value);
        field.set(connection, list);
      } 
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\platform\BukkitViaInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */