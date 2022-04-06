package us.myles.ViaVersion.protocols.protocol1_9to1_8;

import java.util.List;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.platform.providers.Provider;
import us.myles.ViaVersion.api.platform.providers.ViaProviders;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.remapper.ValueTransformer;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.protocol1_8.ClientboundPackets1_8;
import us.myles.ViaVersion.protocols.protocol1_8.ServerboundPackets1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.packets.EntityPackets;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.packets.PlayerPackets;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.packets.SpawnPackets;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.packets.WorldPackets;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BossBarProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BulkChunkTranslatorProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.CommandBlockProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.EntityIdProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MainHandProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.ClientChunks;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.CommandBlockStorage;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.InventoryTracker;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.MovementTracker;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.PlaceBlockTracker;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;

public class Protocol1_9To1_8 extends Protocol<ClientboundPackets1_8, ClientboundPackets1_9, ServerboundPackets1_8, ServerboundPackets1_9> {
  public static final ValueTransformer<String, JsonElement> FIX_JSON = new ValueTransformer<String, JsonElement>(Type.COMPONENT) {
      public JsonElement transform(PacketWrapper wrapper, String line) {
        return Protocol1_9To1_8.fixJson(line);
      }
    };
  
  public Protocol1_9To1_8() {
    super(ClientboundPackets1_8.class, ClientboundPackets1_9.class, ServerboundPackets1_8.class, ServerboundPackets1_9.class);
  }
  
  public static JsonElement fixJson(String line) {
    if (line == null || line.equalsIgnoreCase("null")) {
      line = "{\"text\":\"\"}";
    } else {
      if ((!line.startsWith("\"") || !line.endsWith("\"")) && (!line.startsWith("{") || !line.endsWith("}")))
        return constructJson(line); 
      if (line.startsWith("\"") && line.endsWith("\""))
        line = "{\"text\":" + line + "}"; 
    } 
    try {
      return (JsonElement)GsonUtil.getGson().fromJson(line, JsonObject.class);
    } catch (Exception e) {
      if (Via.getConfig().isForceJsonTransform())
        return constructJson(line); 
      Via.getPlatform().getLogger().warning("Invalid JSON String: \"" + line + "\" Please report this issue to the ViaVersion Github: " + e.getMessage());
      return (JsonElement)GsonUtil.getGson().fromJson("{\"text\":\"\"}", JsonObject.class);
    } 
  }
  
  private static JsonElement constructJson(String text) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("text", text);
    return (JsonElement)jsonObject;
  }
  
  public static Item getHandItem(UserConnection info) {
    return ((HandItemProvider)Via.getManager().getProviders().get(HandItemProvider.class)).getHandItem(info);
  }
  
  public static boolean isSword(int id) {
    if (id == 267)
      return true; 
    if (id == 268)
      return true; 
    if (id == 272)
      return true; 
    if (id == 276)
      return true; 
    if (id == 283)
      return true; 
    return false;
  }
  
  protected void registerPackets() {
    MetadataRewriter1_9To1_8 metadataRewriter1_9To1_8 = new MetadataRewriter1_9To1_8(this);
    registerOutgoing(State.LOGIN, 0, 0, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  if (wrapper.isReadable(Type.COMPONENT, 0))
                    return; 
                  wrapper.write(Type.COMPONENT, Protocol1_9To1_8.fixJson((String)wrapper.read(Type.STRING)));
                });
          }
        });
    SpawnPackets.register(this);
    InventoryPackets.register(this);
    EntityPackets.register(this);
    PlayerPackets.register(this);
    WorldPackets.register(this);
  }
  
  protected void register(ViaProviders providers) {
    providers.register(HandItemProvider.class, (Provider)new HandItemProvider());
    providers.register(BulkChunkTranslatorProvider.class, (Provider)new BulkChunkTranslatorProvider());
    providers.register(CommandBlockProvider.class, (Provider)new CommandBlockProvider());
    providers.register(EntityIdProvider.class, (Provider)new EntityIdProvider());
    providers.register(BossBarProvider.class, (Provider)new BossBarProvider());
    providers.register(MainHandProvider.class, (Provider)new MainHandProvider());
    providers.require(MovementTransmitterProvider.class);
  }
  
  public boolean isFiltered(Class packetClass) {
    return ((BulkChunkTranslatorProvider)Via.getManager().getProviders().get(BulkChunkTranslatorProvider.class)).isFiltered(packetClass);
  }
  
  protected void filterPacket(UserConnection info, Object packet, List output) throws Exception {
    output.addAll(((ClientChunks)info.get(ClientChunks.class)).transformMapChunkBulk(packet));
  }
  
  public void init(UserConnection userConnection) {
    userConnection.put((StoredObject)new EntityTracker1_9(userConnection));
    userConnection.put((StoredObject)new ClientChunks(userConnection));
    userConnection.put((StoredObject)new MovementTracker(userConnection));
    userConnection.put((StoredObject)new InventoryTracker(userConnection));
    userConnection.put((StoredObject)new PlaceBlockTracker(userConnection));
    userConnection.put((StoredObject)new CommandBlockStorage(userConnection));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\Protocol1_9To1_8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */