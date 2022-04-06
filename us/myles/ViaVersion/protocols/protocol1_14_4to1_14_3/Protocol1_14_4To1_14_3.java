package us.myles.ViaVersion.protocols.protocol1_14_4to1_14_3;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;

public class Protocol1_14_4To1_14_3 extends Protocol<ClientboundPackets1_14, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14> {
  public Protocol1_14_4To1_14_3() {
    super(ClientboundPackets1_14.class, ClientboundPackets1_14.class, null, null);
  }
  
  protected void registerPackets() {
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_14.TRADE_LIST, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.passthrough((Type)Type.VAR_INT);
                    int size = ((Short)wrapper.passthrough(Type.UNSIGNED_BYTE)).shortValue();
                    for (int i = 0; i < size; i++) {
                      wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                      wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                      if (((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue())
                        wrapper.passthrough(Type.FLAT_VAR_INT_ITEM); 
                      wrapper.passthrough(Type.BOOLEAN);
                      wrapper.passthrough(Type.INT);
                      wrapper.passthrough(Type.INT);
                      wrapper.passthrough(Type.INT);
                      wrapper.passthrough(Type.INT);
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.write(Type.INT, Integer.valueOf(0));
                    } 
                  }
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14_4to1_14_3\Protocol1_14_4To1_14_3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */