package us.myles.ViaVersion.api.remapper;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.exception.InformativeException;

@FunctionalInterface
public interface PacketHandler extends ValueWriter {
  void handle(PacketWrapper paramPacketWrapper) throws Exception;
  
  default void write(PacketWrapper writer, Object inputValue) throws Exception {
    try {
      handle(writer);
    } catch (InformativeException e) {
      e.addSource(getClass());
      throw e;
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\remapper\PacketHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */