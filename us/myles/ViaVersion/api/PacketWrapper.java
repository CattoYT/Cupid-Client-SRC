package us.myles.ViaVersion.api;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.ValueCreator;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.TypeConverter;
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.exception.InformativeException;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.util.PipelineUtil;

public class PacketWrapper {
  public static final int PASSTHROUGH_ID = 1000;
  
  private static final Protocol[] PROTOCOL_ARRAY = new Protocol[0];
  
  private final ByteBuf inputBuffer;
  
  private final UserConnection userConnection;
  
  private boolean send = true;
  
  private int id = -1;
  
  private final LinkedList<Pair<Type, Object>> readableObjects = new LinkedList<>();
  
  private final List<Pair<Type, Object>> packetValues = new ArrayList<>();
  
  public PacketWrapper(int packetID, ByteBuf inputBuffer, UserConnection userConnection) {
    this.id = packetID;
    this.inputBuffer = inputBuffer;
    this.userConnection = userConnection;
  }
  
  public <T> T get(Type<T> type, int index) throws Exception {
    int currentIndex = 0;
    for (Pair<Type, Object> packetValue : this.packetValues) {
      if (packetValue.getKey() == type) {
        if (currentIndex == index)
          return (T)packetValue.getValue(); 
        currentIndex++;
      } 
    } 
    Exception e = new ArrayIndexOutOfBoundsException("Could not find type " + type.getTypeName() + " at " + index);
    throw (new InformativeException(e)).set("Type", type.getTypeName()).set("Index", Integer.valueOf(index)).set("Packet ID", Integer.valueOf(getId())).set("Data", this.packetValues);
  }
  
  public boolean is(Type type, int index) {
    int currentIndex = 0;
    for (Pair<Type, Object> packetValue : this.packetValues) {
      if (packetValue.getKey() == type) {
        if (currentIndex == index)
          return true; 
        currentIndex++;
      } 
    } 
    return false;
  }
  
  public boolean isReadable(Type type, int index) {
    int currentIndex = 0;
    for (Pair<Type, Object> packetValue : this.readableObjects) {
      if (((Type)packetValue.getKey()).getBaseClass() == type.getBaseClass()) {
        if (currentIndex == index)
          return true; 
        currentIndex++;
      } 
    } 
    return false;
  }
  
  public <T> void set(Type<T> type, int index, T value) throws Exception {
    int currentIndex = 0;
    for (Pair<Type, Object> packetValue : this.packetValues) {
      if (packetValue.getKey() == type) {
        if (currentIndex == index) {
          packetValue.setValue(value);
          return;
        } 
        currentIndex++;
      } 
    } 
    Exception e = new ArrayIndexOutOfBoundsException("Could not find type " + type.getTypeName() + " at " + index);
    throw (new InformativeException(e)).set("Type", type.getTypeName()).set("Index", Integer.valueOf(index)).set("Packet ID", Integer.valueOf(getId()));
  }
  
  public <T> T read(Type<T> type) throws Exception {
    if (type == Type.NOTHING)
      return null; 
    if (this.readableObjects.isEmpty()) {
      Preconditions.checkNotNull(this.inputBuffer, "This packet does not have an input buffer.");
      try {
        return (T)type.read(this.inputBuffer);
      } catch (Exception exception) {
        throw (new InformativeException(exception)).set("Type", type.getTypeName()).set("Packet ID", Integer.valueOf(getId())).set("Data", this.packetValues);
      } 
    } 
    Pair<Type, Object> read = this.readableObjects.poll();
    Type rtype = read.getKey();
    if (rtype.equals(type) || (type.getBaseClass().equals(rtype.getBaseClass()) && type.getOutputClass().equals(rtype.getOutputClass())))
      return (T)read.getValue(); 
    if (rtype == Type.NOTHING)
      return read(type); 
    Exception e = new IOException("Unable to read type " + type.getTypeName() + ", found " + ((Type)read.getKey()).getTypeName());
    throw (new InformativeException(e)).set("Type", type.getTypeName()).set("Packet ID", Integer.valueOf(getId())).set("Data", this.packetValues);
  }
  
  public <T> void write(Type<T> type, T value) {
    if (value != null && 
      !type.getOutputClass().isAssignableFrom(value.getClass()))
      if (type instanceof TypeConverter) {
        value = (T)((TypeConverter)type).from(value);
      } else {
        Via.getPlatform().getLogger().warning("Possible type mismatch: " + value.getClass().getName() + " -> " + type.getOutputClass());
      }  
    this.packetValues.add(new Pair<>(type, value));
  }
  
  public <T> T passthrough(Type<T> type) throws Exception {
    T value = read(type);
    write(type, value);
    return value;
  }
  
  public void passthroughAll() throws Exception {
    this.packetValues.addAll(this.readableObjects);
    this.readableObjects.clear();
    if (this.inputBuffer.readableBytes() > 0)
      passthrough(Type.REMAINING_BYTES); 
  }
  
  public void writeToBuffer(ByteBuf buffer) throws Exception {
    if (this.id != -1)
      Type.VAR_INT.writePrimitive(buffer, this.id); 
    if (!this.readableObjects.isEmpty()) {
      this.packetValues.addAll(this.readableObjects);
      this.readableObjects.clear();
    } 
    int index = 0;
    for (Pair<Type, Object> packetValue : this.packetValues) {
      try {
        Object value = packetValue.getValue();
        if (value != null && 
          !((Type)packetValue.getKey()).getOutputClass().isAssignableFrom(value.getClass()))
          if (packetValue.getKey() instanceof TypeConverter) {
            value = ((TypeConverter)packetValue.getKey()).from(value);
          } else {
            Via.getPlatform().getLogger().warning("Possible type mismatch: " + value.getClass().getName() + " -> " + ((Type)packetValue.getKey()).getOutputClass());
          }  
        ((Type)packetValue.getKey()).write(buffer, value);
      } catch (Exception e) {
        throw (new InformativeException(e)).set("Index", Integer.valueOf(index)).set("Type", ((Type)packetValue.getKey()).getTypeName()).set("Packet ID", Integer.valueOf(getId())).set("Data", this.packetValues);
      } 
      index++;
    } 
    writeRemaining(buffer);
  }
  
  public void clearInputBuffer() {
    if (this.inputBuffer != null)
      this.inputBuffer.clear(); 
    this.readableObjects.clear();
  }
  
  public void clearPacket() {
    clearInputBuffer();
    this.packetValues.clear();
  }
  
  private void writeRemaining(ByteBuf output) {
    if (this.inputBuffer != null)
      output.writeBytes(this.inputBuffer, this.inputBuffer.readableBytes()); 
  }
  
  public void send(Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline) throws Exception {
    send(packetProtocol, skipCurrentPipeline, false);
  }
  
  public void send(Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline, boolean currentThread) throws Exception {
    if (!isCancelled())
      try {
        ByteBuf output = constructPacket(packetProtocol, skipCurrentPipeline, Direction.OUTGOING);
        user().sendRawPacket(output, currentThread);
      } catch (Exception e) {
        if (!PipelineUtil.containsCause(e, CancelException.class))
          throw e; 
      }  
  }
  
  private ByteBuf constructPacket(Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline, Direction direction) throws Exception {
    Protocol[] protocols = (Protocol[])user().getProtocolInfo().getPipeline().pipes().toArray((Object[])PROTOCOL_ARRAY);
    boolean reverse = (direction == Direction.OUTGOING);
    int index = -1;
    for (int i = 0; i < protocols.length; i++) {
      if (protocols[i].getClass() == packetProtocol) {
        index = i;
        break;
      } 
    } 
    if (index == -1)
      throw new NoSuchElementException(packetProtocol.getCanonicalName()); 
    if (skipCurrentPipeline)
      index = reverse ? (index - 1) : (index + 1); 
    resetReader();
    apply(direction, user().getProtocolInfo().getState(), index, protocols, reverse);
    ByteBuf output = (this.inputBuffer == null) ? user().getChannel().alloc().buffer() : this.inputBuffer.alloc().buffer();
    writeToBuffer(output);
    return output;
  }
  
  public void send(Class<? extends Protocol> packetProtocol) throws Exception {
    send(packetProtocol, true);
  }
  
  public ChannelFuture sendFuture(Class<? extends Protocol> packetProtocol) throws Exception {
    if (!isCancelled()) {
      ByteBuf output = constructPacket(packetProtocol, true, Direction.OUTGOING);
      return user().sendRawPacketFuture(output);
    } 
    return user().getChannel().newFailedFuture(new Exception("Cancelled packet"));
  }
  
  @Deprecated
  public void send() throws Exception {
    if (!isCancelled()) {
      ByteBuf output = (this.inputBuffer == null) ? user().getChannel().alloc().buffer() : this.inputBuffer.alloc().buffer();
      writeToBuffer(output);
      user().sendRawPacket(output);
    } 
  }
  
  public PacketWrapper create(int packetID) {
    return new PacketWrapper(packetID, null, user());
  }
  
  public PacketWrapper create(int packetID, ValueCreator init) throws Exception {
    PacketWrapper wrapper = create(packetID);
    init.write(wrapper);
    return wrapper;
  }
  
  public PacketWrapper apply(Direction direction, State state, int index, List<Protocol> pipeline, boolean reverse) throws Exception {
    Protocol[] array = pipeline.<Protocol>toArray(PROTOCOL_ARRAY);
    return apply(direction, state, reverse ? (array.length - 1) : index, array, reverse);
  }
  
  public PacketWrapper apply(Direction direction, State state, int index, List<Protocol> pipeline) throws Exception {
    return apply(direction, state, index, pipeline.<Protocol>toArray(PROTOCOL_ARRAY), false);
  }
  
  private PacketWrapper apply(Direction direction, State state, int index, Protocol[] pipeline, boolean reverse) throws Exception {
    if (reverse) {
      for (int i = index; i >= 0; i--) {
        pipeline[i].transform(direction, state, this);
        resetReader();
      } 
    } else {
      for (int i = index; i < pipeline.length; i++) {
        pipeline[i].transform(direction, state, this);
        resetReader();
      } 
    } 
    return this;
  }
  
  public void cancel() {
    this.send = false;
  }
  
  public boolean isCancelled() {
    return !this.send;
  }
  
  public UserConnection user() {
    return this.userConnection;
  }
  
  public void resetReader() {
    this.packetValues.addAll(this.readableObjects);
    this.readableObjects.clear();
    this.readableObjects.addAll(this.packetValues);
    this.packetValues.clear();
  }
  
  @Deprecated
  public void sendToServer() throws Exception {
    if (!isCancelled()) {
      ByteBuf output = (this.inputBuffer == null) ? user().getChannel().alloc().buffer() : this.inputBuffer.alloc().buffer();
      writeToBuffer(output);
      user().sendRawPacketToServer(output, true);
    } 
  }
  
  public void sendToServer(Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline, boolean currentThread) throws Exception {
    if (!isCancelled())
      try {
        ByteBuf output = constructPacket(packetProtocol, skipCurrentPipeline, Direction.INCOMING);
        user().sendRawPacketToServer(output, currentThread);
      } catch (Exception e) {
        if (!PipelineUtil.containsCause(e, CancelException.class))
          throw e; 
      }  
  }
  
  public void sendToServer(Class<? extends Protocol> packetProtocol, boolean skipCurrentPipeline) throws Exception {
    sendToServer(packetProtocol, skipCurrentPipeline, false);
  }
  
  public void sendToServer(Class<? extends Protocol> packetProtocol) throws Exception {
    sendToServer(packetProtocol, true);
  }
  
  public int getId() {
    return this.id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public String toString() {
    return "PacketWrapper{packetValues=" + this.packetValues + ", readableObjects=" + this.readableObjects + ", id=" + this.id + '}';
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\PacketWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */