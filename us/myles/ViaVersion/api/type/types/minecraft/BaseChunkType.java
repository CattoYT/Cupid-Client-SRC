package us.myles.ViaVersion.api.type.types.minecraft;

import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.type.Type;

public abstract class BaseChunkType extends Type<Chunk> {
  public BaseChunkType() {
    super(Chunk.class);
  }
  
  public BaseChunkType(String typeName) {
    super(typeName, Chunk.class);
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)BaseChunkType.class;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\BaseChunkType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */