package us.myles.ViaVersion.protocols.protocol1_17to1_16_4;

import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.MappingData;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.RegistryType;
import us.myles.ViaVersion.api.rewriters.SoundRewriter;
import us.myles.ViaVersion.api.rewriters.StatisticsRewriter;
import us.myles.ViaVersion.api.rewriters.TagRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import us.myles.ViaVersion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import us.myles.ViaVersion.protocols.protocol1_17to1_16_4.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_17to1_16_4.packets.WorldPackets;
import us.myles.ViaVersion.protocols.protocol1_17to1_16_4.storage.BiomeStorage;

public class Protocol1_17To1_16_4 extends Protocol<ClientboundPackets1_16_2, ClientboundPackets1_16_2, ServerboundPackets1_16_2, ServerboundPackets1_16_2> {
  public static final MappingData MAPPINGS = new MappingData("1.16.2", "1.17", true);
  
  private TagRewriter tagRewriter;
  
  public Protocol1_17To1_16_4() {
    super(ClientboundPackets1_16_2.class, ClientboundPackets1_16_2.class, ServerboundPackets1_16_2.class, ServerboundPackets1_16_2.class);
  }
  
  protected void registerPackets() {
    InventoryPackets.register(this);
    WorldPackets.register(this);
    this.tagRewriter = new TagRewriter(this, null);
    this.tagRewriter.register((ClientboundPacketType)ClientboundPackets1_16_2.TAGS);
    (new StatisticsRewriter(this, null)).register((ClientboundPacketType)ClientboundPackets1_16_2.STATISTICS);
    SoundRewriter soundRewriter = new SoundRewriter(this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16_2.SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_16_2.ENTITY_SOUND);
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_16_2.RESOURCE_PACK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough(Type.STRING);
                  wrapper.passthrough(Type.STRING);
                  wrapper.write(Type.BOOLEAN, Boolean.valueOf(false));
                });
          }
        });
  }
  
  protected void onMappingDataLoaded() {
    this.tagRewriter.addEmptyTags(RegistryType.ITEM, new String[] { "minecraft:candles", "minecraft:ignored_by_piglin_babies", "minecraft:piglin_food" });
    this.tagRewriter.addEmptyTags(RegistryType.BLOCK, new String[] { "minecraft:crystal_sound_blocks", "minecraft:candle_cakes", "minecraft:candles" });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:cauldrons", new int[] { 261 });
  }
  
  public void init(UserConnection user) {
    user.put((StoredObject)new BiomeStorage(user));
  }
  
  @Nullable
  public MappingData getMappingData() {
    return MAPPINGS;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_17to1_16_4\Protocol1_17To1_16_4.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */