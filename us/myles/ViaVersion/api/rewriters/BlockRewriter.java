package us.myles.ViaVersion.api.rewriters;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.BlockChangeRecord;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;

public class BlockRewriter {
  private final Protocol protocol;
  
  private final Type<Position> positionType;
  
  public BlockRewriter(Protocol protocol, Type<Position> positionType) {
    this.protocol = protocol;
    this.positionType = positionType;
  }
  
  public void registerBlockAction(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map(BlockRewriter.this.positionType);
            map(Type.UNSIGNED_BYTE);
            map(Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int id = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  int mappedId = BlockRewriter.this.protocol.getMappingData().getNewBlockId(id);
                  if (mappedId == -1) {
                    wrapper.cancel();
                    return;
                  } 
                  wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(mappedId));
                });
          }
        });
  }
  
  public void registerBlockChange(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map(BlockRewriter.this.positionType);
            map((Type)Type.VAR_INT);
            handler(wrapper -> wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(BlockRewriter.this.protocol.getMappingData().getNewBlockStateId(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue()))));
          }
        });
  }
  
  public void registerMultiBlockChange(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.INT);
            handler(wrapper -> {
                  for (BlockChangeRecord record : (BlockChangeRecord[])wrapper.passthrough(Type.BLOCK_CHANGE_RECORD_ARRAY))
                    record.setBlockId(BlockRewriter.this.protocol.getMappingData().getNewBlockStateId(record.getBlockId())); 
                });
          }
        });
  }
  
  public void registerVarLongMultiBlockChange(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.LONG);
            map(Type.BOOLEAN);
            handler(wrapper -> {
                  for (BlockChangeRecord record : (BlockChangeRecord[])wrapper.passthrough(Type.VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY))
                    record.setBlockId(BlockRewriter.this.protocol.getMappingData().getNewBlockStateId(record.getBlockId())); 
                });
          }
        });
  }
  
  public void registerAcknowledgePlayerDigging(ClientboundPacketType packetType) {
    registerBlockChange(packetType);
  }
  
  public void registerEffect(ClientboundPacketType packetType, final int playRecordId, final int blockBreakId) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(BlockRewriter.this.positionType);
            map(Type.INT);
            handler(wrapper -> {
                  int id = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                  int data = ((Integer)wrapper.get(Type.INT, 1)).intValue();
                  if (id == playRecordId) {
                    wrapper.set(Type.INT, 1, Integer.valueOf(BlockRewriter.this.protocol.getMappingData().getNewItemId(data)));
                  } else if (id == blockBreakId) {
                    wrapper.set(Type.INT, 1, Integer.valueOf(BlockRewriter.this.protocol.getMappingData().getNewBlockStateId(data)));
                  } 
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\rewriters\BlockRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */