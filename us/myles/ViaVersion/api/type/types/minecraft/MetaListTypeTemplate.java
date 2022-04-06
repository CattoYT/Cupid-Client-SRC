package us.myles.ViaVersion.api.type.types.minecraft;

import java.util.List;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.type.Type;

public abstract class MetaListTypeTemplate extends Type<List<Metadata>> {
  protected MetaListTypeTemplate() {
    super("MetaData List", List.class);
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)MetaListTypeTemplate.class;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\MetaListTypeTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */