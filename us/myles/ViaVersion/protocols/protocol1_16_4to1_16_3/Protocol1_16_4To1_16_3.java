package us.myles.ViaVersion.protocols.protocol1_16_4to1_16_3;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;

public class Protocol1_16_4To1_16_3 extends Protocol<ClientboundPackets1_16_2, ClientboundPackets1_16_2, ServerboundPackets1_16_2, ServerboundPackets1_16_2> {
  public Protocol1_16_4To1_16_3() {
    super(ClientboundPackets1_16_2.class, ClientboundPackets1_16_2.class, ServerboundPackets1_16_2.class, ServerboundPackets1_16_2.class);
  }
  
  protected void registerPackets() {
    registerIncoming((ServerboundPacketType)ServerboundPackets1_16_2.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            map(Type.FLAT_VAR_INT_ITEM);
            map(Type.BOOLEAN);
            handler(wrapper -> {
                  int slot = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  wrapper.write((Type)Type.VAR_INT, Integer.valueOf((slot == 40) ? 1 : 0));
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16_4to1_16_3\Protocol1_16_4To1_16_3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */