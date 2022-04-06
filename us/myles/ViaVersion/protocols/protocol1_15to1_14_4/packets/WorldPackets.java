package us.myles.ViaVersion.protocols.protocol1_15to1_14_4.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.minecraft.chunks.ChunkSection;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.BlockRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.types.Chunk1_14Type;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.types.Chunk1_15Type;

public class WorldPackets {
  public static void register(final Protocol1_15To1_14_4 protocol) {
    BlockRewriter blockRewriter = new BlockRewriter((Protocol)protocol, Type.POSITION1_14);
    blockRewriter.registerBlockAction((ClientboundPacketType)ClientboundPackets1_14.BLOCK_ACTION);
    blockRewriter.registerBlockChange((ClientboundPacketType)ClientboundPackets1_14.BLOCK_CHANGE);
    blockRewriter.registerMultiBlockChange((ClientboundPacketType)ClientboundPackets1_14.MULTI_BLOCK_CHANGE);
    blockRewriter.registerAcknowledgePlayerDigging((ClientboundPacketType)ClientboundPackets1_14.ACKNOWLEDGE_PLAYER_DIGGING);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_14.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Chunk chunk = (Chunk)wrapper.read((Type)new Chunk1_14Type());
                    wrapper.write((Type)new Chunk1_15Type(), chunk);
                    if (chunk.isFullChunk()) {
                      int[] biomeData = chunk.getBiomeData();
                      int[] newBiomeData = new int[1024];
                      if (biomeData != null) {
                        int i;
                        for (i = 0; i < 4; i++) {
                          for (int j = 0; j < 4; j++) {
                            int x = (j << 2) + 2;
                            int z = (i << 2) + 2;
                            int oldIndex = z << 4 | x;
                            newBiomeData[i << 2 | j] = biomeData[oldIndex];
                          } 
                        } 
                        for (i = 1; i < 64; i++)
                          System.arraycopy(newBiomeData, 0, newBiomeData, i * 16, 16); 
                      } 
                      chunk.setBiomeData(newBiomeData);
                    } 
                    for (int s = 0; s < 16; s++) {
                      ChunkSection section = chunk.getSections()[s];
                      if (section != null)
                        for (int i = 0; i < section.getPaletteSize(); i++) {
                          int old = section.getPaletteEntry(i);
                          int newId = protocol.getMappingData().getNewBlockStateId(old);
                          section.setPaletteEntry(i, newId);
                        }  
                    } 
                  }
                });
          }
        });
    blockRewriter.registerEffect((ClientboundPacketType)ClientboundPackets1_14.EFFECT, 1010, 2001);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_14.SPAWN_PARTICLE, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.BOOLEAN);
            map((Type)Type.FLOAT, Type.DOUBLE);
            map((Type)Type.FLOAT, Type.DOUBLE);
            map((Type)Type.FLOAT, Type.DOUBLE);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map(Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                    if (id == 3 || id == 23) {
                      int data = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                      wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(protocol.getMappingData().getNewBlockStateId(data)));
                    } else if (id == 32) {
                      InventoryPackets.toClient((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                    } 
                  }
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_15to1_14_4\packets\WorldPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */