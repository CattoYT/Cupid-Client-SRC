package us.myles.ViaVersion.protocols.protocol1_11to1_10.metadata;

import java.util.List;
import java.util.Optional;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_11Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_9;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.rewriters.MetadataRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_11to1_10.EntityIdRewriter;
import us.myles.ViaVersion.protocols.protocol1_11to1_10.Protocol1_11To1_10;
import us.myles.ViaVersion.protocols.protocol1_11to1_10.storage.EntityTracker1_11;

public class MetadataRewriter1_11To1_10 extends MetadataRewriter {
  public MetadataRewriter1_11To1_10(Protocol1_11To1_10 protocol) {
    super((Protocol)protocol, EntityTracker1_11.class);
  }
  
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) {
    if (metadata.getValue() instanceof Item)
      EntityIdRewriter.toClientItem((Item)metadata.getValue()); 
    if (type == null)
      return; 
    if (type.is((EntityType)Entity1_11Types.EntityType.ELDER_GUARDIAN) || type.is((EntityType)Entity1_11Types.EntityType.GUARDIAN)) {
      int oldid = metadata.getId();
      if (oldid == 12) {
        metadata.setMetaType((MetaType)MetaType1_9.Boolean);
        boolean val = ((((Byte)metadata.getValue()).byteValue() & 0x2) == 2);
        metadata.setValue(Boolean.valueOf(val));
      } 
    } 
    if (type.isOrHasParent((EntityType)Entity1_11Types.EntityType.ABSTRACT_SKELETON)) {
      int oldid = metadata.getId();
      if (oldid == 12)
        metadatas.remove(metadata); 
      if (oldid == 13)
        metadata.setId(12); 
    } 
    if (type.isOrHasParent((EntityType)Entity1_11Types.EntityType.ZOMBIE))
      if (type.is(new EntityType[] { (EntityType)Entity1_11Types.EntityType.ZOMBIE, (EntityType)Entity1_11Types.EntityType.HUSK }) && metadata.getId() == 14) {
        metadatas.remove(metadata);
      } else if (metadata.getId() == 15) {
        metadata.setId(14);
      } else if (metadata.getId() == 14) {
        metadata.setId(15);
      }  
    if (type.isOrHasParent((EntityType)Entity1_11Types.EntityType.ABSTRACT_HORSE)) {
      int oldid = metadata.getId();
      if (oldid == 14)
        metadatas.remove(metadata); 
      if (oldid == 16)
        metadata.setId(14); 
      if (oldid == 17)
        metadata.setId(16); 
      if (!type.is((EntityType)Entity1_11Types.EntityType.HORSE))
        if (metadata.getId() == 15 || metadata.getId() == 16)
          metadatas.remove(metadata);  
      if (type.is(new EntityType[] { (EntityType)Entity1_11Types.EntityType.DONKEY, (EntityType)Entity1_11Types.EntityType.MULE }))
        if (metadata.getId() == 13)
          if ((((Byte)metadata.getValue()).byteValue() & 0x8) == 8) {
            metadatas.add(new Metadata(15, (MetaType)MetaType1_9.Boolean, Boolean.valueOf(true)));
          } else {
            metadatas.add(new Metadata(15, (MetaType)MetaType1_9.Boolean, Boolean.valueOf(false)));
          }   
    } 
    if (type.is((EntityType)Entity1_11Types.EntityType.ARMOR_STAND) && Via.getConfig().isHologramPatch()) {
      Metadata flags = getMetaByIndex(11, metadatas);
      Metadata customName = getMetaByIndex(2, metadatas);
      Metadata customNameVisible = getMetaByIndex(3, metadatas);
      if (metadata.getId() == 0 && flags != null && customName != null && customNameVisible != null) {
        byte data = ((Byte)metadata.getValue()).byteValue();
        if ((data & 0x20) == 32 && (((Byte)flags.getValue()).byteValue() & 0x1) == 1 && 
          !((String)customName.getValue()).isEmpty() && ((Boolean)customNameVisible.getValue()).booleanValue()) {
          EntityTracker1_11 tracker = (EntityTracker1_11)connection.get(EntityTracker1_11.class);
          if (!tracker.isHologram(entityId)) {
            tracker.addHologram(entityId);
            try {
              PacketWrapper wrapper = new PacketWrapper(37, null, connection);
              wrapper.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
              wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
              wrapper.write((Type)Type.SHORT, Short.valueOf((short)(int)(128.0D * -Via.getConfig().getHologramYOffset() * 32.0D)));
              wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
              wrapper.write(Type.BOOLEAN, Boolean.valueOf(true));
              wrapper.send(Protocol1_11To1_10.class);
            } catch (Exception e) {
              e.printStackTrace();
            } 
          } 
        } 
      } 
    } 
  }
  
  protected EntityType getTypeFromId(int type) {
    return (EntityType)Entity1_11Types.getTypeFromId(type, false);
  }
  
  protected EntityType getObjectTypeFromId(int type) {
    return (EntityType)Entity1_11Types.getTypeFromId(type, true);
  }
  
  public static Entity1_11Types.EntityType rewriteEntityType(int numType, List<Metadata> metadata) {
    Optional<Entity1_11Types.EntityType> optType = Entity1_11Types.EntityType.findById(numType);
    if (!optType.isPresent()) {
      Via.getManager().getPlatform().getLogger().severe("Error: could not find Entity type " + numType + " with metadata: " + metadata);
      return null;
    } 
    Entity1_11Types.EntityType type = optType.get();
    try {
      if (type.is((EntityType)Entity1_11Types.EntityType.GUARDIAN)) {
        Optional<Metadata> options = getById(metadata, 12);
        if (options.isPresent() && ((
          (Byte)((Metadata)options.get()).getValue()).byteValue() & 0x4) == 4)
          return Entity1_11Types.EntityType.ELDER_GUARDIAN; 
      } 
      if (type.is((EntityType)Entity1_11Types.EntityType.SKELETON)) {
        Optional<Metadata> options = getById(metadata, 12);
        if (options.isPresent()) {
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 1)
            return Entity1_11Types.EntityType.WITHER_SKELETON; 
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 2)
            return Entity1_11Types.EntityType.STRAY; 
        } 
      } 
      if (type.is((EntityType)Entity1_11Types.EntityType.ZOMBIE)) {
        Optional<Metadata> options = getById(metadata, 13);
        if (options.isPresent()) {
          int value = ((Integer)((Metadata)options.get()).getValue()).intValue();
          if (value > 0 && value < 6) {
            metadata.add(new Metadata(16, (MetaType)MetaType1_9.VarInt, Integer.valueOf(value - 1)));
            return Entity1_11Types.EntityType.ZOMBIE_VILLAGER;
          } 
          if (value == 6)
            return Entity1_11Types.EntityType.HUSK; 
        } 
      } 
      if (type.is((EntityType)Entity1_11Types.EntityType.HORSE)) {
        Optional<Metadata> options = getById(metadata, 14);
        if (options.isPresent()) {
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 0)
            return Entity1_11Types.EntityType.HORSE; 
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 1)
            return Entity1_11Types.EntityType.DONKEY; 
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 2)
            return Entity1_11Types.EntityType.MULE; 
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 3)
            return Entity1_11Types.EntityType.ZOMBIE_HORSE; 
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 4)
            return Entity1_11Types.EntityType.SKELETON_HORSE; 
        } 
      } 
    } catch (Exception e) {
      if (!Via.getConfig().isSuppressMetadataErrors() || Via.getManager().isDebug()) {
        Via.getPlatform().getLogger().warning("An error occurred with entity type rewriter");
        Via.getPlatform().getLogger().warning("Metadata: " + metadata);
        e.printStackTrace();
      } 
    } 
    return type;
  }
  
  public static Optional<Metadata> getById(List<Metadata> metadatas, int id) {
    for (Metadata metadata : metadatas) {
      if (metadata.getId() == id)
        return Optional.of(metadata); 
    } 
    return Optional.empty();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_11to1_10\metadata\MetadataRewriter1_11To1_10.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */