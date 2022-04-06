package us.myles.ViaVersion.api.rewriters;

import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;

public class StatisticsRewriter {
  private final Protocol protocol;
  
  private final IdRewriteFunction entityRewriter;
  
  private final int customStatsCategory = 8;
  
  public StatisticsRewriter(Protocol protocol, @Nullable IdRewriteFunction entityRewriter) {
    this.protocol = protocol;
    this.entityRewriter = entityRewriter;
  }
  
  public void register(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  int newSize = size;
                  for (int i = 0; i < size; i++) {
                    int categoryId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    int statisticId = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    int value = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    if (categoryId == 8 && StatisticsRewriter.this.protocol.getMappingData().getStatisticsMappings() != null) {
                      statisticId = StatisticsRewriter.this.protocol.getMappingData().getStatisticsMappings().getNewId(statisticId);
                      if (statisticId == -1) {
                        newSize--;
                        continue;
                      } 
                    } else {
                      RegistryType type = StatisticsRewriter.this.getRegistryTypeForStatistic(categoryId);
                      IdRewriteFunction statisticsRewriter;
                      if (type != null && (statisticsRewriter = StatisticsRewriter.this.getRewriter(type)) != null)
                        statisticId = statisticsRewriter.rewrite(statisticId); 
                    } 
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(categoryId));
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(statisticId));
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(value));
                    continue;
                  } 
                  if (newSize != size)
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(newSize)); 
                });
          }
        });
  }
  
  @Nullable
  protected IdRewriteFunction getRewriter(RegistryType type) {
    switch (type) {
      case BLOCK:
        return (this.protocol.getMappingData().getBlockMappings() != null) ? (id -> this.protocol.getMappingData().getNewBlockId(id)) : null;
      case ITEM:
        return (this.protocol.getMappingData().getItemMappings() != null) ? (id -> this.protocol.getMappingData().getNewItemId(id)) : null;
      case ENTITY:
        return this.entityRewriter;
    } 
    throw new IllegalArgumentException("Unknown registry type in statistics packet: " + type);
  }
  
  @Nullable
  public RegistryType getRegistryTypeForStatistic(int statisticsId) {
    switch (statisticsId) {
      case 0:
        return RegistryType.BLOCK;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        return RegistryType.ITEM;
      case 6:
      case 7:
        return RegistryType.ENTITY;
    } 
    return null;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\rewriters\StatisticsRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */