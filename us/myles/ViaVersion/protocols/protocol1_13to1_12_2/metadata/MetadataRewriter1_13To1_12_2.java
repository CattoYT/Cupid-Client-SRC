package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.metadata;

import java.util.List;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_13Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_13;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.rewriters.MetadataRewriter;
import us.myles.ViaVersion.api.type.types.Particle;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ChatRewriter;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data.EntityTypeRewriter;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data.ParticleRewriter;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.packets.WorldPackets;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage.EntityTracker1_13;

public class MetadataRewriter1_13To1_12_2 extends MetadataRewriter {
  public MetadataRewriter1_13To1_12_2(Protocol1_13To1_12_2 protocol) {
    super((Protocol)protocol, EntityTracker1_13.class);
  }
  
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
    if (metadata.getMetaType().getTypeID() > 4) {
      metadata.setMetaType((MetaType)MetaType1_13.byId(metadata.getMetaType().getTypeID() + 1));
    } else {
      metadata.setMetaType((MetaType)MetaType1_13.byId(metadata.getMetaType().getTypeID()));
    } 
    if (metadata.getId() == 2) {
      metadata.setMetaType((MetaType)MetaType1_13.OptChat);
      if (metadata.getValue() != null && !((String)metadata.getValue()).isEmpty()) {
        metadata.setValue(ChatRewriter.legacyTextToJson((String)metadata.getValue()));
      } else {
        metadata.setValue(null);
      } 
    } 
    if (type == Entity1_13Types.EntityType.ENDERMAN && metadata.getId() == 12) {
      int stateId = ((Integer)metadata.getValue()).intValue();
      int id = stateId & 0xFFF;
      int data = stateId >> 12 & 0xF;
      metadata.setValue(Integer.valueOf(id << 4 | data & 0xF));
    } 
    if (metadata.getMetaType() == MetaType1_13.Slot) {
      metadata.setMetaType((MetaType)MetaType1_13.Slot);
      InventoryPackets.toClient((Item)metadata.getValue());
    } else if (metadata.getMetaType() == MetaType1_13.BlockID) {
      metadata.setValue(Integer.valueOf(WorldPackets.toNewId(((Integer)metadata.getValue()).intValue())));
    } 
    if (type == null)
      return; 
    if (type == Entity1_13Types.EntityType.WOLF && metadata.getId() == 17)
      metadata.setValue(Integer.valueOf(15 - ((Integer)metadata.getValue()).intValue())); 
    if (type.isOrHasParent((EntityType)Entity1_13Types.EntityType.ZOMBIE) && 
      metadata.getId() > 14)
      metadata.setId(metadata.getId() + 1); 
    if (type.isOrHasParent((EntityType)Entity1_13Types.EntityType.MINECART_ABSTRACT) && metadata.getId() == 9) {
      int oldId = ((Integer)metadata.getValue()).intValue();
      int combined = (oldId & 0xFFF) << 4 | oldId >> 12 & 0xF;
      int newId = WorldPackets.toNewId(combined);
      metadata.setValue(Integer.valueOf(newId));
    } 
    if (type == Entity1_13Types.EntityType.AREA_EFFECT_CLOUD) {
      if (metadata.getId() == 9) {
        int particleId = ((Integer)metadata.getValue()).intValue();
        Metadata parameter1Meta = getMetaByIndex(10, metadatas);
        Metadata parameter2Meta = getMetaByIndex(11, metadatas);
        int parameter1 = (parameter1Meta != null) ? ((Integer)parameter1Meta.getValue()).intValue() : 0;
        int parameter2 = (parameter2Meta != null) ? ((Integer)parameter2Meta.getValue()).intValue() : 0;
        Particle particle = ParticleRewriter.rewriteParticle(particleId, new Integer[] { Integer.valueOf(parameter1), Integer.valueOf(parameter2) });
        if (particle != null && particle.getId() != -1)
          metadatas.add(new Metadata(9, (MetaType)MetaType1_13.PARTICLE, particle)); 
      } 
      if (metadata.getId() >= 9)
        metadatas.remove(metadata); 
    } 
    if (metadata.getId() == 0)
      metadata.setValue(Byte.valueOf((byte)(((Byte)metadata.getValue()).byteValue() & 0xFFFFFFEF))); 
  }
  
  public int getNewEntityId(int oldId) {
    return EntityTypeRewriter.getNewId(oldId);
  }
  
  protected EntityType getTypeFromId(int type) {
    return (EntityType)Entity1_13Types.getTypeFromId(type, false);
  }
  
  protected EntityType getObjectTypeFromId(int type) {
    return (EntityType)Entity1_13Types.getTypeFromId(type, true);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\metadata\MetadataRewriter1_13To1_12_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */