package us.myles.ViaVersion.api.type.types.minecraft;

import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.type.Type;

public abstract class MetaTypeTemplate extends Type<Metadata> {
  public MetaTypeTemplate() {
    super("Metadata type", Metadata.class);
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)MetaTypeTemplate.class;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\MetaTypeTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */