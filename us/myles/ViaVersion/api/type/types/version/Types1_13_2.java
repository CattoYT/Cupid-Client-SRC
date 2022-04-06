package us.myles.ViaVersion.api.type.types.version;

import java.util.List;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.Particle;
import us.myles.ViaVersion.protocols.protocol1_13_2to1_13_1.types.Particle1_13_2Type;

public class Types1_13_2 {
  public static final Type<List<Metadata>> METADATA_LIST = (Type<List<Metadata>>)new MetadataList1_13_2Type();
  
  public static final Type<Metadata> METADATA = (Type<Metadata>)new Metadata1_13_2Type();
  
  public static Type<Particle> PARTICLE = (Type<Particle>)new Particle1_13_2Type();
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\version\Types1_13_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */