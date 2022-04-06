package us.myles.ViaVersion.protocols.protocol1_14to1_13_2.types;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.minecraft.chunks.BaseChunk;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.minecraft.chunks.ChunkSection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.minecraft.BaseChunkType;
import us.myles.ViaVersion.api.type.types.version.Types1_13;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public class Chunk1_14Type extends Type<Chunk> {
  public Chunk1_14Type() {
    super(Chunk.class);
  }
  
  public Chunk read(ByteBuf input) throws Exception {
    int chunkX = input.readInt();
    int chunkZ = input.readInt();
    boolean fullChunk = input.readBoolean();
    int primaryBitmask = Type.VAR_INT.readPrimitive(input);
    CompoundTag heightMap = (CompoundTag)Type.NBT.read(input);
    Type.VAR_INT.readPrimitive(input);
    ChunkSection[] sections = new ChunkSection[16];
    for (int i = 0; i < 16; i++) {
      if ((primaryBitmask & 1 << i) != 0) {
        short nonAirBlocksCount = input.readShort();
        ChunkSection section = (ChunkSection)Types1_13.CHUNK_SECTION.read(input);
        section.setNonAirBlocksCount(nonAirBlocksCount);
        sections[i] = section;
      } 
    } 
    int[] biomeData = fullChunk ? new int[256] : null;
    if (fullChunk)
      for (int j = 0; j < 256; j++)
        biomeData[j] = input.readInt();  
    List<CompoundTag> nbtData = new ArrayList<>(Arrays.asList((Object[])Type.NBT_ARRAY.read(input)));
    if (input.readableBytes() > 0) {
      byte[] array = (byte[])Type.REMAINING_BYTES.read(input);
      if (Via.getManager().isDebug())
        Via.getPlatform().getLogger().warning("Found " + array.length + " more bytes than expected while reading the chunk: " + chunkX + "/" + chunkZ); 
    } 
    return (Chunk)new BaseChunk(chunkX, chunkZ, fullChunk, false, primaryBitmask, sections, biomeData, heightMap, nbtData);
  }
  
  public void write(ByteBuf output, Chunk chunk) throws Exception {
    output.writeInt(chunk.getX());
    output.writeInt(chunk.getZ());
    output.writeBoolean(chunk.isFullChunk());
    Type.VAR_INT.writePrimitive(output, chunk.getBitmask());
    Type.NBT.write(output, chunk.getHeightMap());
    ByteBuf buf = output.alloc().buffer();
    try {
      for (int i = 0; i < 16; i++) {
        ChunkSection section = chunk.getSections()[i];
        if (section != null) {
          buf.writeShort(section.getNonAirBlocksCount());
          Types1_13.CHUNK_SECTION.write(buf, section);
        } 
      } 
      buf.readerIndex(0);
      Type.VAR_INT.writePrimitive(output, buf.readableBytes() + (chunk.isBiomeData() ? 1024 : 0));
      output.writeBytes(buf);
    } finally {
      buf.release();
    } 
    if (chunk.isBiomeData())
      for (int value : chunk.getBiomeData())
        output.writeInt(value & 0xFF);  
    Type.NBT_ARRAY.write(output, chunk.getBlockEntities().toArray((Object[])new CompoundTag[0]));
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)BaseChunkType.class;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14to1_13_2\types\Chunk1_14Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */