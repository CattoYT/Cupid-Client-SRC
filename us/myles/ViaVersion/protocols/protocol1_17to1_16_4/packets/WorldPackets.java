package us.myles.ViaVersion.protocols.protocol1_17to1_16_4.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.minecraft.chunks.ChunkSection;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.BlockRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.types.Chunk1_16_2Type;
import us.myles.ViaVersion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import us.myles.ViaVersion.protocols.protocol1_17to1_16_4.storage.BiomeStorage;
import us.myles.ViaVersion.protocols.protocol1_17to1_16_4.types.Chunk1_17Type;

public class WorldPackets {
  public static void register(final Protocol1_17To1_16_4 protocol) {
    BlockRewriter blockRewriter = new BlockRewriter((Protocol)protocol, Type.POSITION1_14);
    blockRewriter.registerBlockAction((ClientboundPacketType)ClientboundPackets1_16_2.BLOCK_ACTION);
    blockRewriter.registerBlockChange((ClientboundPacketType)ClientboundPackets1_16_2.BLOCK_CHANGE);
    blockRewriter.registerVarLongMultiBlockChange((ClientboundPacketType)ClientboundPackets1_16_2.MULTI_BLOCK_CHANGE);
    blockRewriter.registerAcknowledgePlayerDigging((ClientboundPacketType)ClientboundPackets1_16_2.ACKNOWLEDGE_PLAYER_DIGGING);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_16_2.UPDATE_LIGHT, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  wrapper.passthrough((Type)Type.VAR_INT);
                  wrapper.passthrough(Type.BOOLEAN);
                  wrapper.write((Type)Type.VAR_LONG, Long.valueOf(((Integer)wrapper.read((Type)Type.VAR_INT)).longValue()));
                  wrapper.write((Type)Type.VAR_LONG, Long.valueOf(((Integer)wrapper.read((Type)Type.VAR_INT)).longValue()));
                  wrapper.write((Type)Type.VAR_LONG, Long.valueOf(((Integer)wrapper.read((Type)Type.VAR_INT)).longValue()));
                  wrapper.write((Type)Type.VAR_LONG, Long.valueOf(((Integer)wrapper.read((Type)Type.VAR_INT)).longValue()));
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_16_2.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  Chunk chunk = (Chunk)wrapper.read((Type)new Chunk1_16_2Type());
                  wrapper.write((Type)new Chunk1_17Type(), chunk);
                  BiomeStorage biomeStorage = (BiomeStorage)wrapper.user().get(BiomeStorage.class);
                  if (chunk.isFullChunk()) {
                    biomeStorage.setBiomes(chunk.getX(), chunk.getZ(), chunk.getBiomeData());
                  } else {
                    int[] biomes = biomeStorage.getBiomes(chunk.getX(), chunk.getZ());
                    if (biomes != null) {
                      chunk.setBiomeData(biomes);
                    } else {
                      Via.getPlatform().getLogger().warning("Biome data not found for chunk at " + chunk.getX() + ", " + chunk.getZ());
                      chunk.setBiomeData(new int[1024]);
                    } 
                  } 
                  for (int s = 0; s < 16; s++) {
                    ChunkSection section = chunk.getSections()[s];
                    if (section != null)
                      for (int i = 0; i < section.getPaletteSize(); i++) {
                        int old = section.getPaletteEntry(i);
                        section.setPaletteEntry(i, protocol.getMappingData().getNewBlockStateId(old));
                      }  
                  } 
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_16_2.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.BOOLEAN);
            map(Type.UNSIGNED_BYTE);
            map(Type.BYTE);
            map(Type.STRING_ARRAY);
            map(Type.NBT);
            map(Type.NBT);
            handler(wrapper -> {
                  String world = (String)wrapper.passthrough(Type.STRING);
                  ((BiomeStorage)wrapper.user().get(BiomeStorage.class)).setWorld(world);
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_16_2.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough(Type.NBT);
                  String world = (String)wrapper.passthrough(Type.STRING);
                  BiomeStorage biomeStorage = (BiomeStorage)wrapper.user().get(BiomeStorage.class);
                  if (!world.equals(biomeStorage.getWorld()))
                    biomeStorage.clearBiomes(); 
                  biomeStorage.setWorld(world);
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_16_2.UNLOAD_CHUNK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int x = ((Integer)wrapper.passthrough(Type.INT)).intValue();
                  int z = ((Integer)wrapper.passthrough(Type.INT)).intValue();
                  ((BiomeStorage)wrapper.user().get(BiomeStorage.class)).clearBiomes(x, z);
                });
          }
        });
    blockRewriter.registerEffect((ClientboundPacketType)ClientboundPackets1_16_2.EFFECT, 1010, 2001);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_17to1_16_4\packets\WorldPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */