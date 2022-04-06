package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.types;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.minecraft.Environment;
import us.myles.ViaVersion.api.minecraft.chunks.BaseChunk;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.minecraft.chunks.ChunkSection;
import us.myles.ViaVersion.api.type.PartialType;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.minecraft.BaseChunkType;
import us.myles.ViaVersion.api.type.types.version.Types1_13;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public class Chunk1_13Type extends PartialType<Chunk, ClientWorld> {
  public Chunk1_13Type(ClientWorld param) {
    super(param, Chunk.class);
  }
  
  public Chunk read(ByteBuf input, ClientWorld world) throws Exception {
    int chunkX = input.readInt();
    int chunkZ = input.readInt();
    boolean fullChunk = input.readBoolean();
    int primaryBitmask = Type.VAR_INT.readPrimitive(input);
    ByteBuf data = input.readSlice(Type.VAR_INT.readPrimitive(input));
    ChunkSection[] sections = new ChunkSection[16];
    for (int i = 0; i < 16; i++) {
      if ((primaryBitmask & 1 << i) != 0) {
        ChunkSection section = (ChunkSection)Types1_13.CHUNK_SECTION.read(data);
        sections[i] = section;
        section.readBlockLight(data);
        if (world.getEnvironment() == Environment.NORMAL)
          section.readSkyLight(data); 
      } 
    } 
    int[] biomeData = fullChunk ? new int[256] : null;
    if (fullChunk)
      if (data.readableBytes() >= 1024) {
        for (int j = 0; j < 256; j++)
          biomeData[j] = data.readInt(); 
      } else {
        Via.getPlatform().getLogger().log(Level.WARNING, "Chunk x=" + chunkX + " z=" + chunkZ + " doesn't have biome data!");
      }  
    List<CompoundTag> nbtData = new ArrayList<>(Arrays.asList((Object[])Type.NBT_ARRAY.read(input)));
    if (input.readableBytes() > 0) {
      byte[] array = (byte[])Type.REMAINING_BYTES.read(input);
      if (Via.getManager().isDebug())
        Via.getPlatform().getLogger().warning("Found " + array.length + " more bytes than expected while reading the chunk: " + chunkX + "/" + chunkZ); 
    } 
    return (Chunk)new BaseChunk(chunkX, chunkZ, fullChunk, false, primaryBitmask, sections, biomeData, nbtData);
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
          Types1_13.CHUNK_SECTION.write(buf, section);
          section.writeBlockLight(buf);
          if (section.hasSkyLight())
            section.writeSkyLight(buf); 
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
        output.writeInt(value);  
    Type.NBT_ARRAY.write(output, chunk.getBlockEntities().toArray((Object[])new CompoundTag[0]));
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)BaseChunkType.class;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\types\Chunk1_13Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */