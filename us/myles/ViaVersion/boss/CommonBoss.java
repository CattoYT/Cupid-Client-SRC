package us.myles.ViaVersion.boss;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossFlag;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;

public abstract class CommonBoss<T> extends BossBar<T> {
  private final UUID uuid;
  
  private final Set<UserConnection> connections;
  
  private final Set<BossFlag> flags;
  
  private String title;
  
  private float health;
  
  private BossColor color;
  
  private BossStyle style;
  
  private boolean visible;
  
  public CommonBoss(String title, float health, BossColor color, BossStyle style) {
    Preconditions.checkNotNull(title, "Title cannot be null");
    Preconditions.checkArgument((health >= 0.0F && health <= 1.0F), "Health must be between 0 and 1");
    this.uuid = UUID.randomUUID();
    this.title = title;
    this.health = health;
    this.color = (color == null) ? BossColor.PURPLE : color;
    this.style = (style == null) ? BossStyle.SOLID : style;
    this.connections = Collections.newSetFromMap(new WeakHashMap<>());
    this.flags = new HashSet<>();
    this.visible = true;
  }
  
  public BossBar setTitle(String title) {
    Preconditions.checkNotNull(title);
    this.title = title;
    sendPacket(UpdateAction.UPDATE_TITLE);
    return this;
  }
  
  public BossBar setHealth(float health) {
    Preconditions.checkArgument((health >= 0.0F && health <= 1.0F), "Health must be between 0 and 1");
    this.health = health;
    sendPacket(UpdateAction.UPDATE_HEALTH);
    return this;
  }
  
  public BossColor getColor() {
    return this.color;
  }
  
  public BossBar setColor(BossColor color) {
    Preconditions.checkNotNull(color);
    this.color = color;
    sendPacket(UpdateAction.UPDATE_STYLE);
    return this;
  }
  
  public BossBar setStyle(BossStyle style) {
    Preconditions.checkNotNull(style);
    this.style = style;
    sendPacket(UpdateAction.UPDATE_STYLE);
    return this;
  }
  
  public BossBar addPlayer(UUID player) {
    return addConnection(Via.getManager().getConnection(player));
  }
  
  public BossBar addConnection(UserConnection conn) {
    if (this.connections.add(conn) && this.visible)
      sendPacketConnection(conn, getPacket(UpdateAction.ADD, conn)); 
    return this;
  }
  
  public BossBar removePlayer(UUID uuid) {
    return removeConnection(Via.getManager().getConnection(uuid));
  }
  
  public BossBar removeConnection(UserConnection conn) {
    if (this.connections.remove(conn))
      sendPacketConnection(conn, getPacket(UpdateAction.REMOVE, conn)); 
    return this;
  }
  
  public BossBar addFlag(BossFlag flag) {
    Preconditions.checkNotNull(flag);
    if (!hasFlag(flag))
      this.flags.add(flag); 
    sendPacket(UpdateAction.UPDATE_FLAGS);
    return this;
  }
  
  public BossBar removeFlag(BossFlag flag) {
    Preconditions.checkNotNull(flag);
    if (hasFlag(flag))
      this.flags.remove(flag); 
    sendPacket(UpdateAction.UPDATE_FLAGS);
    return this;
  }
  
  public boolean hasFlag(BossFlag flag) {
    Preconditions.checkNotNull(flag);
    return this.flags.contains(flag);
  }
  
  public Set<UUID> getPlayers() {
    return (Set<UUID>)this.connections.stream().map(conn -> Via.getManager().getConnectedClientId(conn)).filter(Objects::nonNull)
      .collect(Collectors.toSet());
  }
  
  public Set<UserConnection> getConnections() {
    return Collections.unmodifiableSet(this.connections);
  }
  
  public BossBar show() {
    setVisible(true);
    return this;
  }
  
  public BossBar hide() {
    setVisible(false);
    return this;
  }
  
  public boolean isVisible() {
    return this.visible;
  }
  
  private void setVisible(boolean value) {
    if (this.visible != value) {
      this.visible = value;
      sendPacket(value ? UpdateAction.ADD : UpdateAction.REMOVE);
    } 
  }
  
  public UUID getId() {
    return this.uuid;
  }
  
  public UUID getUuid() {
    return this.uuid;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public float getHealth() {
    return this.health;
  }
  
  public BossStyle getStyle() {
    return this.style;
  }
  
  public Set<BossFlag> getFlags() {
    return this.flags;
  }
  
  private void sendPacket(UpdateAction action) {
    for (UserConnection conn : new ArrayList(this.connections)) {
      PacketWrapper wrapper = getPacket(action, conn);
      sendPacketConnection(conn, wrapper);
    } 
  }
  
  private void sendPacketConnection(UserConnection conn, PacketWrapper wrapper) {
    if (conn.getProtocolInfo() == null || !conn.getProtocolInfo().getPipeline().contains(Protocol1_9To1_8.class)) {
      this.connections.remove(conn);
      return;
    } 
    try {
      wrapper.send(Protocol1_9To1_8.class);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  private PacketWrapper getPacket(UpdateAction action, UserConnection connection) {
    try {
      PacketWrapper wrapper = new PacketWrapper(12, null, connection);
      wrapper.write(Type.UUID, this.uuid);
      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(action.getId()));
      switch (action) {
        case ADD:
          Protocol1_9To1_8.FIX_JSON.write(wrapper, this.title);
          wrapper.write((Type)Type.FLOAT, Float.valueOf(this.health));
          wrapper.write((Type)Type.VAR_INT, Integer.valueOf(this.color.getId()));
          wrapper.write((Type)Type.VAR_INT, Integer.valueOf(this.style.getId()));
          wrapper.write(Type.BYTE, Byte.valueOf((byte)flagToBytes()));
          break;
        case UPDATE_HEALTH:
          wrapper.write((Type)Type.FLOAT, Float.valueOf(this.health));
          break;
        case UPDATE_TITLE:
          Protocol1_9To1_8.FIX_JSON.write(wrapper, this.title);
          break;
        case UPDATE_STYLE:
          wrapper.write((Type)Type.VAR_INT, Integer.valueOf(this.color.getId()));
          wrapper.write((Type)Type.VAR_INT, Integer.valueOf(this.style.getId()));
          break;
        case UPDATE_FLAGS:
          wrapper.write(Type.BYTE, Byte.valueOf((byte)flagToBytes()));
          break;
      } 
      return wrapper;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  private int flagToBytes() {
    int bitmask = 0;
    for (BossFlag flag : this.flags)
      bitmask |= flag.getId(); 
    return bitmask;
  }
  
  private enum UpdateAction {
    ADD(0),
    REMOVE(1),
    UPDATE_HEALTH(2),
    UPDATE_TITLE(3),
    UPDATE_STYLE(4),
    UPDATE_FLAGS(5);
    
    private final int id;
    
    UpdateAction(int id) {
      this.id = id;
    }
    
    public int getId() {
      return this.id;
    }
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\boss\CommonBoss.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */