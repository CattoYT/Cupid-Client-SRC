package us.myles.ViaVersion.api.type.types.version;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.minecraft.AbstractMetaListType;

public class MetadataList1_8Type extends AbstractMetaListType {
  protected Type<Metadata> getType() {
    return Types1_8.METADATA;
  }
  
  protected void writeEnd(Type<Metadata> type, ByteBuf buffer) throws Exception {
    buffer.writeByte(127);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\version\MetadataList1_8Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */