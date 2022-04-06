package us.myles.ViaVersion.protocols.protocol1_14to1_13_2.packets;

import java.util.Arrays;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.entities.Entity1_14Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.BlockFace;
import us.myles.ViaVersion.api.minecraft.chunks.Chunk;
import us.myles.ViaVersion.api.minecraft.chunks.ChunkSection;
import us.myles.ViaVersion.api.minecraft.chunks.NibbleArray;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.remapper.ValueCreator;
import us.myles.ViaVersion.api.rewriters.BlockRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.types.Chunk1_14Type;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import us.myles.ViaVersion.util.CompactArrayUtil;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class WorldPackets {
  public static final int SERVERSIDE_VIEW_DISTANCE = 64;
  
  private static final byte[] FULL_LIGHT = new byte[2048];
  
  public static int air;
  
  public static int voidAir;
  
  public static int caveAir;
  
  static {
    Arrays.fill(FULL_LIGHT, (byte)-1);
  }
  
  public static void register(final Protocol1_14To1_13_2 protocol) {
    BlockRewriter blockRewriter = new BlockRewriter((Protocol)protocol, null);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.BLOCK_BREAK_ANIMATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.POSITION, Type.POSITION1_14);
            map(Type.BYTE);
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.BLOCK_ACTION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
            map(Type.UNSIGNED_BYTE);
            map(Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(protocol.getMappingData().getNewBlockId(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue())));
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(protocol.getMappingData().getNewBlockStateId(id)));
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.SERVER_DIFFICULTY, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            create(new ValueCreator() {
                  public void write(PacketWrapper wrapper) throws Exception {
                    wrapper.write(Type.BOOLEAN, Boolean.valueOf(false));
                  }
                });
          }
        });
    blockRewriter.registerMultiBlockChange((ClientboundPacketType)ClientboundPackets1_13.MULTI_BLOCK_CHANGE);
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.EXPLOSION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    for (int i = 0; i < 3; i++) {
                      float coord = ((Float)wrapper.get((Type)Type.FLOAT, i)).floatValue();
                      if (coord < 0.0F) {
                        coord = (int)coord;
                        wrapper.set((Type)Type.FLOAT, i, Float.valueOf(coord));
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    Chunk chunk = (Chunk)wrapper.read((Type)new Chunk1_13Type(clientWorld));
                    wrapper.write((Type)new Chunk1_14Type(), chunk);
                    int[] motionBlocking = new int[256];
                    int[] worldSurface = new int[256];
                    for (int s = 0; s < 16; s++) {
                      ChunkSection section = chunk.getSections()[s];
                      if (section != null) {
                        boolean hasBlock = false;
                        for (int j = 0; j < section.getPaletteSize(); j++) {
                          int old = section.getPaletteEntry(j);
                          int newId = protocol.getMappingData().getNewBlockStateId(old);
                          if (!hasBlock && newId != WorldPackets.air && newId != WorldPackets.voidAir && newId != WorldPackets.caveAir)
                            hasBlock = true; 
                          section.setPaletteEntry(j, newId);
                        } 
                        if (!hasBlock) {
                          section.setNonAirBlocksCount(0);
                        } else {
                          int nonAirBlockCount = 0;
                          for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 16; y++) {
                              for (int z = 0; z < 16; z++) {
                                int id = section.getFlatBlock(x, y, z);
                                if (id != WorldPackets.air && id != WorldPackets.voidAir && id != WorldPackets.caveAir) {
                                  nonAirBlockCount++;
                                  worldSurface[x + z * 16] = y + s * 16 + 1;
                                } 
                                if (protocol.getMappingData().getMotionBlocking().contains(id))
                                  motionBlocking[x + z * 16] = y + s * 16 + 1; 
                                if (Via.getConfig().isNonFullBlockLightFix() && protocol.getMappingData().getNonFullBlocks().contains(id))
                                  WorldPackets.setNonFullLight(chunk, section, s, x, y, z); 
                              } 
                            } 
                          } 
                          section.setNonAirBlocksCount(nonAirBlockCount);
                        } 
                      } 
                    } 
                    CompoundTag heightMap = new CompoundTag("");
                    heightMap.put((Tag)new LongArrayTag("MOTION_BLOCKING", WorldPackets.encodeHeightMap(motionBlocking)));
                    heightMap.put((Tag)new LongArrayTag("WORLD_SURFACE", WorldPackets.encodeHeightMap(worldSurface)));
                    chunk.setHeightMap(heightMap);
                    PacketWrapper lightPacket = wrapper.create(36);
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(chunk.getX()));
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(chunk.getZ()));
                    int skyLightMask = chunk.isFullChunk() ? 262143 : 0;
                    int blockLightMask = 0;
                    for (int i = 0; i < (chunk.getSections()).length; i++) {
                      ChunkSection sec = chunk.getSections()[i];
                      if (sec != null) {
                        if (!chunk.isFullChunk() && sec.hasSkyLight())
                          skyLightMask |= 1 << i + 1; 
                        blockLightMask |= 1 << i + 1;
                      } 
                    } 
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(skyLightMask));
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(blockLightMask));
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(0));
                    lightPacket.write((Type)Type.VAR_INT, Integer.valueOf(0));
                    if (chunk.isFullChunk())
                      lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, WorldPackets.FULL_LIGHT); 
                    for (ChunkSection section : chunk.getSections()) {
                      if (section == null || !section.hasSkyLight()) {
                        if (chunk.isFullChunk())
                          lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, WorldPackets.FULL_LIGHT); 
                      } else {
                        lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, section.getSkyLight());
                      } 
                    } 
                    if (chunk.isFullChunk())
                      lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, WorldPackets.FULL_LIGHT); 
                    for (ChunkSection section : chunk.getSections()) {
                      if (section != null)
                        lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, section.getBlockLight()); 
                    } 
                    EntityTracker1_14 entityTracker = (EntityTracker1_14)wrapper.user().get(EntityTracker1_14.class);
                    int diffX = Math.abs(entityTracker.getChunkCenterX() - chunk.getX());
                    int diffZ = Math.abs(entityTracker.getChunkCenterZ() - chunk.getZ());
                    if (entityTracker.isForceSendCenterChunk() || diffX >= 64 || diffZ >= 64) {
                      PacketWrapper fakePosLook = wrapper.create(64);
                      fakePosLook.write((Type)Type.VAR_INT, Integer.valueOf(chunk.getX()));
                      fakePosLook.write((Type)Type.VAR_INT, Integer.valueOf(chunk.getZ()));
                      fakePosLook.send(Protocol1_14To1_13_2.class, true, true);
                      entityTracker.setChunkCenterX(chunk.getX());
                      entityTracker.setChunkCenterZ(chunk.getZ());
                    } 
                    lightPacket.send(Protocol1_14To1_13_2.class, true, true);
                  }
                  
                  private Byte[] fromPrimitiveArray(byte[] bytes) {
                    Byte[] newArray = new Byte[bytes.length];
                    for (int i = 0; i < bytes.length; i++)
                      newArray[i] = Byte.valueOf(bytes[i]); 
                    return newArray;
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.POSITION, Type.POSITION1_14);
            map(Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                    int data = ((Integer)wrapper.get(Type.INT, 1)).intValue();
                    if (id == 1010) {
                      wrapper.set(Type.INT, 1, Integer.valueOf(protocol.getMappingData().getNewItemId(data)));
                    } else if (id == 2001) {
                      wrapper.set(Type.INT, 1, Integer.valueOf(protocol.getMappingData().getNewBlockStateId(data)));
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.UNSIGNED_BYTE);
            map(Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get(Type.INT, 1)).intValue();
                    clientChunks.setEnvironment(dimensionId);
                    int entityId = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                    Entity1_14Types.EntityType entType = Entity1_14Types.EntityType.PLAYER;
                    EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().get(EntityTracker1_14.class);
                    tracker.addEntity(entityId, (EntityType)entType);
                    tracker.setClientEntityId(entityId);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short difficulty = ((Short)wrapper.read(Type.UNSIGNED_BYTE)).shortValue();
                    PacketWrapper difficultyPacket = wrapper.create(13);
                    difficultyPacket.write(Type.UNSIGNED_BYTE, Short.valueOf(difficulty));
                    difficultyPacket.write(Type.BOOLEAN, Boolean.valueOf(false));
                    difficultyPacket.send(protocol.getClass());
                    wrapper.passthrough(Type.UNSIGNED_BYTE);
                    wrapper.passthrough(Type.STRING);
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(64));
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.MAP_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.BYTE);
            map(Type.BOOLEAN);
            create(new ValueCreator() {
                  public void write(PacketWrapper wrapper) throws Exception {
                    wrapper.write(Type.BOOLEAN, Boolean.valueOf(false));
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                    clientWorld.setEnvironment(dimensionId);
                    EntityTracker1_14 entityTracker = (EntityTracker1_14)wrapper.user().get(EntityTracker1_14.class);
                    entityTracker.setForceSendCenterChunk(true);
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    short difficulty = ((Short)wrapper.read(Type.UNSIGNED_BYTE)).shortValue();
                    PacketWrapper difficultyPacket = wrapper.create(13);
                    difficultyPacket.write(Type.UNSIGNED_BYTE, Short.valueOf(difficulty));
                    difficultyPacket.write(Type.BOOLEAN, Boolean.valueOf(false));
                    difficultyPacket.send(protocol.getClass());
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.SPAWN_POSITION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
          }
        });
  }
  
  private static long[] encodeHeightMap(int[] heightMap) {
    return CompactArrayUtil.createCompactArray(9, heightMap.length, i -> heightMap[i]);
  }
  
  private static void setNonFullLight(Chunk chunk, ChunkSection section, int ySection, int x, int y, int z) {
    int skyLight = 0;
    int blockLight = 0;
    for (BlockFace blockFace : BlockFace.values()) {
      NibbleArray skyLightArray = section.getSkyLightNibbleArray();
      NibbleArray blockLightArray = section.getBlockLightNibbleArray();
      int neighbourX = x + blockFace.getModX();
      int neighbourY = y + blockFace.getModY();
      int neighbourZ = z + blockFace.getModZ();
      if (blockFace.getModX() != 0) {
        if (neighbourX == 16 || neighbourX == -1)
          continue; 
      } else if (blockFace.getModY() != 0) {
        if (neighbourY == 16 || neighbourY == -1) {
          if (neighbourY == 16) {
            ySection++;
            neighbourY = 0;
          } else {
            ySection--;
            neighbourY = 15;
          } 
          if (ySection == 16 || ySection == -1)
            continue; 
          ChunkSection newSection = chunk.getSections()[ySection];
          if (newSection == null)
            continue; 
          skyLightArray = newSection.getSkyLightNibbleArray();
          blockLightArray = newSection.getBlockLightNibbleArray();
        } 
      } else if (blockFace.getModZ() != 0) {
        if (neighbourZ == 16 || neighbourZ == -1)
          continue; 
      } 
      if (blockLightArray != null && blockLight != 15) {
        int neighbourBlockLight = blockLightArray.get(neighbourX, neighbourY, neighbourZ);
        if (neighbourBlockLight == 15) {
          blockLight = 14;
        } else if (neighbourBlockLight > blockLight) {
          blockLight = neighbourBlockLight - 1;
        } 
      } 
      if (skyLightArray != null && skyLight != 15) {
        int neighbourSkyLight = skyLightArray.get(neighbourX, neighbourY, neighbourZ);
        if (neighbourSkyLight == 15) {
          if (blockFace.getModY() == 1) {
            skyLight = 15;
          } else {
            skyLight = 14;
          } 
        } else if (neighbourSkyLight > skyLight) {
          skyLight = neighbourSkyLight - 1;
        } 
      } 
      continue;
    } 
    if (skyLight != 0) {
      if (!section.hasSkyLight()) {
        byte[] newSkyLight = new byte[2028];
        section.setSkyLight(newSkyLight);
      } 
      section.getSkyLightNibbleArray().set(x, y, z, skyLight);
    } 
    if (blockLight != 0)
      section.getBlockLightNibbleArray().set(x, y, z, blockLight); 
  }
  
  private static long getChunkIndex(int x, int z) {
    return (x & 0x3FFFFFFL) << 38L | z & 0x3FFFFFFL;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14to1_13_2\packets\WorldPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */