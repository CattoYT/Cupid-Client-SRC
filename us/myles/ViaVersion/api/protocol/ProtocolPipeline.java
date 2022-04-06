package us.myles.ViaVersion.api.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.platform.ViaPlatform;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;

public class ProtocolPipeline extends SimpleProtocol {
  private List<Protocol> protocolList;
  
  private UserConnection userConnection;
  
  public ProtocolPipeline(UserConnection userConnection) {
    init(userConnection);
  }
  
  protected void registerPackets() {
    this.protocolList = new CopyOnWriteArrayList<>();
    this.protocolList.add(ProtocolRegistry.BASE_PROTOCOL);
  }
  
  public void init(UserConnection userConnection) {
    this.userConnection = userConnection;
    ProtocolInfo protocolInfo = new ProtocolInfo(userConnection);
    protocolInfo.setPipeline(this);
    userConnection.setProtocolInfo(protocolInfo);
    for (Protocol protocol : this.protocolList)
      protocol.init(userConnection); 
  }
  
  public void add(Protocol protocol) {
    if (this.protocolList != null) {
      this.protocolList.add(protocol);
      protocol.init(this.userConnection);
      List<Protocol> toMove = new ArrayList<>();
      for (Protocol p : this.protocolList) {
        if (ProtocolRegistry.isBaseProtocol(p))
          toMove.add(p); 
      } 
      this.protocolList.removeAll(toMove);
      this.protocolList.addAll(toMove);
    } else {
      throw new NullPointerException("Tried to add protocol too early");
    } 
  }
  
  public void transform(Direction direction, State state, PacketWrapper packetWrapper) throws Exception {
    int originalID = packetWrapper.getId();
    packetWrapper.apply(direction, state, 0, this.protocolList, (direction == Direction.OUTGOING));
    super.transform(direction, state, packetWrapper);
    if (Via.getManager().isDebug())
      logPacket(direction, state, packetWrapper, originalID); 
  }
  
  private void logPacket(Direction direction, State state, PacketWrapper packetWrapper, int originalID) {
    int clientProtocol = this.userConnection.getProtocolInfo().getProtocolVersion();
    ViaPlatform platform = Via.getPlatform();
    String actualUsername = packetWrapper.user().getProtocolInfo().getUsername();
    String username = (actualUsername != null) ? (actualUsername + " ") : "";
    platform.getLogger().log(Level.INFO, "{0}{1} {2}: {3} (0x{4}) -> {5} (0x{6}) [{7}] {8}", new Object[] { username, direction, state, 
          
          Integer.valueOf(originalID), 
          Integer.toHexString(originalID), 
          Integer.valueOf(packetWrapper.getId()), 
          Integer.toHexString(packetWrapper.getId()), 
          Integer.toString(clientProtocol), packetWrapper });
  }
  
  public boolean contains(Class<? extends Protocol> pipeClass) {
    for (Protocol protocol : this.protocolList) {
      if (protocol.getClass().equals(pipeClass))
        return true; 
    } 
    return false;
  }
  
  public <P extends Protocol> P getProtocol(Class<P> pipeClass) {
    for (Protocol protocol : this.protocolList) {
      if (protocol.getClass() == pipeClass)
        return (P)protocol; 
    } 
    return null;
  }
  
  public boolean filter(Object o, List list) throws Exception {
    for (Protocol protocol : this.protocolList) {
      if (protocol.isFiltered(o.getClass())) {
        protocol.filterPacket(this.userConnection, o, list);
        return true;
      } 
    } 
    return false;
  }
  
  public List<Protocol> pipes() {
    return this.protocolList;
  }
  
  public void cleanPipes() {
    pipes().clear();
    registerPackets();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\protocol\ProtocolPipeline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */