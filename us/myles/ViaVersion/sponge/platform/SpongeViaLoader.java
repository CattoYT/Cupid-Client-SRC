package us.myles.ViaVersion.sponge.platform;

import java.util.HashSet;
import java.util.Set;
import org.spongepowered.api.Sponge;
import us.myles.ViaVersion.SpongePlugin;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.platform.TaskId;
import us.myles.ViaVersion.api.platform.ViaPlatformLoader;
import us.myles.ViaVersion.api.platform.providers.Provider;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BulkChunkTranslatorProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import us.myles.ViaVersion.sponge.listeners.UpdateListener;
import us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.BlockListener;
import us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.DeathListener;
import us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.HandItemCache;
import us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.sponge4.Sponge4ArmorListener;
import us.myles.ViaVersion.sponge.listeners.protocol1_9to1_8.sponge5.Sponge5ArmorListener;
import us.myles.ViaVersion.sponge.providers.SpongeViaBulkChunkTranslator;
import us.myles.ViaVersion.sponge.providers.SpongeViaMovementTransmitter;

public class SpongeViaLoader implements ViaPlatformLoader {
  private final SpongePlugin plugin;
  
  private final Set<Object> listeners = new HashSet();
  
  private final Set<TaskId> tasks = new HashSet<>();
  
  public SpongeViaLoader(SpongePlugin plugin) {
    this.plugin = plugin;
  }
  
  private void registerListener(Object listener) {
    Sponge.getEventManager().registerListeners(this.plugin, storeListener(listener));
  }
  
  private <T> T storeListener(T listener) {
    this.listeners.add(listener);
    return listener;
  }
  
  public void load() {
    registerListener(new UpdateListener());
    if (ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_9.getVersion()) {
      try {
        Class.forName("org.spongepowered.api.event.entity.DisplaceEntityEvent");
        ((Sponge4ArmorListener)storeListener(new Sponge4ArmorListener())).register();
      } catch (ClassNotFoundException e) {
        ((Sponge5ArmorListener)storeListener(new Sponge5ArmorListener(this.plugin))).register();
      } 
      ((DeathListener)storeListener(new DeathListener(this.plugin))).register();
      ((BlockListener)storeListener(new BlockListener(this.plugin))).register();
      if (this.plugin.getConf().isItemCache()) {
        this.tasks.add(Via.getPlatform().runRepeatingSync((Runnable)new HandItemCache(), Long.valueOf(2L)));
        HandItemCache.CACHE = true;
      } 
    } 
    if (ProtocolRegistry.SERVER_PROTOCOL < ProtocolVersion.v1_9.getVersion()) {
      Via.getManager().getProviders().use(BulkChunkTranslatorProvider.class, (Provider)new SpongeViaBulkChunkTranslator());
      Via.getManager().getProviders().use(MovementTransmitterProvider.class, (Provider)new SpongeViaMovementTransmitter());
      Via.getManager().getProviders().use(HandItemProvider.class, (Provider)new HandItemProvider() {
            public Item getHandItem(UserConnection info) {
              if (HandItemCache.CACHE)
                return HandItemCache.getHandItem(info.getProtocolInfo().getUuid()); 
              return super.getHandItem(info);
            }
          });
    } 
  }
  
  public void unload() {
    this.listeners.forEach(Sponge.getEventManager()::unregisterListeners);
    this.listeners.clear();
    this.tasks.forEach(Via.getPlatform()::cancelTask);
    this.tasks.clear();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\platform\SpongeViaLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */