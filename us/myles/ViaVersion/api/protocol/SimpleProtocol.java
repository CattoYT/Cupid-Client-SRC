package us.myles.ViaVersion.api.protocol;

public abstract class SimpleProtocol extends Protocol<SimpleProtocol.DummyPacketTypes, SimpleProtocol.DummyPacketTypes, SimpleProtocol.DummyPacketTypes, SimpleProtocol.DummyPacketTypes> {
  public enum DummyPacketTypes implements ClientboundPacketType, ServerboundPacketType {
  
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\protocol\SimpleProtocol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */