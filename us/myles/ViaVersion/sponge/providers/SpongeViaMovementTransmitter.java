package us.myles.ViaVersion.sponge.providers;

import java.lang.reflect.Field;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;

public class SpongeViaMovementTransmitter extends MovementTransmitterProvider {
  private Object idlePacket;
  
  private Object idlePacket2;
  
  public SpongeViaMovementTransmitter() {
    Class<?> idlePacketClass;
    try {
      idlePacketClass = Class.forName("net.minecraft.network.play.client.C03PacketPlayer");
    } catch (ClassNotFoundException e) {
      return;
    } 
    try {
      this.idlePacket = idlePacketClass.newInstance();
      this.idlePacket2 = idlePacketClass.newInstance();
      Field flying = idlePacketClass.getDeclaredField("field_149474_g");
      flying.setAccessible(true);
      flying.set(this.idlePacket2, Boolean.valueOf(true));
    } catch (NoSuchFieldException|InstantiationException|IllegalArgumentException|IllegalAccessException e) {
      throw new RuntimeException("Couldn't make player idle packet, help!", e);
    } 
  }
  
  public Object getFlyingPacket() {
    if (this.idlePacket == null)
      throw new NullPointerException("Could not locate flying packet"); 
    return this.idlePacket2;
  }
  
  public Object getGroundPacket() {
    if (this.idlePacket == null)
      throw new NullPointerException("Could not locate flying packet"); 
    return this.idlePacket;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\providers\SpongeViaMovementTransmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */