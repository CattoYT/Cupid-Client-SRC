package us.myles.ViaVersion.protocols.base;

import java.util.UUID;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.type.Type;

public class BaseProtocol1_16 extends BaseProtocol1_7 {
  protected UUID passthroughLoginUUID(PacketWrapper wrapper) throws Exception {
    return (UUID)wrapper.passthrough(Type.UUID_INT_ARRAY);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\base\BaseProtocol1_16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */