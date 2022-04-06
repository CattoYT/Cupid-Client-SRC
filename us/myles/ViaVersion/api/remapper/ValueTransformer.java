package us.myles.ViaVersion.api.remapper;

import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.exception.InformativeException;

public abstract class ValueTransformer<T1, T2> implements ValueWriter<T1> {
  private final Type<T1> inputType;
  
  private final Type<T2> outputType;
  
  public ValueTransformer(@Nullable Type<T1> inputType, Type<T2> outputType) {
    this.inputType = inputType;
    this.outputType = outputType;
  }
  
  public ValueTransformer(Type<T2> outputType) {
    this(null, outputType);
  }
  
  public abstract T2 transform(PacketWrapper paramPacketWrapper, T1 paramT1) throws Exception;
  
  public void write(PacketWrapper writer, T1 inputValue) throws Exception {
    try {
      writer.write(this.outputType, transform(writer, inputValue));
    } catch (InformativeException e) {
      e.addSource(getClass());
      throw e;
    } 
  }
  
  @Nullable
  public Type<T1> getInputType() {
    return this.inputType;
  }
  
  public Type<T2> getOutputType() {
    return this.outputType;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\remapper\ValueTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */