package us.myles.ViaVersion.protocols.protocol1_13_1to1_13.metadata;

import java.util.List;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_13Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_13;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.rewriters.MetadataRewriter;
import us.myles.ViaVersion.api.type.types.Particle;
import us.myles.ViaVersion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import us.myles.ViaVersion.protocols.protocol1_13_1to1_13.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage.EntityTracker1_13;

public class MetadataRewriter1_13_1To1_13 extends MetadataRewriter {
  public MetadataRewriter1_13_1To1_13(Protocol1_13_1To1_13 protocol) {
    super((Protocol)protocol, EntityTracker1_13.class);
  }
  
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) {
    if (metadata.getMetaType() == MetaType1_13.Slot) {
      InventoryPackets.toClient((Item)metadata.getValue());
    } else if (metadata.getMetaType() == MetaType1_13.BlockID) {
      int data = ((Integer)metadata.getValue()).intValue();
      metadata.setValue(Integer.valueOf(this.protocol.getMappingData().getNewBlockStateId(data)));
    } 
    if (type == null)
      return; 
    if (type.isOrHasParent((EntityType)Entity1_13Types.EntityType.MINECART_ABSTRACT) && metadata.getId() == 9) {
      int data = ((Integer)metadata.getValue()).intValue();
      metadata.setValue(Integer.valueOf(this.protocol.getMappingData().getNewBlockStateId(data)));
    } else if (type.isOrHasParent((EntityType)Entity1_13Types.EntityType.ABSTRACT_ARROW) && metadata.getId() >= 7) {
      metadata.setId(metadata.getId() + 1);
    } else if (type.is((EntityType)Entity1_13Types.EntityType.AREA_EFFECT_CLOUD) && metadata.getId() == 10) {
      rewriteParticle((Particle)metadata.getValue());
    } 
  }
  
  protected EntityType getTypeFromId(int type) {
    return (EntityType)Entity1_13Types.getTypeFromId(type, false);
  }
  
  protected EntityType getObjectTypeFromId(int type) {
    return (EntityType)Entity1_13Types.getTypeFromId(type, true);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13_1to1_13\metadata\MetadataRewriter1_13_1To1_13.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */