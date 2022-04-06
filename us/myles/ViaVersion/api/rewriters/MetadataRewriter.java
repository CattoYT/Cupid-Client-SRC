package us.myles.ViaVersion.api.rewriters;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.ParticleMappings;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.storage.EntityTracker;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.Particle;
import us.myles.viaversion.libs.fastutil.ints.Int2IntMap;
import us.myles.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public abstract class MetadataRewriter {
  private final Class<? extends EntityTracker> entityTrackerClass;
  
  protected final Protocol protocol;
  
  private Int2IntMap typeMapping;
  
  protected MetadataRewriter(Protocol protocol, Class<? extends EntityTracker> entityTrackerClass) {
    this.protocol = protocol;
    this.entityTrackerClass = entityTrackerClass;
    protocol.put(this);
  }
  
  public final void handleMetadata(int entityId, List<Metadata> metadatas, UserConnection connection) {
    EntityType type = ((EntityTracker)connection.get(this.entityTrackerClass)).getEntity(entityId);
    for (Metadata metadata : new ArrayList(metadatas)) {
      try {
        handleMetadata(entityId, type, metadata, metadatas, connection);
      } catch (Exception e) {
        metadatas.remove(metadata);
        if (!Via.getConfig().isSuppressMetadataErrors() || Via.getManager().isDebug()) {
          Logger logger = Via.getPlatform().getLogger();
          logger.warning("An error occurred with entity metadata handler");
          logger.warning("This is most likely down to one of your plugins sending bad datawatchers. Please test if this occurs without any plugins except ViaVersion before reporting it on GitHub");
          logger.warning("Also make sure that all your plugins are compatible with your server version.");
          logger.warning("Entity type: " + type);
          logger.warning("Metadata: " + metadata);
          e.printStackTrace();
        } 
      } 
    } 
  }
  
  protected void rewriteParticle(Particle particle) {
    ParticleMappings mappings = this.protocol.getMappingData().getParticleMappings();
    int id = particle.getId();
    if (id == mappings.getBlockId() || id == mappings.getFallingDustId()) {
      Particle.ParticleData data = particle.getArguments().get(0);
      data.setValue(Integer.valueOf(this.protocol.getMappingData().getNewBlockStateId(((Integer)data.get()).intValue())));
    } else if (id == mappings.getItemId()) {
      Particle.ParticleData data = particle.getArguments().get(0);
      data.setValue(Integer.valueOf(this.protocol.getMappingData().getNewItemId(((Integer)data.get()).intValue())));
    } 
    particle.setId(this.protocol.getMappingData().getNewParticleId(id));
  }
  
  public void registerTracker(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            handler(MetadataRewriter.this.getTracker());
          }
        });
  }
  
  public void registerSpawnTrackerWithData(ClientboundPacketType packetType, final EntityType fallingBlockType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BYTE);
            map(Type.BYTE);
            map(Type.INT);
            handler(MetadataRewriter.this.getTracker());
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  EntityType entityType = ((EntityTracker)wrapper.user().get(MetadataRewriter.this.entityTrackerClass)).getEntity(entityId);
                  if (entityType == fallingBlockType)
                    wrapper.set(Type.INT, 0, Integer.valueOf(MetadataRewriter.this.protocol.getMappingData().getNewBlockStateId(((Integer)wrapper.get(Type.INT, 0)).intValue()))); 
                });
          }
        });
  }
  
  public void registerTracker(ClientboundPacketType packetType, final EntityType entityType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  ((EntityTracker)wrapper.user().get(MetadataRewriter.this.entityTrackerClass)).addEntity(entityId, entityType);
                });
          }
        });
  }
  
  public void registerEntityDestroy(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.VAR_INT_ARRAY_PRIMITIVE);
            handler(wrapper -> {
                  EntityTracker entityTracker = (EntityTracker)wrapper.user().get(MetadataRewriter.this.entityTrackerClass);
                  for (int entity : (int[])wrapper.get(Type.VAR_INT_ARRAY_PRIMITIVE, 0))
                    entityTracker.removeEntity(entity); 
                });
          }
        });
  }
  
  public void registerMetadataRewriter(ClientboundPacketType packetType, @Nullable final Type<List<Metadata>> oldMetaType, final Type<List<Metadata>> newMetaType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            if (oldMetaType != null) {
              map(oldMetaType, newMetaType);
            } else {
              map(newMetaType);
            } 
            handler(wrapper -> {
                  int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  List<Metadata> metadata = (List<Metadata>)wrapper.get(newMetaType, 0);
                  MetadataRewriter.this.handleMetadata(entityId, metadata, wrapper.user());
                });
          }
        });
  }
  
  public void registerMetadataRewriter(ClientboundPacketType packetType, Type<List<Metadata>> metaType) {
    registerMetadataRewriter(packetType, null, metaType);
  }
  
  public <T extends Enum<T> & EntityType> void mapTypes(EntityType[] oldTypes, Class<T> newTypeClass) {
    if (this.typeMapping == null) {
      this.typeMapping = (Int2IntMap)new Int2IntOpenHashMap(oldTypes.length, 1.0F);
      this.typeMapping.defaultReturnValue(-1);
    } 
    for (EntityType oldType : oldTypes) {
      try {
        T newType = (T)Enum.valueOf(newTypeClass, oldType.name());
        this.typeMapping.put(oldType.getId(), ((EntityType)newType).getId());
      } catch (IllegalArgumentException notFound) {
        if (!this.typeMapping.containsKey(oldType.getId()))
          Via.getPlatform().getLogger().warning("Could not find new entity type for " + oldType + "! Old type: " + oldType
              .getClass().getEnclosingClass().getSimpleName() + ", new type: " + newTypeClass.getEnclosingClass().getSimpleName()); 
      } 
    } 
  }
  
  public void mapType(EntityType oldType, EntityType newType) {
    if (this.typeMapping == null) {
      this.typeMapping = (Int2IntMap)new Int2IntOpenHashMap();
      this.typeMapping.defaultReturnValue(-1);
    } 
    this.typeMapping.put(oldType.getId(), newType.getId());
  }
  
  public PacketHandler getTracker() {
    return getTrackerAndRewriter(null);
  }
  
  public PacketHandler getTrackerAndRewriter(@Nullable Type<List<Metadata>> metaType) {
    return wrapper -> {
        int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
        int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
        int newType = getNewEntityId(type);
        if (newType != type)
          wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(newType)); 
        EntityType entType = getTypeFromId(newType);
        ((EntityTracker)wrapper.user().get(this.entityTrackerClass)).addEntity(entityId, entType);
        if (metaType != null)
          handleMetadata(entityId, (List<Metadata>)wrapper.get(metaType, 0), wrapper.user()); 
      };
  }
  
  public PacketHandler getTrackerAndRewriter(@Nullable Type<List<Metadata>> metaType, EntityType entityType) {
    return wrapper -> {
        int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
        ((EntityTracker)wrapper.user().get(this.entityTrackerClass)).addEntity(entityId, entityType);
        if (metaType != null)
          handleMetadata(entityId, (List<Metadata>)wrapper.get(metaType, 0), wrapper.user()); 
      };
  }
  
  public PacketHandler getObjectTracker() {
    return wrapper -> {
        int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
        byte type = ((Byte)wrapper.get(Type.BYTE, 0)).byteValue();
        EntityType entType = getObjectTypeFromId(type);
        ((EntityTracker)wrapper.user().get(this.entityTrackerClass)).addEntity(entityId, entType);
      };
  }
  
  protected EntityType getObjectTypeFromId(int type) {
    return getTypeFromId(type);
  }
  
  public int getNewEntityId(int oldId) {
    return (this.typeMapping != null) ? this.typeMapping.getOrDefault(oldId, oldId) : oldId;
  }
  
  @Nullable
  protected Metadata getMetaByIndex(int index, List<Metadata> metadataList) {
    for (Metadata metadata : metadataList) {
      if (metadata.getId() == index)
        return metadata; 
    } 
    return null;
  }
  
  protected abstract EntityType getTypeFromId(int paramInt);
  
  protected abstract void handleMetadata(int paramInt, @Nullable EntityType paramEntityType, Metadata paramMetadata, List<Metadata> paramList, UserConnection paramUserConnection) throws Exception;
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\rewriters\MetadataRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */