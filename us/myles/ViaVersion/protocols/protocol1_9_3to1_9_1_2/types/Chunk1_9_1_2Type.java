package us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.types;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.BitSet;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.minecraft.Environment;
import us.myles.ViaVersion.api.minecraft.chunks.BaseChunk;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.minecraft.chunks.ChunkSection;
import us.myles.ViaVersion.api.type.PartialType;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.minecraft.BaseChunkType;
import us.myles.ViaVersion.api.type.types.version.Types1_9;
import us.myles.ViaVersion.protocols.protocol1_10to1_9_3.Protocol1_10To1_9_3_4;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

public class Chunk1_9_1_2Type extends PartialType<Chunk, ClientWorld> {
  public Chunk1_9_1_2Type(ClientWorld clientWorld) {
    super(clientWorld, Chunk.class);
  }
  
  public Chunk read(ByteBuf input, ClientWorld world) throws Exception {
    boolean replacePistons = (world.getUser().getProtocolInfo().getPipeline().contains(Protocol1_10To1_9_3_4.class) && Via.getConfig().isReplacePistons());
    int replacementId = Via.getConfig().getPistonReplacementId();
    int chunkX = input.readInt();
    int chunkZ = input.readInt();
    boolean groundUp = input.readBoolean();
    int primaryBitmask = Type.VAR_INT.readPrimitive(input);
    Type.VAR_INT.readPrimitive(input);
    BitSet usedSections = new BitSet(16);
    ChunkSection[] sections = new ChunkSection[16];
    int i;
    for (i = 0; i < 16; i++) {
      if ((primaryBitmask & 1 << i) != 0)
        usedSections.set(i); 
    } 
    for (i = 0; i < 16; i++) {
      if (usedSections.get(i)) {
        ChunkSection section = (ChunkSection)Types1_9.CHUNK_SECTION.read(input);
        sections[i] = section;
        section.readBlockLight(input);
        if (world.getEnvironment() == Environment.NORMAL)
          section.readSkyLight(input); 
        if (replacePistons)
          section.replacePaletteEntry(36, replacementId); 
      } 
    } 
    int[] biomeData = groundUp ? new int[256] : null;
    if (groundUp)
      for (int j = 0; j < 256; j++)
        biomeData[j] = input.readByte() & 0xFF;  
    return (Chunk)new BaseChunk(chunkX, chunkZ, groundUp, false, primaryBitmask, sections, biomeData, new ArrayList());
  }
  
  public void write(ByteBuf output, ClientWorld world, Chunk chunk) throws Exception {
    output.writeInt(chunk.getX());
    output.writeInt(chunk.getZ());
    output.writeBoolean(chunk.isFullChunk());
    Type.VAR_INT.writePrimitive(output, chunk.getBitmask());
    ByteBuf buf = output.alloc().buffer();
    try {
      for (int i = 0; i < 16; i++) {
        ChunkSection section = chunk.getSections()[i];
        if (section != null) {
          Types1_9.CHUNK_SECTION.write(buf, section);
          section.writeBlockLight(buf);
          if (section.hasSkyLight())
            section.writeSkyLight(buf); 
        } 
      } 
      buf.readerIndex(0);
      Type.VAR_INT.writePrimitive(output, buf.readableBytes() + (chunk.isBiomeData() ? 256 : 0));
      output.writeBytes(buf);
    } finally {
      buf.release();
    } 
    if (chunk.isBiomeData())
      for (int biome : chunk.getBiomeData())
        output.writeByte((byte)biome);  
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)BaseChunkType.class;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9_3to1_9_1_2\types\Chunk1_9_1_2Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */