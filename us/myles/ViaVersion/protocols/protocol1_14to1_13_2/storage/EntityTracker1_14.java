package us.myles.ViaVersion.protocols.protocol1_14to1_13_2.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_14Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.storage.EntityTracker;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;

public class EntityTracker1_14 extends EntityTracker {
  private final Map<Integer, Byte> insentientData = new ConcurrentHashMap<>();
  
  private final Map<Integer, Byte> sleepingAndRiptideData = new ConcurrentHashMap<>();
  
  private final Map<Integer, Byte> playerEntityFlags = new ConcurrentHashMap<>();
  
  private int latestTradeWindowId;
  
  private boolean forceSendCenterChunk = true;
  
  private int chunkCenterX;
  
  private int chunkCenterZ;
  
  public EntityTracker1_14(UserConnection user) {
    super(user, (EntityType)Entity1_14Types.EntityType.PLAYER);
  }
  
  public void removeEntity(int entityId) {
    super.removeEntity(entityId);
    this.insentientData.remove(Integer.valueOf(entityId));
    this.sleepingAndRiptideData.remove(Integer.valueOf(entityId));
    this.playerEntityFlags.remove(Integer.valueOf(entityId));
  }
  
  public byte getInsentientData(int entity) {
    Byte val = this.insentientData.get(Integer.valueOf(entity));
    return (val == null) ? 0 : val.byteValue();
  }
  
  public void setInsentientData(int entity, byte value) {
    this.insentientData.put(Integer.valueOf(entity), Byte.valueOf(value));
  }
  
  private static byte zeroIfNull(Byte val) {
    if (val == null)
      return 0; 
    return val.byteValue();
  }
  
  public boolean isSleeping(int player) {
    return ((zeroIfNull(this.sleepingAndRiptideData.get(Integer.valueOf(player))) & 0x1) != 0);
  }
  
  public void setSleeping(int player, boolean value) {
    byte newValue = (byte)(zeroIfNull(this.sleepingAndRiptideData.get(Integer.valueOf(player))) & 0xFFFFFFFE | (value ? 1 : 0));
    if (newValue == 0) {
      this.sleepingAndRiptideData.remove(Integer.valueOf(player));
    } else {
      this.sleepingAndRiptideData.put(Integer.valueOf(player), Byte.valueOf(newValue));
    } 
  }
  
  public boolean isRiptide(int player) {
    return ((zeroIfNull(this.sleepingAndRiptideData.get(Integer.valueOf(player))) & 0x2) != 0);
  }
  
  public void setRiptide(int player, boolean value) {
    byte newValue = (byte)(zeroIfNull(this.sleepingAndRiptideData.get(Integer.valueOf(player))) & 0xFFFFFFFD | (value ? 2 : 0));
    if (newValue == 0) {
      this.sleepingAndRiptideData.remove(Integer.valueOf(player));
    } else {
      this.sleepingAndRiptideData.put(Integer.valueOf(player), Byte.valueOf(newValue));
    } 
  }
  
  public void onExternalJoinGame(int playerEntityId) {
    super.onExternalJoinGame(playerEntityId);
    PacketWrapper setViewDistance = new PacketWrapper(65, null, getUser());
    setViewDistance.write((Type)Type.VAR_INT, Integer.valueOf(64));
    try {
      setViewDistance.send(Protocol1_14To1_13_2.class, true, true);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public byte getEntityFlags(int player) {
    return zeroIfNull(this.playerEntityFlags.get(Integer.valueOf(player)));
  }
  
  public void setEntityFlags(int player, byte data) {
    this.playerEntityFlags.put(Integer.valueOf(player), Byte.valueOf(data));
  }
  
  public int getLatestTradeWindowId() {
    return this.latestTradeWindowId;
  }
  
  public void setLatestTradeWindowId(int latestTradeWindowId) {
    this.latestTradeWindowId = latestTradeWindowId;
  }
  
  public boolean isForceSendCenterChunk() {
    return this.forceSendCenterChunk;
  }
  
  public void setForceSendCenterChunk(boolean forceSendCenterChunk) {
    this.forceSendCenterChunk = forceSendCenterChunk;
  }
  
  public int getChunkCenterX() {
    return this.chunkCenterX;
  }
  
  public void setChunkCenterX(int chunkCenterX) {
    this.chunkCenterX = chunkCenterX;
  }
  
  public int getChunkCenterZ() {
    return this.chunkCenterZ;
  }
  
  public void setChunkCenterZ(int chunkCenterZ) {
    this.chunkCenterZ = chunkCenterZ;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14to1_13_2\storage\EntityTracker1_14.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */