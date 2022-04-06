package us.myles.ViaVersion.api.remapper;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.type.Type;

public class TypeRemapper<T> implements ValueReader<T>, ValueWriter<T> {
  private final Type<T> type;
  
  public TypeRemapper(Type<T> type) {
    this.type = type;
  }
  
  public T read(PacketWrapper wrapper) throws Exception {
    return (T)wrapper.read(this.type);
  }
  
  public void write(PacketWrapper output, T inputValue) {
    output.write(this.type, inputValue);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\remapper\TypeRemapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */