package us.myles.ViaVersion.api.type.types.version;

import java.util.List;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.type.Type;

public class Types1_12 {
  public static final Type<List<Metadata>> METADATA_LIST = (Type<List<Metadata>>)new MetadataList1_12Type();
  
  public static final Type<Metadata> METADATA = (Type<Metadata>)new Metadata1_12Type();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\version\Types1_12.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */