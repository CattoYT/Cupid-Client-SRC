package us.myles.ViaVersion.protocols.protocol1_9to1_8.metadata;

import java.util.List;
import java.util.UUID;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_10Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.EulerAngle;
import us.myles.ViaVersion.api.minecraft.Vector;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_8;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_9;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.rewriters.MetadataRewriter;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ItemRewriter;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;

public class MetadataRewriter1_9To1_8 extends MetadataRewriter {
  public MetadataRewriter1_9To1_8(Protocol1_9To1_8 protocol) {
    super((Protocol)protocol, EntityTracker1_9.class);
  }
  
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
    String owner;
    UUID toWrite;
    Vector vector;
    EulerAngle angle;
    MetaIndex metaIndex = MetaIndex.searchIndex(type, metadata.getId());
    if (metaIndex == null)
      throw new Exception("Could not find valid metadata"); 
    if (metaIndex.getNewType() == MetaType1_9.Discontinued) {
      metadatas.remove(metadata);
      return;
    } 
    metadata.setId(metaIndex.getNewIndex());
    metadata.setMetaType((MetaType)metaIndex.getNewType());
    Object value = metadata.getValue();
    switch (metaIndex.getNewType()) {
      case Byte:
        if (metaIndex.getOldType() == MetaType1_8.Byte)
          metadata.setValue(value); 
        if (metaIndex.getOldType() == MetaType1_8.Int)
          metadata.setValue(Byte.valueOf(((Integer)value).byteValue())); 
        if (metaIndex == MetaIndex.ENTITY_STATUS && type == Entity1_10Types.EntityType.PLAYER) {
          Byte val = Byte.valueOf((byte)0);
          if ((((Byte)value).byteValue() & 0x10) == 16)
            val = Byte.valueOf((byte)1); 
          int newIndex = MetaIndex.PLAYER_HAND.getNewIndex();
          MetaType1_9 metaType1_9 = MetaIndex.PLAYER_HAND.getNewType();
          metadatas.add(new Metadata(newIndex, (MetaType)metaType1_9, val));
        } 
        return;
      case OptUUID:
        owner = (String)value;
        toWrite = null;
        if (!owner.isEmpty())
          try {
            toWrite = UUID.fromString(owner);
          } catch (Exception exception) {} 
        metadata.setValue(toWrite);
        return;
      case VarInt:
        if (metaIndex.getOldType() == MetaType1_8.Byte)
          metadata.setValue(Integer.valueOf(((Byte)value).intValue())); 
        if (metaIndex.getOldType() == MetaType1_8.Short)
          metadata.setValue(Integer.valueOf(((Short)value).intValue())); 
        if (metaIndex.getOldType() == MetaType1_8.Int)
          metadata.setValue(value); 
        return;
      case Float:
        metadata.setValue(value);
        return;
      case String:
        metadata.setValue(value);
        return;
      case Boolean:
        if (metaIndex == MetaIndex.AGEABLE_AGE) {
          metadata.setValue(Boolean.valueOf((((Byte)value).byteValue() < 0)));
        } else {
          metadata.setValue(Boolean.valueOf((((Byte)value).byteValue() != 0)));
        } 
        return;
      case Slot:
        metadata.setValue(value);
        ItemRewriter.toClient((Item)metadata.getValue());
        return;
      case Position:
        vector = (Vector)value;
        metadata.setValue(vector);
        return;
      case Vector3F:
        angle = (EulerAngle)value;
        metadata.setValue(angle);
        return;
      case Chat:
        value = Protocol1_9To1_8.fixJson(value.toString());
        metadata.setValue(value);
        return;
      case BlockID:
        metadata.setValue(Integer.valueOf(((Number)value).intValue()));
        return;
    } 
    metadatas.remove(metadata);
    throw new Exception("Unhandled MetaDataType: " + metaIndex.getNewType());
  }
  
  protected EntityType getTypeFromId(int type) {
    return (EntityType)Entity1_10Types.getTypeFromId(type, false);
  }
  
  protected EntityType getObjectTypeFromId(int type) {
    return (EntityType)Entity1_10Types.getTypeFromId(type, true);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\metadata\MetadataRewriter1_9To1_8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */