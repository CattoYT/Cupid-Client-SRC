package us.myles.ViaVersion.protocols.protocol1_12_2to1_12_1;

import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import us.myles.ViaVersion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;

public class Protocol1_12_2To1_12_1 extends Protocol {
  public Protocol1_12_2To1_12_1() {
    super(ClientboundPackets1_12_1.class, ClientboundPackets1_12_1.class, ServerboundPackets1_12_1.class, ServerboundPackets1_12_1.class);
  }
  
  protected void registerPackets() {
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_12_1.KEEP_ALIVE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT, Type.LONG);
          }
        });
    registerIncoming((ServerboundPacketType)ServerboundPackets1_12_1.KEEP_ALIVE, new PacketRemapper() {
          public void registerMap() {
            map(Type.LONG, (Type)Type.VAR_INT);
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_12_2to1_12_1\Protocol1_12_2To1_12_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */