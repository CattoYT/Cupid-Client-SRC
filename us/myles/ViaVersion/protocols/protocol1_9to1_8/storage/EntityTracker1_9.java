package us.myles.ViaVersion.protocols.protocol1_9to1_8.storage;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_10Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_9;
import us.myles.ViaVersion.api.storage.EntityTracker;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.chat.GameMode;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BossBarProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.EntityIdProvider;

public class EntityTracker1_9 extends EntityTracker {
  private final Map<Integer, UUID> uuidMap = new ConcurrentHashMap<>();
  
  private final Map<Integer, List<Metadata>> metadataBuffer = new ConcurrentHashMap<>();
  
  private final Map<Integer, Integer> vehicleMap = new ConcurrentHashMap<>();
  
  private final Map<Integer, BossBar> bossBarMap = new ConcurrentHashMap<>();
  
  private final Set<Integer> validBlocking = Sets.newConcurrentHashSet();
  
  private final Set<Integer> knownHolograms = Sets.newConcurrentHashSet();
  
  private final Set<Position> blockInteractions = Collections.newSetFromMap(CacheBuilder.newBuilder()
      .maximumSize(10L)
      .expireAfterAccess(250L, TimeUnit.MILLISECONDS)
      .build()
      .asMap());
  
  private boolean blocking = false;
  
  private boolean autoTeam = false;
  
  private Position currentlyDigging = null;
  
  private boolean teamExists = false;
  
  private GameMode gameMode;
  
  private String currentTeam;
  
  public EntityTracker1_9(UserConnection user) {
    super(user, (EntityType)Entity1_10Types.EntityType.PLAYER);
  }
  
  public UUID getEntityUUID(int id) {
    UUID uuid = this.uuidMap.get(Integer.valueOf(id));
    if (uuid == null) {
      uuid = UUID.randomUUID();
      this.uuidMap.put(Integer.valueOf(id), uuid);
    } 
    return uuid;
  }
  
  public void setSecondHand(Item item) {
    setSecondHand(getClientEntityId(), item);
  }
  
  public void setSecondHand(int entityID, Item item) {
    PacketWrapper wrapper = new PacketWrapper(60, null, getUser());
    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(entityID));
    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(1));
    wrapper.write(Type.ITEM, item);
    try {
      wrapper.send(Protocol1_9To1_8.class);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void removeEntity(int entityId) {
    super.removeEntity(entityId);
    this.vehicleMap.remove(Integer.valueOf(entityId));
    this.uuidMap.remove(Integer.valueOf(entityId));
    this.validBlocking.remove(Integer.valueOf(entityId));
    this.knownHolograms.remove(Integer.valueOf(entityId));
    this.metadataBuffer.remove(Integer.valueOf(entityId));
    BossBar bar = this.bossBarMap.remove(Integer.valueOf(entityId));
    if (bar != null) {
      bar.hide();
      ((BossBarProvider)Via.getManager().getProviders().get(BossBarProvider.class)).handleRemove(getUser(), bar.getId());
    } 
  }
  
  public boolean interactedBlockRecently(int x, int y, int z) {
    return this.blockInteractions.contains(new Position(x, (short)y, z));
  }
  
  public void addBlockInteraction(Position p) {
    this.blockInteractions.add(p);
  }
  
  public void handleMetadata(int entityId, List<Metadata> metadataList) {
    EntityType type = getEntity(entityId);
    if (type == null)
      return; 
    for (Metadata metadata : new ArrayList(metadataList)) {
      if (type == Entity1_10Types.EntityType.WITHER && 
        metadata.getId() == 10)
        metadataList.remove(metadata); 
      if (type == Entity1_10Types.EntityType.ENDER_DRAGON && 
        metadata.getId() == 11)
        metadataList.remove(metadata); 
      if (type == Entity1_10Types.EntityType.SKELETON && 
        getMetaByIndex(metadataList, 12) == null)
        metadataList.add(new Metadata(12, (MetaType)MetaType1_9.Boolean, Boolean.valueOf(true))); 
      if (type == Entity1_10Types.EntityType.HORSE)
        if (metadata.getId() == 16 && ((Integer)metadata.getValue()).intValue() == Integer.MIN_VALUE)
          metadata.setValue(Integer.valueOf(0));  
      if (type == Entity1_10Types.EntityType.PLAYER) {
        if (metadata.getId() == 0) {
          byte data = ((Byte)metadata.getValue()).byteValue();
          if (entityId != getProvidedEntityId() && Via.getConfig().isShieldBlocking())
            if ((data & 0x10) == 16) {
              if (this.validBlocking.contains(Integer.valueOf(entityId))) {
                Item shield = new Item(442, (byte)1, (short)0, null);
                setSecondHand(entityId, shield);
              } else {
                setSecondHand(entityId, (Item)null);
              } 
            } else {
              setSecondHand(entityId, (Item)null);
            }  
        } 
        if (metadata.getId() == 12 && Via.getConfig().isLeftHandedHandling())
          metadataList.add(new Metadata(13, (MetaType)MetaType1_9.Byte, 
                
                Byte.valueOf((byte)(((((Byte)metadata.getValue()).byteValue() & 0x80) != 0) ? 0 : 1)))); 
      } 
      if (type == Entity1_10Types.EntityType.ARMOR_STAND && Via.getConfig().isHologramPatch() && 
        metadata.getId() == 0 && getMetaByIndex(metadataList, 10) != null) {
        Metadata meta = getMetaByIndex(metadataList, 10);
        byte data = ((Byte)metadata.getValue()).byteValue();
        if ((data & 0x20) == 32 && (((Byte)meta.getValue()).byteValue() & 0x1) == 1 && 
          !((String)getMetaByIndex(metadataList, 2).getValue()).isEmpty() && ((Boolean)getMetaByIndex(metadataList, 3).getValue()).booleanValue() && 
          !this.knownHolograms.contains(Integer.valueOf(entityId))) {
          this.knownHolograms.add(Integer.valueOf(entityId));
          try {
            PacketWrapper wrapper = new PacketWrapper(37, null, getUser());
            wrapper.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
            wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
            wrapper.write((Type)Type.SHORT, Short.valueOf((short)(int)(128.0D * Via.getConfig().getHologramYOffset() * 32.0D)));
            wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
            wrapper.write(Type.BOOLEAN, Boolean.valueOf(true));
            wrapper.send(Protocol1_9To1_8.class, true, false);
          } catch (Exception exception) {}
        } 
      } 
      if (Via.getConfig().isBossbarPatch() && (
        type == Entity1_10Types.EntityType.ENDER_DRAGON || type == Entity1_10Types.EntityType.WITHER)) {
        if (metadata.getId() == 2) {
          BossBar bar = this.bossBarMap.get(Integer.valueOf(entityId));
          String title = (String)metadata.getValue();
          title = title.isEmpty() ? ((type == Entity1_10Types.EntityType.ENDER_DRAGON) ? "Ender Dragon" : "Wither") : title;
          if (bar == null) {
            bar = Via.getAPI().createBossBar(title, BossColor.PINK, BossStyle.SOLID);
            this.bossBarMap.put(Integer.valueOf(entityId), bar);
            bar.addConnection(getUser());
            bar.show();
            ((BossBarProvider)Via.getManager().getProviders().get(BossBarProvider.class)).handleAdd(getUser(), bar.getId());
            continue;
          } 
          bar.setTitle(title);
          continue;
        } 
        if (metadata.getId() == 6 && !Via.getConfig().isBossbarAntiflicker()) {
          BossBar bar = this.bossBarMap.get(Integer.valueOf(entityId));
          float maxHealth = (type == Entity1_10Types.EntityType.ENDER_DRAGON) ? 200.0F : 300.0F;
          float health = Math.max(0.0F, Math.min(((Float)metadata.getValue()).floatValue() / maxHealth, 1.0F));
          if (bar == null) {
            String title = (type == Entity1_10Types.EntityType.ENDER_DRAGON) ? "Ender Dragon" : "Wither";
            bar = Via.getAPI().createBossBar(title, health, BossColor.PINK, BossStyle.SOLID);
            this.bossBarMap.put(Integer.valueOf(entityId), bar);
            bar.addConnection(getUser());
            bar.show();
            ((BossBarProvider)Via.getManager().getProviders().get(BossBarProvider.class)).handleAdd(getUser(), bar.getId());
            continue;
          } 
          bar.setHealth(health);
        } 
      } 
    } 
  }
  
  public Metadata getMetaByIndex(List<Metadata> list, int index) {
    for (Metadata meta : list) {
      if (index == meta.getId())
        return meta; 
    } 
    return null;
  }
  
  public void sendTeamPacket(boolean add, boolean now) {
    PacketWrapper wrapper = new PacketWrapper(65, null, getUser());
    wrapper.write(Type.STRING, "viaversion");
    if (add) {
      if (!this.teamExists) {
        wrapper.write(Type.BYTE, Byte.valueOf((byte)0));
        wrapper.write(Type.STRING, "viaversion");
        wrapper.write(Type.STRING, "§f");
        wrapper.write(Type.STRING, "");
        wrapper.write(Type.BYTE, Byte.valueOf((byte)0));
        wrapper.write(Type.STRING, "");
        wrapper.write(Type.STRING, "never");
        wrapper.write(Type.BYTE, Byte.valueOf((byte)15));
      } else {
        wrapper.write(Type.BYTE, Byte.valueOf((byte)3));
      } 
      wrapper.write(Type.STRING_ARRAY, new String[] { getUser().getProtocolInfo().getUsername() });
    } else {
      wrapper.write(Type.BYTE, Byte.valueOf((byte)1));
    } 
    this.teamExists = add;
    try {
      wrapper.send(Protocol1_9To1_8.class, true, now);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void addMetadataToBuffer(int entityID, List<Metadata> metadataList) {
    List<Metadata> metadata = this.metadataBuffer.get(Integer.valueOf(entityID));
    if (metadata != null) {
      metadata.addAll(metadataList);
    } else {
      this.metadataBuffer.put(Integer.valueOf(entityID), metadataList);
    } 
  }
  
  public void sendMetadataBuffer(int entityId) {
    List<Metadata> metadataList = this.metadataBuffer.get(Integer.valueOf(entityId));
    if (metadataList != null) {
      PacketWrapper wrapper = new PacketWrapper(57, null, getUser());
      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
      wrapper.write(Types1_9.METADATA_LIST, metadataList);
      ((MetadataRewriter1_9To1_8)((Protocol1_9To1_8)getUser().getProtocolInfo().getPipeline().getProtocol(Protocol1_9To1_8.class)).get(MetadataRewriter1_9To1_8.class))
        .handleMetadata(entityId, metadataList, getUser());
      handleMetadata(entityId, metadataList);
      if (!metadataList.isEmpty())
        try {
          wrapper.send(Protocol1_9To1_8.class);
        } catch (Exception e) {
          e.printStackTrace();
        }  
      this.metadataBuffer.remove(Integer.valueOf(entityId));
    } 
  }
  
  public int getProvidedEntityId() {
    try {
      return ((EntityIdProvider)Via.getManager().getProviders().get(EntityIdProvider.class)).getEntityId(getUser());
    } catch (Exception e) {
      return getClientEntityId();
    } 
  }
  
  public Map<Integer, UUID> getUuidMap() {
    return this.uuidMap;
  }
  
  public Map<Integer, List<Metadata>> getMetadataBuffer() {
    return this.metadataBuffer;
  }
  
  public Map<Integer, Integer> getVehicleMap() {
    return this.vehicleMap;
  }
  
  public Map<Integer, BossBar> getBossBarMap() {
    return this.bossBarMap;
  }
  
  public Set<Integer> getValidBlocking() {
    return this.validBlocking;
  }
  
  public Set<Integer> getKnownHolograms() {
    return this.knownHolograms;
  }
  
  public Set<Position> getBlockInteractions() {
    return this.blockInteractions;
  }
  
  public boolean isBlocking() {
    return this.blocking;
  }
  
  public void setBlocking(boolean blocking) {
    this.blocking = blocking;
  }
  
  public boolean isAutoTeam() {
    return this.autoTeam;
  }
  
  public void setAutoTeam(boolean autoTeam) {
    this.autoTeam = autoTeam;
  }
  
  public Position getCurrentlyDigging() {
    return this.currentlyDigging;
  }
  
  public void setCurrentlyDigging(Position currentlyDigging) {
    this.currentlyDigging = currentlyDigging;
  }
  
  public boolean isTeamExists() {
    return this.teamExists;
  }
  
  public GameMode getGameMode() {
    return this.gameMode;
  }
  
  public void setGameMode(GameMode gameMode) {
    this.gameMode = gameMode;
  }
  
  public String getCurrentTeam() {
    return this.currentTeam;
  }
  
  public void setCurrentTeam(String currentTeam) {
    this.currentTeam = currentTeam;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\storage\EntityTracker1_9.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */