package us.myles.ViaVersion.api.remapper;

import us.myles.ViaVersion.api.PacketWrapper;

@FunctionalInterface
public interface ValueWriter<T> {
  void write(PacketWrapper paramPacketWrapper, T paramT) throws Exception;
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\remapper\ValueWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */