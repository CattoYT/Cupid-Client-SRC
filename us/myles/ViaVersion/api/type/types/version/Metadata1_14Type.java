package us.myles.ViaVersion.api.type.types.version;

import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_14;
import us.myles.ViaVersion.api.type.types.minecraft.ModernMetaType;

public class Metadata1_14Type extends ModernMetaType {
  protected MetaType getType(int index) {
    return (MetaType)MetaType1_14.byId(index);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\version\Metadata1_14Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */