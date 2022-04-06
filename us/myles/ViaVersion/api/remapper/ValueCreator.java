package us.myles.ViaVersion.api.remapper;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.exception.InformativeException;

@FunctionalInterface
public interface ValueCreator extends ValueWriter {
  void write(PacketWrapper paramPacketWrapper) throws Exception;
  
  default void write(PacketWrapper writer, Object inputValue) throws Exception {
    try {
      write(writer);
    } catch (InformativeException e) {
      e.addSource(getClass());
      throw e;
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\remapper\ValueCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */