package us.myles.ViaVersion.bukkit.classgenerator;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import us.myles.ViaVersion.ViaVersionPlugin;
import us.myles.ViaVersion.bukkit.handlers.BukkitDecodeHandler;
import us.myles.ViaVersion.bukkit.handlers.BukkitEncodeHandler;
import us.myles.ViaVersion.bukkit.util.NMSUtil;
import us.myles.viaversion.libs.javassist.CannotCompileException;
import us.myles.viaversion.libs.javassist.ClassPath;
import us.myles.viaversion.libs.javassist.ClassPool;
import us.myles.viaversion.libs.javassist.CtClass;
import us.myles.viaversion.libs.javassist.CtField;
import us.myles.viaversion.libs.javassist.CtMethod;
import us.myles.viaversion.libs.javassist.CtNewConstructor;
import us.myles.viaversion.libs.javassist.CtNewMethod;
import us.myles.viaversion.libs.javassist.LoaderClassPath;
import us.myles.viaversion.libs.javassist.NotFoundException;
import us.myles.viaversion.libs.javassist.expr.ConstructorCall;
import us.myles.viaversion.libs.javassist.expr.ExprEditor;

public class ClassGenerator {
  private static HandlerConstructor constructor = new BasicHandlerConstructor();
  
  private static String psPackage;
  
  private static Class psConnectListener;
  
  public static HandlerConstructor getConstructor() {
    return constructor;
  }
  
  public static void generate() {
    if (ViaVersionPlugin.getInstance().isCompatSpigotBuild() || ViaVersionPlugin.getInstance().isProtocolSupport())
      try {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath((ClassPath)new LoaderClassPath(Bukkit.class.getClassLoader()));
        for (Plugin p : Bukkit.getPluginManager().getPlugins())
          pool.insertClassPath((ClassPath)new LoaderClassPath(p.getClass().getClassLoader())); 
        if (ViaVersionPlugin.getInstance().isCompatSpigotBuild()) {
          Class decodeSuper = NMSUtil.nms("PacketDecoder");
          Class encodeSuper = NMSUtil.nms("PacketEncoder");
          addSpigotCompatibility(pool, BukkitDecodeHandler.class, decodeSuper);
          addSpigotCompatibility(pool, BukkitEncodeHandler.class, encodeSuper);
        } else {
          if (isMultiplatformPS()) {
            psConnectListener = makePSConnectListener(pool, shouldUseNewHandshakeVersionMethod());
            return;
          } 
          String psPackage = getOldPSPackage();
          Class<?> decodeSuper = Class.forName(psPackage.equals("unknown") ? "protocolsupport.protocol.pipeline.common.PacketDecoder" : (psPackage + ".wrapped.WrappedDecoder"));
          Class<?> encodeSuper = Class.forName(psPackage.equals("unknown") ? "protocolsupport.protocol.pipeline.common.PacketEncoder" : (psPackage + ".wrapped.WrappedEncoder"));
          addPSCompatibility(pool, BukkitDecodeHandler.class, decodeSuper);
          addPSCompatibility(pool, BukkitEncodeHandler.class, encodeSuper);
        } 
        CtClass generated = pool.makeClass("us.myles.ViaVersion.classgenerator.generated.GeneratedConstructor");
        CtClass handlerInterface = pool.get(HandlerConstructor.class.getName());
        generated.setInterfaces(new CtClass[] { handlerInterface });
        pool.importPackage("us.myles.ViaVersion.classgenerator.generated");
        pool.importPackage("us.myles.ViaVersion.classgenerator");
        pool.importPackage("us.myles.ViaVersion.api.data");
        pool.importPackage("io.netty.handler.codec");
        generated.addMethod(CtMethod.make("public MessageToByteEncoder newEncodeHandler(UserConnection info, MessageToByteEncoder minecraftEncoder) {\n        return new BukkitEncodeHandler(info, minecraftEncoder);\n    }", generated));
        generated.addMethod(CtMethod.make("public ByteToMessageDecoder newDecodeHandler(UserConnection info, ByteToMessageDecoder minecraftDecoder) {\n        return new BukkitDecodeHandler(info, minecraftDecoder);\n    }", generated));
        constructor = generated.toClass(HandlerConstructor.class.getClassLoader()).newInstance();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (CannotCompileException e) {
        e.printStackTrace();
      } catch (NotFoundException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }  
  }
  
  private static Class addSpigotCompatibility(ClassPool pool, Class input, Class superclass) {
    String newName = "us.myles.ViaVersion.classgenerator.generated." + input.getSimpleName();
    try {
      CtClass generated = pool.getAndRename(input.getName(), newName);
      if (superclass != null) {
        CtClass toExtend = pool.get(superclass.getName());
        generated.setSuperclass(toExtend);
        if (superclass.getName().startsWith("net.minecraft"))
          if ((generated.getConstructors()).length != 0)
            generated.getConstructors()[0].instrument(new ExprEditor() {
                  public void edit(ConstructorCall c) throws CannotCompileException {
                    if (c.isSuper())
                      c.replace("super(null);"); 
                    super.edit(c);
                  }
                });  
      } 
      return generated.toClass(HandlerConstructor.class.getClassLoader());
    } catch (NotFoundException e) {
      e.printStackTrace();
    } catch (CannotCompileException e) {
      e.printStackTrace();
    } 
    return null;
  }
  
  private static Class addPSCompatibility(ClassPool pool, Class input, Class superclass) {
    boolean newPS = getOldPSPackage().equals("unknown");
    String newName = "us.myles.ViaVersion.classgenerator.generated." + input.getSimpleName();
    try {
      CtClass generated = pool.getAndRename(input.getName(), newName);
      if (superclass != null) {
        CtClass toExtend = pool.get(superclass.getName());
        generated.setSuperclass(toExtend);
        if (!newPS) {
          pool.importPackage(getOldPSPackage());
          pool.importPackage(getOldPSPackage() + ".wrapped");
          if (superclass.getName().endsWith("Decoder")) {
            generated.addMethod(CtMethod.make("public void setRealDecoder(IPacketDecoder dec) {\n        ((WrappedDecoder) this.minecraftDecoder).setRealDecoder(dec);\n    }", generated));
          } else {
            pool.importPackage("protocolsupport.api");
            pool.importPackage("java.lang.reflect");
            generated.addMethod(CtMethod.make("public void setRealEncoder(IPacketEncoder enc) {\n         try {\n             Field field = enc.getClass().getDeclaredField(\"version\");\n             field.setAccessible(true);\n             ProtocolVersion version = (ProtocolVersion) field.get(enc);\n             if (version == ProtocolVersion.MINECRAFT_FUTURE) enc = enc.getClass().getConstructor(\n                 new Class[]{ProtocolVersion.class}).newInstance(new Object[] {ProtocolVersion.getLatest()});\n         } catch (Exception e) {\n         }\n        ((WrappedEncoder) this.minecraftEncoder).setRealEncoder(enc);\n    }", generated));
          } 
        } 
      } 
      return generated.toClass(HandlerConstructor.class.getClassLoader());
    } catch (NotFoundException e) {
      e.printStackTrace();
    } catch (CannotCompileException e) {
      e.printStackTrace();
    } 
    return null;
  }
  
  private static Class makePSConnectListener(ClassPool pool, boolean newVersionMethod) {
    try {
      CtClass toExtend = pool.get("protocolsupport.api.Connection$PacketListener");
      CtClass connectListenerClazz = pool.makeClass("us.myles.ViaVersion.classgenerator.generated.ProtocolSupportConnectListener");
      connectListenerClazz.setSuperclass(toExtend);
      pool.importPackage("java.util.Arrays");
      pool.importPackage("us.myles.ViaVersion.api.protocol.ProtocolRegistry");
      pool.importPackage("protocolsupport.api.ProtocolVersion");
      pool.importPackage("protocolsupport.api.ProtocolType");
      pool.importPackage("protocolsupport.api.Connection");
      pool.importPackage("protocolsupport.api.Connection.PacketListener");
      pool.importPackage("protocolsupport.api.Connection.PacketListener.PacketEvent");
      pool.importPackage("protocolsupport.protocol.ConnectionImpl");
      pool.importPackage(NMSUtil.nms("PacketHandshakingInSetProtocol").getName());
      connectListenerClazz.addField(CtField.make("private ConnectionImpl connection;", connectListenerClazz));
      connectListenerClazz.addConstructor(CtNewConstructor.make("public ProtocolSupportConnectListener (ConnectionImpl connection) {\n    this.connection = connection;\n}", connectListenerClazz));
      connectListenerClazz.addMethod(CtNewMethod.make("public void onPacketReceiving(protocolsupport.api.Connection.PacketListener.PacketEvent event) {\n    if (event.getPacket() instanceof PacketHandshakingInSetProtocol) {\n        PacketHandshakingInSetProtocol packet = (PacketHandshakingInSetProtocol) event.getPacket();\n" + (newVersionMethod ? "        int protoVersion = packet.getProtocolVersion();\n" : "        int protoVersion = packet.b();\n") + "        if (connection.getVersion() == ProtocolVersion.MINECRAFT_FUTURE && protoVersion == us.myles.ViaVersion.api.protocol.ProtocolRegistry.SERVER_PROTOCOL) {\n            connection.setVersion(ProtocolVersion.getLatest(ProtocolType.PC));\n        }\n    }\n    connection.removePacketListener(this);\n}", connectListenerClazz));
      return connectListenerClazz.toClass(HandlerConstructor.class.getClassLoader());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  public static void registerPSConnectListener(ViaVersionPlugin plugin) {
    if (getPSConnectListener() != null)
      try {
        Class<? extends Event> connectionOpenEvent = (Class)Class.forName("protocolsupport.api.events.ConnectionOpenEvent");
        Bukkit.getPluginManager().registerEvent(connectionOpenEvent, new Listener() {
            
            },  EventPriority.HIGH, new EventExecutor() {
              public void execute(Listener listener, Event event) throws EventException {
                try {
                  Object connection = event.getClass().getMethod("getConnection", new Class[0]).invoke(event, new Object[0]);
                  Object connectListener = ClassGenerator.getPSConnectListener().getConstructor(new Class[] { connection.getClass() }).newInstance(new Object[] { connection });
                  Method addConnectListener = connection.getClass().getMethod("addPacketListener", new Class[] { Class.forName("protocolsupport.api.Connection$PacketListener") });
                  addConnectListener.invoke(connection, new Object[] { connectListener });
                } catch (Exception e) {
                  e.printStackTrace();
                } 
              }
            }(Plugin)plugin);
      } catch (Exception e) {
        e.printStackTrace();
      }  
  }
  
  public static Class getPSConnectListener() {
    return psConnectListener;
  }
  
  public static String getOldPSPackage() {
    if (psPackage == null)
      try {
        Class.forName("protocolsupport.protocol.core.IPacketDecoder");
        psPackage = "protocolsupport.protocol.core";
      } catch (ClassNotFoundException e) {
        try {
          Class.forName("protocolsupport.protocol.pipeline.IPacketDecoder");
          psPackage = "protocolsupport.protocol.pipeline";
        } catch (ClassNotFoundException e1) {
          psPackage = "unknown";
        } 
      }  
    return psPackage;
  }
  
  public static boolean isMultiplatformPS() {
    try {
      Class.forName("protocolsupport.zplatform.impl.spigot.network.pipeline.SpigotPacketEncoder");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    } 
  }
  
  public static boolean shouldUseNewHandshakeVersionMethod() {
    try {
      NMSUtil.nms("PacketHandshakingInSetProtocol").getMethod("getProtocolVersion", new Class[0]);
      return true;
    } catch (Exception e) {
      return false;
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\classgenerator\ClassGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */