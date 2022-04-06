package us.myles.ViaVersion.api.protocol;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.Pair;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.MappingDataLoader;
import us.myles.ViaVersion.protocols.base.BaseProtocol;
import us.myles.ViaVersion.protocols.base.BaseProtocol1_16;
import us.myles.ViaVersion.protocols.base.BaseProtocol1_7;
import us.myles.ViaVersion.protocols.protocol1_10to1_9_3.Protocol1_10To1_9_3_4;
import us.myles.ViaVersion.protocols.protocol1_11_1to1_11.Protocol1_11_1To1_11;
import us.myles.ViaVersion.protocols.protocol1_11to1_10.Protocol1_11To1_10;
import us.myles.ViaVersion.protocols.protocol1_12_1to1_12.Protocol1_12_1To1_12;
import us.myles.ViaVersion.protocols.protocol1_12_2to1_12_1.Protocol1_12_2To1_12_1;
import us.myles.ViaVersion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import us.myles.ViaVersion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import us.myles.ViaVersion.protocols.protocol1_13_2to1_13_1.Protocol1_13_2To1_13_1;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import us.myles.ViaVersion.protocols.protocol1_14_1to1_14.Protocol1_14_1To1_14;
import us.myles.ViaVersion.protocols.protocol1_14_2to1_14_1.Protocol1_14_2To1_14_1;
import us.myles.ViaVersion.protocols.protocol1_14_3to1_14_2.Protocol1_14_3To1_14_2;
import us.myles.ViaVersion.protocols.protocol1_14_4to1_14_3.Protocol1_14_4To1_14_3;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import us.myles.ViaVersion.protocols.protocol1_15_1to1_15.Protocol1_15_1To1_15;
import us.myles.ViaVersion.protocols.protocol1_15_2to1_15_1.Protocol1_15_2To1_15_1;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import us.myles.ViaVersion.protocols.protocol1_16_1to1_16.Protocol1_16_1To1_16;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import us.myles.ViaVersion.protocols.protocol1_16_3to1_16_2.Protocol1_16_3To1_16_2;
import us.myles.ViaVersion.protocols.protocol1_16_4to1_16_3.Protocol1_16_4To1_16_3;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import us.myles.ViaVersion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import us.myles.ViaVersion.protocols.protocol1_9_1_2to1_9_3_4.Protocol1_9_1_2To1_9_3_4;
import us.myles.ViaVersion.protocols.protocol1_9_1to1_9.Protocol1_9_1To1_9;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.Protocol1_9_3To1_9_1_2;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_9_1.Protocol1_9To1_9_1;
import us.myles.viaversion.libs.fastutil.ints.Int2ObjectMap;
import us.myles.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import us.myles.viaversion.libs.fastutil.objects.ObjectIterator;

public class ProtocolRegistry {
  public static final Protocol BASE_PROTOCOL = (Protocol)new BaseProtocol();
  
  public static int SERVER_PROTOCOL = -1;
  
  public static int maxProtocolPathSize = 50;
  
  private static final Int2ObjectMap<Int2ObjectMap<Protocol>> registryMap = (Int2ObjectMap<Int2ObjectMap<Protocol>>)new Int2ObjectOpenHashMap(32);
  
  private static final Map<Class<? extends Protocol>, Protocol> protocols = new HashMap<>();
  
  private static final Map<Pair<Integer, Integer>, List<Pair<Integer, Protocol>>> pathCache = new ConcurrentHashMap<>();
  
  private static final Set<Integer> supportedVersions = new HashSet<>();
  
  private static final List<Pair<Range<Integer>, Protocol>> baseProtocols = Lists.newCopyOnWriteArrayList();
  
  private static final List<Protocol> registerList = new ArrayList<>();
  
  private static final Object MAPPING_LOADER_LOCK = new Object();
  
  private static Map<Class<? extends Protocol>, CompletableFuture<Void>> mappingLoaderFutures = new HashMap<>();
  
  private static ThreadPoolExecutor mappingLoaderExecutor;
  
  private static boolean mappingsLoaded;
  
  static {
    ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setNameFormat("Via-Mappingloader-%d").build();
    mappingLoaderExecutor = new ThreadPoolExecutor(5, 16, 45L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory);
    mappingLoaderExecutor.allowCoreThreadTimeOut(true);
    registerBaseProtocol(BASE_PROTOCOL, Range.lessThan(Integer.valueOf(-2147483648)));
    registerBaseProtocol((Protocol)new BaseProtocol1_7(), Range.lessThan(Integer.valueOf(ProtocolVersion.v1_16.getVersion())));
    registerBaseProtocol((Protocol)new BaseProtocol1_16(), Range.atLeast(Integer.valueOf(ProtocolVersion.v1_16.getVersion())));
    registerProtocol((Protocol)new Protocol1_9To1_8(), ProtocolVersion.v1_9, ProtocolVersion.v1_8);
    registerProtocol((Protocol)new Protocol1_9_1To1_9(), Arrays.asList(new Integer[] { Integer.valueOf(ProtocolVersion.v1_9_1.getVersion()), Integer.valueOf(ProtocolVersion.v1_9_2.getVersion()) }, ), ProtocolVersion.v1_9.getVersion());
    registerProtocol((Protocol)new Protocol1_9_3To1_9_1_2(), ProtocolVersion.v1_9_3, ProtocolVersion.v1_9_2);
    registerProtocol((Protocol)new Protocol1_9To1_9_1(), ProtocolVersion.v1_9, ProtocolVersion.v1_9_2);
    registerProtocol((Protocol)new Protocol1_9_1_2To1_9_3_4(), Arrays.asList(new Integer[] { Integer.valueOf(ProtocolVersion.v1_9_1.getVersion()), Integer.valueOf(ProtocolVersion.v1_9_2.getVersion()) }, ), ProtocolVersion.v1_9_3.getVersion());
    registerProtocol((Protocol)new Protocol1_10To1_9_3_4(), ProtocolVersion.v1_10, ProtocolVersion.v1_9_3);
    registerProtocol((Protocol)new Protocol1_11To1_10(), ProtocolVersion.v1_11, ProtocolVersion.v1_10);
    registerProtocol((Protocol)new Protocol1_11_1To1_11(), ProtocolVersion.v1_11_1, ProtocolVersion.v1_11);
    registerProtocol((Protocol)new Protocol1_12To1_11_1(), ProtocolVersion.v1_12, ProtocolVersion.v1_11_1);
    registerProtocol((Protocol)new Protocol1_12_1To1_12(), ProtocolVersion.v1_12_1, ProtocolVersion.v1_12);
    registerProtocol((Protocol)new Protocol1_12_2To1_12_1(), ProtocolVersion.v1_12_2, ProtocolVersion.v1_12_1);
    registerProtocol((Protocol)new Protocol1_13To1_12_2(), ProtocolVersion.v1_13, ProtocolVersion.v1_12_2);
    registerProtocol((Protocol)new Protocol1_13_1To1_13(), ProtocolVersion.v1_13_1, ProtocolVersion.v1_13);
    registerProtocol((Protocol)new Protocol1_13_2To1_13_1(), ProtocolVersion.v1_13_2, ProtocolVersion.v1_13_1);
    registerProtocol((Protocol)new Protocol1_14To1_13_2(), ProtocolVersion.v1_14, ProtocolVersion.v1_13_2);
    registerProtocol((Protocol)new Protocol1_14_1To1_14(), ProtocolVersion.v1_14_1, ProtocolVersion.v1_14);
    registerProtocol((Protocol)new Protocol1_14_2To1_14_1(), ProtocolVersion.v1_14_2, ProtocolVersion.v1_14_1);
    registerProtocol((Protocol)new Protocol1_14_3To1_14_2(), ProtocolVersion.v1_14_3, ProtocolVersion.v1_14_2);
    registerProtocol((Protocol)new Protocol1_14_4To1_14_3(), ProtocolVersion.v1_14_4, ProtocolVersion.v1_14_3);
    registerProtocol((Protocol)new Protocol1_15To1_14_4(), ProtocolVersion.v1_15, ProtocolVersion.v1_14_4);
    registerProtocol((Protocol)new Protocol1_15_1To1_15(), ProtocolVersion.v1_15_1, ProtocolVersion.v1_15);
    registerProtocol((Protocol)new Protocol1_15_2To1_15_1(), ProtocolVersion.v1_15_2, ProtocolVersion.v1_15_1);
    registerProtocol((Protocol)new Protocol1_16To1_15_2(), ProtocolVersion.v1_16, ProtocolVersion.v1_15_2);
    registerProtocol((Protocol)new Protocol1_16_1To1_16(), ProtocolVersion.v1_16_1, ProtocolVersion.v1_16);
    registerProtocol((Protocol)new Protocol1_16_2To1_16_1(), ProtocolVersion.v1_16_2, ProtocolVersion.v1_16_1);
    registerProtocol((Protocol)new Protocol1_16_3To1_16_2(), ProtocolVersion.v1_16_3, ProtocolVersion.v1_16_2);
    registerProtocol((Protocol)new Protocol1_16_4To1_16_3(), ProtocolVersion.v1_16_4, ProtocolVersion.v1_16_3);
    registerProtocol((Protocol)new Protocol1_17To1_16_4(), ProtocolVersion.v1_17, ProtocolVersion.v1_16_4);
  }
  
  public static void init() {}
  
  public static void registerProtocol(Protocol protocol, ProtocolVersion supported, ProtocolVersion output) {
    registerProtocol(protocol, Collections.singletonList(Integer.valueOf(supported.getVersion())), output.getVersion());
  }
  
  public static void registerProtocol(Protocol protocol, List<Integer> supported, int output) {
    if (!pathCache.isEmpty())
      pathCache.clear(); 
    protocols.put(protocol.getClass(), protocol);
    for (Iterator<Integer> iterator = supported.iterator(); iterator.hasNext(); ) {
      int version = ((Integer)iterator.next()).intValue();
      Int2ObjectMap<Protocol> protocolMap = (Int2ObjectMap<Protocol>)registryMap.computeIfAbsent(version, s -> new Int2ObjectOpenHashMap(2));
      protocolMap.put(output, protocol);
    } 
    if (Via.getPlatform().isPluginEnabled()) {
      protocol.register(Via.getManager().getProviders());
      refreshVersions();
    } else {
      registerList.add(protocol);
    } 
    if (protocol.hasMappingDataToLoad())
      if (mappingLoaderExecutor != null) {
        addMappingLoaderFuture((Class)protocol.getClass(), protocol::loadMappingData);
      } else {
        protocol.loadMappingData();
      }  
  }
  
  public static void registerBaseProtocol(Protocol baseProtocol, Range<Integer> supportedProtocols) {
    baseProtocols.add(new Pair(supportedProtocols, baseProtocol));
    if (Via.getPlatform().isPluginEnabled()) {
      baseProtocol.register(Via.getManager().getProviders());
      refreshVersions();
    } else {
      registerList.add(baseProtocol);
    } 
  }
  
  public static void refreshVersions() {
    supportedVersions.clear();
    supportedVersions.add(Integer.valueOf(SERVER_PROTOCOL));
    for (ProtocolVersion versions : ProtocolVersion.getProtocols()) {
      List<Pair<Integer, Protocol>> paths = getProtocolPath(versions.getVersion(), SERVER_PROTOCOL);
      if (paths == null)
        continue; 
      supportedVersions.add(Integer.valueOf(versions.getVersion()));
      for (Pair<Integer, Protocol> path : paths)
        supportedVersions.add(path.getKey()); 
    } 
  }
  
  public static SortedSet<Integer> getSupportedVersions() {
    return Collections.unmodifiableSortedSet(new TreeSet<>(supportedVersions));
  }
  
  public static boolean isWorkingPipe() {
    for (ObjectIterator<Int2ObjectMap<Protocol>> objectIterator = registryMap.values().iterator(); objectIterator.hasNext(); ) {
      Int2ObjectMap<Protocol> map = objectIterator.next();
      if (map.containsKey(SERVER_PROTOCOL))
        return true; 
    } 
    return false;
  }
  
  public static void onServerLoaded() {
    for (Protocol protocol : registerList)
      protocol.register(Via.getManager().getProviders()); 
    registerList.clear();
  }
  
  @Nullable
  private static List<Pair<Integer, Protocol>> getProtocolPath(List<Pair<Integer, Protocol>> current, int clientVersion, int serverVersion) {
    if (clientVersion == serverVersion)
      return null; 
    if (current.size() > maxProtocolPathSize)
      return null; 
    Int2ObjectMap<Protocol> inputMap = (Int2ObjectMap<Protocol>)registryMap.get(clientVersion);
    if (inputMap == null)
      return null; 
    Protocol protocol = (Protocol)inputMap.get(serverVersion);
    if (protocol != null) {
      current.add(new Pair(Integer.valueOf(serverVersion), protocol));
      return current;
    } 
    List<Pair<Integer, Protocol>> shortest = null;
    for (ObjectIterator<Int2ObjectMap.Entry<Protocol>> objectIterator = inputMap.int2ObjectEntrySet().iterator(); objectIterator.hasNext(); ) {
      Int2ObjectMap.Entry<Protocol> entry = objectIterator.next();
      if (entry.getIntKey() == serverVersion)
        continue; 
      Pair<Integer, Protocol> pair = new Pair(Integer.valueOf(entry.getIntKey()), entry.getValue());
      if (current.contains(pair))
        continue; 
      List<Pair<Integer, Protocol>> newCurrent = new ArrayList<>(current);
      newCurrent.add(pair);
      newCurrent = getProtocolPath(newCurrent, entry.getKey().intValue(), serverVersion);
      if (newCurrent != null)
        if (shortest == null || shortest.size() > newCurrent.size())
          shortest = newCurrent;  
    } 
    return shortest;
  }
  
  @Nullable
  public static List<Pair<Integer, Protocol>> getProtocolPath(int clientVersion, int serverVersion) {
    Pair<Integer, Integer> protocolKey = new Pair(Integer.valueOf(clientVersion), Integer.valueOf(serverVersion));
    List<Pair<Integer, Protocol>> protocolList = pathCache.get(protocolKey);
    if (protocolList != null)
      return protocolList; 
    List<Pair<Integer, Protocol>> outputPath = getProtocolPath(new ArrayList<>(), clientVersion, serverVersion);
    if (outputPath != null)
      pathCache.put(protocolKey, outputPath); 
    return outputPath;
  }
  
  @Nullable
  public static Protocol getProtocol(Class<? extends Protocol> protocolClass) {
    return protocols.get(protocolClass);
  }
  
  public static Protocol getBaseProtocol(int serverVersion) {
    for (Pair<Range<Integer>, Protocol> rangeProtocol : (Iterable<Pair<Range<Integer>, Protocol>>)Lists.reverse(baseProtocols)) {
      if (((Range)rangeProtocol.getKey()).contains(Integer.valueOf(serverVersion)))
        return (Protocol)rangeProtocol.getValue(); 
    } 
    throw new IllegalStateException("No Base Protocol for " + serverVersion);
  }
  
  public static boolean isBaseProtocol(Protocol protocol) {
    for (Pair<Range<Integer>, Protocol> p : baseProtocols) {
      if (p.getValue() == protocol)
        return true; 
    } 
    return false;
  }
  
  public static void completeMappingDataLoading(Class<? extends Protocol> protocolClass) throws Exception {
    if (mappingsLoaded)
      return; 
    CompletableFuture<Void> future = getMappingLoaderFuture(protocolClass);
    if (future == null)
      return; 
    future.get();
  }
  
  public static boolean checkForMappingCompletion() {
    synchronized (MAPPING_LOADER_LOCK) {
      if (mappingsLoaded)
        return false; 
      for (CompletableFuture<Void> future : mappingLoaderFutures.values()) {
        if (!future.isDone())
          return false; 
      } 
      shutdownLoaderExecutor();
      return true;
    } 
  }
  
  private static void shutdownLoaderExecutor() {
    Via.getPlatform().getLogger().info("Shutting down mapping loader executor!");
    mappingsLoaded = true;
    mappingLoaderExecutor.shutdown();
    mappingLoaderExecutor = null;
    mappingLoaderFutures.clear();
    mappingLoaderFutures = null;
    if (MappingDataLoader.isCacheJsonMappings())
      MappingDataLoader.getMappingsCache().clear(); 
  }
  
  public static void addMappingLoaderFuture(Class<? extends Protocol> protocolClass, Runnable runnable) {
    synchronized (MAPPING_LOADER_LOCK) {
      CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, mappingLoaderExecutor).exceptionally(throwable -> {
            Via.getPlatform().getLogger().severe("Error during mapping loading of " + protocolClass.getSimpleName());
            throwable.printStackTrace();
            return null;
          });
      mappingLoaderFutures.put(protocolClass, future);
    } 
  }
  
  public static void addMappingLoaderFuture(Class<? extends Protocol> protocolClass, Class<? extends Protocol> dependsOn, Runnable runnable) {
    synchronized (MAPPING_LOADER_LOCK) {
      CompletableFuture<Void> future = getMappingLoaderFuture(dependsOn).whenCompleteAsync((v, throwable) -> runnable.run(), mappingLoaderExecutor).exceptionally(throwable -> {
            Via.getPlatform().getLogger().severe("Error during mapping loading of " + protocolClass.getSimpleName());
            throwable.printStackTrace();
            return null;
          });
      mappingLoaderFutures.put(protocolClass, future);
    } 
  }
  
  @Nullable
  public static CompletableFuture<Void> getMappingLoaderFuture(Class<? extends Protocol> protocolClass) {
    synchronized (MAPPING_LOADER_LOCK) {
      if (mappingsLoaded)
        return null; 
      return mappingLoaderFutures.get(protocolClass);
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\protocol\ProtocolRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */