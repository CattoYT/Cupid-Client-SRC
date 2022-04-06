package us.myles.ViaVersion.api.remapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Pair;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.exception.InformativeException;

public abstract class PacketRemapper {
  private final List<Pair<ValueReader, ValueWriter>> valueRemappers = new ArrayList<>();
  
  public PacketRemapper() {
    registerMap();
  }
  
  public void map(Type<?> type) {
    TypeRemapper<?> remapper = new TypeRemapper(type);
    map(remapper, remapper);
  }
  
  public void map(Type<?> oldType, Type<?> newType) {
    map(new TypeRemapper(oldType), new TypeRemapper(newType));
  }
  
  public <T1, T2> void map(Type<T1> oldType, Type<T2> newType, final Function<T1, T2> transformer) {
    map(new TypeRemapper(oldType), new ValueTransformer<T1, T2>(newType) {
          public T2 transform(PacketWrapper wrapper, T1 inputValue) throws Exception {
            return transformer.apply(inputValue);
          }
        });
  }
  
  public <T1, T2> void map(ValueTransformer<T1, T2> transformer) {
    if (transformer.getInputType() == null)
      throw new IllegalArgumentException("Use map(Type<T1>, ValueTransformer<T1, T2>) for value transformers without specified input type!"); 
    map(transformer.getInputType(), transformer);
  }
  
  public <T1, T2> void map(Type<T1> oldType, ValueTransformer<T1, T2> transformer) {
    map(new TypeRemapper<>(oldType), transformer);
  }
  
  public <T> void map(ValueReader<T> inputReader, ValueWriter<T> outputWriter) {
    this.valueRemappers.add(new Pair(inputReader, outputWriter));
  }
  
  public void create(ValueCreator creator) {
    map(new TypeRemapper(Type.NOTHING), creator);
  }
  
  public void handler(PacketHandler handler) {
    map(new TypeRemapper(Type.NOTHING), handler);
  }
  
  public abstract void registerMap();
  
  public void remap(PacketWrapper packetWrapper) throws Exception {
    try {
      for (Pair<ValueReader, ValueWriter> valueRemapper : this.valueRemappers) {
        Object object = ((ValueReader)valueRemapper.getKey()).read(packetWrapper);
        ((ValueWriter<Object>)valueRemapper.getValue()).write(packetWrapper, object);
      } 
    } catch (InformativeException e) {
      e.addSource(getClass());
      throw e;
    } catch (CancelException e) {
      throw e;
    } catch (Exception e) {
      InformativeException ex = new InformativeException(e);
      ex.addSource(getClass());
      throw ex;
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\remapper\PacketRemapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */