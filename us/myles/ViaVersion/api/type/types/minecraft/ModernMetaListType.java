package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.type.Type;

public abstract class ModernMetaListType extends AbstractMetaListType {
  protected void writeEnd(Type<Metadata> type, ByteBuf buffer) throws Exception {
    type.write(buffer, null);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\ModernMetaListType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */