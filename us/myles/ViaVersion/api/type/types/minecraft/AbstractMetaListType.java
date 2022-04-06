package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.type.Type;

public abstract class AbstractMetaListType extends MetaListTypeTemplate {
  public List<Metadata> read(ByteBuf buffer) throws Exception {
    Type<Metadata> type = getType();
    List<Metadata> list = new ArrayList<>();
    while (true) {
      Metadata meta = (Metadata)type.read(buffer);
      if (meta != null)
        list.add(meta); 
      if (meta == null)
        return list; 
    } 
  }
  
  public void write(ByteBuf buffer, List<Metadata> object) throws Exception {
    Type<Metadata> type = getType();
    for (Metadata metadata : object)
      type.write(buffer, metadata); 
    writeEnd(type, buffer);
  }
  
  protected abstract Type<Metadata> getType();
  
  protected abstract void writeEnd(Type<Metadata> paramType, ByteBuf paramByteBuf) throws Exception;
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\AbstractMetaListType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */