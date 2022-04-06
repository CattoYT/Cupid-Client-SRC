package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.BlockChangeRecord;
import us.myles.ViaVersion.api.minecraft.BlockChangeRecord1_16_2;
import us.myles.ViaVersion.api.type.Type;

public class VarLongBlockChangeRecordType extends Type<BlockChangeRecord> {
  public VarLongBlockChangeRecordType() {
    super(BlockChangeRecord.class);
  }
  
  public BlockChangeRecord read(ByteBuf buffer) throws Exception {
    long data = Type.VAR_LONG.readPrimitive(buffer);
    short position = (short)(int)(data & 0xFFFL);
    return (BlockChangeRecord)new BlockChangeRecord1_16_2(position >>> 8 & 0xF, position & 0xF, position >>> 4 & 0xF, (int)(data >>> 12L));
  }
  
  public void write(ByteBuf buffer, BlockChangeRecord object) throws Exception {
    short position = (short)(object.getSectionX() << 8 | object.getSectionZ() << 4 | object.getSectionY());
    Type.VAR_LONG.writePrimitive(buffer, object.getBlockId() << 12L | position);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\VarLongBlockChangeRecordType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */