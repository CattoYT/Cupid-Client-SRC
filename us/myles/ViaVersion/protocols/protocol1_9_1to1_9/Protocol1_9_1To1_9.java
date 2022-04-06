package us.myles.ViaVersion.protocols.protocol1_9_1to1_9;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ServerboundPackets1_9;

public class Protocol1_9_1To1_9 extends Protocol<ClientboundPackets1_9, ClientboundPackets1_9, ServerboundPackets1_9, ServerboundPackets1_9> {
  public Protocol1_9_1To1_9() {
    super(ClientboundPackets1_9.class, ClientboundPackets1_9.class, ServerboundPackets1_9.class, ServerboundPackets1_9.class);
  }
  
  protected void registerPackets() {
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.UNSIGNED_BYTE);
            map(Type.BYTE, Type.INT);
            map(Type.UNSIGNED_BYTE);
            map(Type.UNSIGNED_BYTE);
            map(Type.STRING);
            map(Type.BOOLEAN);
          }
        });
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_9.SOUND, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int sound = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (sound >= 415)
                      wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(sound + 1)); 
                  }
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9_1to1_9\Protocol1_9_1To1_9.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */