package us.myles.ViaVersion.protocols.base;

import java.util.List;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Pair;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.platform.providers.ViaProviders;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ProtocolPipeline;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.api.protocol.SimpleProtocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.packets.State;

public class BaseProtocol extends SimpleProtocol {
  protected void registerPackets() {
    registerIncoming(State.HANDSHAKE, 0, 0, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int protocolVersion = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  wrapper.passthrough(Type.STRING);
                  wrapper.passthrough(Type.UNSIGNED_SHORT);
                  int state = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  ProtocolInfo info = wrapper.user().getProtocolInfo();
                  info.setProtocolVersion(protocolVersion);
                  if (Via.getManager().getProviders().get(VersionProvider.class) == null) {
                    wrapper.user().setActive(false);
                    return;
                  } 
                  int serverProtocol = ((VersionProvider)Via.getManager().getProviders().get(VersionProvider.class)).getServerProtocol(wrapper.user());
                  info.setServerProtocolVersion(serverProtocol);
                  List<Pair<Integer, Protocol>> protocols = null;
                  if (info.getProtocolVersion() >= serverProtocol || Via.getPlatform().isOldClientsAllowed())
                    protocols = ProtocolRegistry.getProtocolPath(info.getProtocolVersion(), serverProtocol); 
                  ProtocolPipeline pipeline = wrapper.user().getProtocolInfo().getPipeline();
                  if (protocols != null) {
                    for (Pair<Integer, Protocol> prot : protocols) {
                      pipeline.add((Protocol)prot.getValue());
                      ProtocolRegistry.completeMappingDataLoading(((Protocol)prot.getValue()).getClass());
                    } 
                    ProtocolVersion protocol = ProtocolVersion.getProtocol(serverProtocol);
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(protocol.getOriginalVersion()));
                  } 
                  pipeline.add(ProtocolRegistry.getBaseProtocol(serverProtocol));
                  if (state == 1)
                    info.setState(State.STATUS); 
                  if (state == 2)
                    info.setState(State.LOGIN); 
                });
          }
        });
  }
  
  public void init(UserConnection userConnection) {}
  
  protected void register(ViaProviders providers) {
    providers.register(VersionProvider.class, new VersionProvider());
  }
  
  public void transform(Direction direction, State state, PacketWrapper packetWrapper) throws Exception {
    super.transform(direction, state, packetWrapper);
    if (direction == Direction.INCOMING && state == State.HANDSHAKE)
      if (packetWrapper.getId() != 0)
        packetWrapper.user().setActive(false);  
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\base\BaseProtocol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */