package us.myles.ViaVersion.protocols.protocol1_15to1_14_4;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.MappingData;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.RegistryType;
import us.myles.ViaVersion.api.rewriters.SoundRewriter;
import us.myles.ViaVersion.api.rewriters.StatisticsRewriter;
import us.myles.ViaVersion.api.rewriters.TagRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.data.MappingData;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.metadata.MetadataRewriter1_15To1_14_4;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.packets.EntityPackets;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.packets.PlayerPackets;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.packets.WorldPackets;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.storage.EntityTracker1_15;

public class Protocol1_15To1_14_4 extends Protocol<ClientboundPackets1_14, ClientboundPackets1_15, ServerboundPackets1_14, ServerboundPackets1_14> {
  public static final MappingData MAPPINGS = new MappingData();
  
  private TagRewriter tagRewriter;
  
  public Protocol1_15To1_14_4() {
    super(ClientboundPackets1_14.class, ClientboundPackets1_15.class, ServerboundPackets1_14.class, ServerboundPackets1_14.class);
  }
  
  protected void registerPackets() {
    MetadataRewriter1_15To1_14_4 metadataRewriter1_15To1_14_4 = new MetadataRewriter1_15To1_14_4(this);
    EntityPackets.register(this);
    PlayerPackets.register(this);
    WorldPackets.register(this);
    InventoryPackets.register(this);
    SoundRewriter soundRewriter = new SoundRewriter(this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_14.ENTITY_SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_14.SOUND);
    (new StatisticsRewriter(this, metadataRewriter1_15To1_14_4::getNewEntityId)).register((ClientboundPacketType)ClientboundPackets1_14.STATISTICS);
    registerIncoming((ServerboundPacketType)ServerboundPackets1_14.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> InventoryPackets.toServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    this.tagRewriter = new TagRewriter(this, EntityPackets::getNewEntityId);
    this.tagRewriter.register((ClientboundPacketType)ClientboundPackets1_14.TAGS);
  }
  
  protected void onMappingDataLoaded() {
    int[] shulkerBoxes = new int[17];
    int shulkerBoxOffset = 501;
    for (int i = 0; i < 17; i++)
      shulkerBoxes[i] = shulkerBoxOffset + i; 
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:shulker_boxes", shulkerBoxes);
  }
  
  public void init(UserConnection userConnection) {
    userConnection.put((StoredObject)new EntityTracker1_15(userConnection));
  }
  
  public MappingData getMappingData() {
    return MAPPINGS;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_15to1_14_4\Protocol1_15To1_14_4.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */