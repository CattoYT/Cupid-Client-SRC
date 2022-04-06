package us.myles.ViaVersion.protocols.protocol1_16to1_15_2;

import com.google.common.base.Joiner;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.MappingData;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.rewriters.ComponentRewriter;
import us.myles.ViaVersion.api.rewriters.RegistryType;
import us.myles.ViaVersion.api.rewriters.SoundRewriter;
import us.myles.ViaVersion.api.rewriters.StatisticsRewriter;
import us.myles.ViaVersion.api.rewriters.TagRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.data.MappingData;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.data.TranslationMappings;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.metadata.MetadataRewriter1_16To1_15_2;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.packets.EntityPackets;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.packets.WorldPackets;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.storage.EntityTracker1_16;
import us.myles.ViaVersion.protocols.protocol1_16to1_15_2.storage.InventoryTracker1_16;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.gson.JsonArray;
import us.myles.viaversion.libs.gson.JsonElement;
import us.myles.viaversion.libs.gson.JsonObject;

public class Protocol1_16To1_15_2 extends Protocol<ClientboundPackets1_15, ClientboundPackets1_16, ServerboundPackets1_14, ServerboundPackets1_16> {
  private static final UUID ZERO_UUID = new UUID(0L, 0L);
  
  public static final MappingData MAPPINGS = new MappingData();
  
  private TagRewriter tagRewriter;
  
  public Protocol1_16To1_15_2() {
    super(ClientboundPackets1_15.class, ClientboundPackets1_16.class, ServerboundPackets1_14.class, ServerboundPackets1_16.class);
  }
  
  protected void registerPackets() {
    MetadataRewriter1_16To1_15_2 metadataRewriter1_16To1_15_2 = new MetadataRewriter1_16To1_15_2(this);
    EntityPackets.register(this);
    WorldPackets.register(this);
    InventoryPackets.register(this);
    this.tagRewriter = new TagRewriter(this, metadataRewriter1_16To1_15_2::getNewEntityId);
    this.tagRewriter.register((ClientboundPacketType)ClientboundPackets1_15.TAGS);
    (new StatisticsRewriter(this, metadataRewriter1_16To1_15_2::getNewEntityId)).register((ClientboundPacketType)ClientboundPackets1_15.STATISTICS);
    registerOutgoing(State.LOGIN, 2, 2, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  UUID uuid = UUID.fromString((String)wrapper.read(Type.STRING));
                  wrapper.write(Type.UUID_INT_ARRAY, uuid);
                });
          }
        });
    registerOutgoing(State.STATUS, 0, 0, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  String original = (String)wrapper.passthrough(Type.STRING);
                  JsonObject object = (JsonObject)GsonUtil.getGson().fromJson(original, JsonObject.class);
                  JsonObject players = object.getAsJsonObject("players");
                  if (players == null)
                    return; 
                  JsonArray sample = players.getAsJsonArray("sample");
                  if (sample == null)
                    return; 
                  JsonArray splitSamples = new JsonArray();
                  for (JsonElement element : sample) {
                    JsonObject playerInfo = element.getAsJsonObject();
                    String name = playerInfo.getAsJsonPrimitive("name").getAsString();
                    if (name.indexOf('\n') == -1) {
                      splitSamples.add((JsonElement)playerInfo);
                      continue;
                    } 
                    String id = playerInfo.getAsJsonPrimitive("id").getAsString();
                    for (String s : name.split("\n")) {
                      JsonObject newSample = new JsonObject();
                      newSample.addProperty("name", s);
                      newSample.addProperty("id", id);
                      splitSamples.add((JsonElement)newSample);
                    } 
                  } 
                  if (splitSamples.size() != sample.size()) {
                    players.add("sample", (JsonElement)splitSamples);
                    wrapper.set(Type.STRING, 0, object.toString());
                  } 
                });
          }
        });
    final TranslationMappings componentRewriter = new TranslationMappings(this);
    registerOutgoing((ClientboundPacketType)ClientboundPackets1_15.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.COMPONENT);
            map(Type.BYTE);
            handler(wrapper -> {
                  componentRewriter.processText((JsonElement)wrapper.get(Type.COMPONENT, 0));
                  wrapper.write(Type.UUID, Protocol1_16To1_15_2.ZERO_UUID);
                });
          }
        });
    translationMappings.registerBossBar((ClientboundPacketType)ClientboundPackets1_15.BOSSBAR);
    translationMappings.registerTitle((ClientboundPacketType)ClientboundPackets1_15.TITLE);
    translationMappings.registerCombatEvent((ClientboundPacketType)ClientboundPackets1_15.COMBAT_EVENT);
    SoundRewriter soundRewriter = new SoundRewriter(this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_15.SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_15.ENTITY_SOUND);
    registerIncoming(ServerboundPackets1_16.INTERACT_ENTITY, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  int action = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  if (action == 0 || action == 2) {
                    if (action == 2) {
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.passthrough((Type)Type.FLOAT);
                    } 
                    wrapper.passthrough((Type)Type.VAR_INT);
                  } 
                  wrapper.read(Type.BOOLEAN);
                });
          }
        });
    if (Via.getConfig().isIgnoreLong1_16ChannelNames())
      registerIncoming(ServerboundPackets1_16.PLUGIN_MESSAGE, new PacketRemapper() {
            public void registerMap() {
              handler(wrapper -> {
                    String channel = (String)wrapper.passthrough(Type.STRING);
                    if (channel.length() > 32) {
                      if (!Via.getConfig().isSuppressConversionWarnings())
                        Via.getPlatform().getLogger().warning("Ignoring incoming plugin channel, as it is longer than 32 characters: " + channel); 
                      wrapper.cancel();
                    } else if (channel.equals("minecraft:register") || channel.equals("minecraft:unregister")) {
                      String[] channels = (new String((byte[])wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8)).split("\000");
                      List<String> checkedChannels = new ArrayList<>(channels.length);
                      for (String registeredChannel : channels) {
                        if (registeredChannel.length() > 32) {
                          if (!Via.getConfig().isSuppressConversionWarnings())
                            Via.getPlatform().getLogger().warning("Ignoring incoming plugin channel register of '" + registeredChannel + "', as it is longer than 32 characters"); 
                        } else {
                          checkedChannels.add(registeredChannel);
                        } 
                      } 
                      if (checkedChannels.isEmpty()) {
                        wrapper.cancel();
                        return;
                      } 
                      wrapper.write(Type.REMAINING_BYTES, Joiner.on(false).join(checkedChannels).getBytes(StandardCharsets.UTF_8));
                    } 
                  });
            }
          }); 
    registerIncoming(ServerboundPackets1_16.PLAYER_ABILITIES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough(Type.BYTE);
                  wrapper.write((Type)Type.FLOAT, Float.valueOf(0.05F));
                  wrapper.write((Type)Type.FLOAT, Float.valueOf(0.1F));
                });
          }
        });
    cancelIncoming(ServerboundPackets1_16.GENERATE_JIGSAW);
    cancelIncoming(ServerboundPackets1_16.UPDATE_JIGSAW_BLOCK);
  }
  
  protected void onMappingDataLoaded() {
    int[] wallPostOverrideTag = new int[47];
    int arrayIndex = 0;
    wallPostOverrideTag[arrayIndex++] = 140;
    wallPostOverrideTag[arrayIndex++] = 179;
    wallPostOverrideTag[arrayIndex++] = 264;
    int i;
    for (i = 153; i <= 158; i++)
      wallPostOverrideTag[arrayIndex++] = i; 
    for (i = 163; i <= 168; i++)
      wallPostOverrideTag[arrayIndex++] = i; 
    for (i = 408; i <= 439; i++)
      wallPostOverrideTag[arrayIndex++] = i; 
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:wall_post_override", wallPostOverrideTag);
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:beacon_base_blocks", new int[] { 133, 134, 148, 265 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:climbable", new int[] { 160, 241, 658 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:fire", new int[] { 142 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:campfires", new int[] { 679 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:fence_gates", new int[] { 242, 467, 468, 469, 470, 471 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:unstable_bottom_center", new int[] { 242, 467, 468, 469, 470, 471 });
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:wooden_trapdoors", new int[] { 193, 194, 195, 196, 197, 198 });
    this.tagRewriter.addTag(RegistryType.ITEM, "minecraft:wooden_trapdoors", new int[] { 215, 216, 217, 218, 219, 220 });
    this.tagRewriter.addTag(RegistryType.ITEM, "minecraft:beacon_payment_items", new int[] { 529, 530, 531, 760 });
    this.tagRewriter.addTag(RegistryType.ENTITY, "minecraft:impact_projectiles", new int[] { 2, 72, 71, 37, 69, 79, 83, 15, 93 });
    this.tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:guarded_by_piglins");
    this.tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:soul_speed_blocks");
    this.tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:soul_fire_base_blocks");
    this.tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:non_flammable_wood");
    this.tagRewriter.addEmptyTag(RegistryType.ITEM, "minecraft:non_flammable_wood");
    this.tagRewriter.addEmptyTags(RegistryType.BLOCK, new String[] { 
          "minecraft:bamboo_plantable_on", "minecraft:beds", "minecraft:bee_growables", "minecraft:beehives", "minecraft:coral_plants", "minecraft:crops", "minecraft:dragon_immune", "minecraft:flowers", "minecraft:portals", "minecraft:shulker_boxes", 
          "minecraft:small_flowers", "minecraft:tall_flowers", "minecraft:trapdoors", "minecraft:underwater_bonemeals", "minecraft:wither_immune", "minecraft:wooden_fences", "minecraft:wooden_trapdoors" });
    this.tagRewriter.addEmptyTags(RegistryType.ENTITY, new String[] { "minecraft:arrows", "minecraft:beehive_inhabitors", "minecraft:raiders", "minecraft:skeletons" });
    this.tagRewriter.addEmptyTags(RegistryType.ITEM, new String[] { 
          "minecraft:beds", "minecraft:coals", "minecraft:fences", "minecraft:flowers", "minecraft:lectern_books", "minecraft:music_discs", "minecraft:small_flowers", "minecraft:tall_flowers", "minecraft:trapdoors", "minecraft:walls", 
          "minecraft:wooden_fences" });
  }
  
  public void init(UserConnection userConnection) {
    userConnection.put((StoredObject)new EntityTracker1_16(userConnection));
    userConnection.put((StoredObject)new InventoryTracker1_16(userConnection));
  }
  
  public MappingData getMappingData() {
    return MAPPINGS;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16to1_15_2\Protocol1_16To1_15_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */