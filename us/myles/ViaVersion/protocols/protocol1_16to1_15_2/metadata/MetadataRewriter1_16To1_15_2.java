package us.myles.ViaVersion.protocols.protocol1_16to1_15_2.metadata;

import java.util.List;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_15Types;
import us.myles.ViaVersion.api.entities.Entity1_16Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_14;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.rewriters.MetadataRewriter;
import us.myles.ViaVersion.api.type.types.Particle;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.storage.EntityTracker1_16;

public class MetadataRewriter1_16To1_15_2 extends MetadataRewriter {
  public MetadataRewriter1_16To1_15_2(Protocol1_16To1_15_2 protocol) {
    super((Protocol)protocol, EntityTracker1_16.class);
    mapType((EntityType)Entity1_15Types.EntityType.ZOMBIE_PIGMAN, (EntityType)Entity1_16Types.EntityType.ZOMBIFIED_PIGLIN);
    mapTypes((EntityType[])Entity1_15Types.EntityType.values(), Entity1_16Types.EntityType.class);
  }
  
  public void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
    if (metadata.getMetaType() == MetaType1_14.Slot) {
      InventoryPackets.toClient((Item)metadata.getValue());
    } else if (metadata.getMetaType() == MetaType1_14.BlockID) {
      int data = ((Integer)metadata.getValue()).intValue();
      metadata.setValue(Integer.valueOf(this.protocol.getMappingData().getNewBlockStateId(data)));
    } 
    if (type == null)
      return; 
    if (type == Entity1_16Types.EntityType.AREA_EFFECT_CLOUD) {
      if (metadata.getId() == 10)
        rewriteParticle((Particle)metadata.getValue()); 
    } else if (type.isOrHasParent((EntityType)Entity1_16Types.EntityType.ABSTRACT_ARROW)) {
      if (metadata.getId() == 8) {
        metadatas.remove(metadata);
      } else if (metadata.getId() > 8) {
        metadata.setId(metadata.getId() - 1);
      } 
    } 
  }
  
  protected EntityType getTypeFromId(int type) {
    return (EntityType)Entity1_16Types.getTypeFromId(type);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16to1_15_2\metadata\MetadataRewriter1_16To1_15_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */