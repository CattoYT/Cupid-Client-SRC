package us.myles.ViaVersion.protocols.protocol1_15to1_14_4.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.entities.Entity1_15Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.storage.EntityTracker1_15;

public class PlayerPackets {
  public static void register(Protocol protocol) {
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_14.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            create(wrapper -> wrapper.write(Type.LONG, Long.valueOf(0L)));
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_14.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.UNSIGNED_BYTE);
            map(Type.INT);
            handler(wrapper -> {
                  EntityTracker1_15 tracker = (EntityTracker1_15)wrapper.user().get(EntityTracker1_15.class);
                  int entityId = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                  tracker.addEntity(entityId, (EntityType)Entity1_15Types.EntityType.PLAYER);
                });
            create(wrapper -> wrapper.write(Type.LONG, Long.valueOf(0L)));
            map(Type.UNSIGNED_BYTE);
            map(Type.STRING);
            map((Type)Type.VAR_INT);
            map(Type.BOOLEAN);
            create(wrapper -> wrapper.write(Type.BOOLEAN, Boolean.valueOf(!Via.getConfig().is1_15InstantRespawn())));
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_15to1_14_4\packets\PlayerPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */