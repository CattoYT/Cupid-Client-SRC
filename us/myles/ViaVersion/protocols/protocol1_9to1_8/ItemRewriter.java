package us.myles.ViaVersion.protocols.protocol1_9to1_8;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.viaversion.libs.fastutil.ints.Int2IntMap;
import us.myles.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ListTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.StringTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class ItemRewriter {
  private static final Map<String, Integer> ENTTIY_NAME_TO_ID = new HashMap<>();
  
  private static final Map<Integer, String> ENTTIY_ID_TO_NAME = new HashMap<>();
  
  private static final Map<String, Integer> POTION_NAME_TO_ID = new HashMap<>();
  
  private static final Map<Integer, String> POTION_ID_TO_NAME = new HashMap<>();
  
  private static final Int2IntMap POTION_INDEX = (Int2IntMap)new Int2IntOpenHashMap(36, 1.0F);
  
  static {
    registerEntity(1, "Item");
    registerEntity(2, "XPOrb");
    registerEntity(7, "ThrownEgg");
    registerEntity(8, "LeashKnot");
    registerEntity(9, "Painting");
    registerEntity(10, "Arrow");
    registerEntity(11, "Snowball");
    registerEntity(12, "Fireball");
    registerEntity(13, "SmallFireball");
    registerEntity(14, "ThrownEnderpearl");
    registerEntity(15, "EyeOfEnderSignal");
    registerEntity(16, "ThrownPotion");
    registerEntity(17, "ThrownExpBottle");
    registerEntity(18, "ItemFrame");
    registerEntity(19, "WitherSkull");
    registerEntity(20, "PrimedTnt");
    registerEntity(21, "FallingSand");
    registerEntity(22, "FireworksRocketEntity");
    registerEntity(30, "ArmorStand");
    registerEntity(40, "MinecartCommandBlock");
    registerEntity(41, "Boat");
    registerEntity(42, "MinecartRideable");
    registerEntity(43, "MinecartChest");
    registerEntity(44, "MinecartFurnace");
    registerEntity(45, "MinecartTNT");
    registerEntity(46, "MinecartHopper");
    registerEntity(47, "MinecartSpawner");
    registerEntity(48, "Mob");
    registerEntity(49, "Monster");
    registerEntity(50, "Creeper");
    registerEntity(51, "Skeleton");
    registerEntity(52, "Spider");
    registerEntity(53, "Giant");
    registerEntity(54, "Zombie");
    registerEntity(55, "Slime");
    registerEntity(56, "Ghast");
    registerEntity(57, "PigZombie");
    registerEntity(58, "Enderman");
    registerEntity(59, "CaveSpider");
    registerEntity(60, "Silverfish");
    registerEntity(61, "Blaze");
    registerEntity(62, "LavaSlime");
    registerEntity(63, "EnderDragon");
    registerEntity(64, "WitherBoss");
    registerEntity(65, "Bat");
    registerEntity(66, "Witch");
    registerEntity(67, "Endermite");
    registerEntity(68, "Guardian");
    registerEntity(90, "Pig");
    registerEntity(91, "Sheep");
    registerEntity(92, "Cow");
    registerEntity(93, "Chicken");
    registerEntity(94, "Squid");
    registerEntity(95, "Wolf");
    registerEntity(96, "MushroomCow");
    registerEntity(97, "SnowMan");
    registerEntity(98, "Ozelot");
    registerEntity(99, "VillagerGolem");
    registerEntity(100, "EntityHorse");
    registerEntity(101, "Rabbit");
    registerEntity(120, "Villager");
    registerEntity(200, "EnderCrystal");
    registerPotion(-1, "empty");
    registerPotion(0, "water");
    registerPotion(64, "mundane");
    registerPotion(32, "thick");
    registerPotion(16, "awkward");
    registerPotion(8198, "night_vision");
    registerPotion(8262, "long_night_vision");
    registerPotion(8206, "invisibility");
    registerPotion(8270, "long_invisibility");
    registerPotion(8203, "leaping");
    registerPotion(8267, "long_leaping");
    registerPotion(8235, "strong_leaping");
    registerPotion(8195, "fire_resistance");
    registerPotion(8259, "long_fire_resistance");
    registerPotion(8194, "swiftness");
    registerPotion(8258, "long_swiftness");
    registerPotion(8226, "strong_swiftness");
    registerPotion(8202, "slowness");
    registerPotion(8266, "long_slowness");
    registerPotion(8205, "water_breathing");
    registerPotion(8269, "long_water_breathing");
    registerPotion(8261, "healing");
    registerPotion(8229, "strong_healing");
    registerPotion(8204, "harming");
    registerPotion(8236, "strong_harming");
    registerPotion(8196, "poison");
    registerPotion(8260, "long_poison");
    registerPotion(8228, "strong_poison");
    registerPotion(8193, "regeneration");
    registerPotion(8257, "long_regeneration");
    registerPotion(8225, "strong_regeneration");
    registerPotion(8201, "strength");
    registerPotion(8265, "long_strength");
    registerPotion(8233, "strong_strength");
    registerPotion(8200, "weakness");
    registerPotion(8264, "long_weakness");
  }
  
  public static void toServer(Item item) {
    if (item != null) {
      if (item.getIdentifier() == 383 && item.getData() == 0) {
        CompoundTag tag = item.getTag();
        int data = 0;
        if (tag != null && tag.get("EntityTag") instanceof CompoundTag) {
          CompoundTag entityTag = (CompoundTag)tag.get("EntityTag");
          if (entityTag.get("id") instanceof StringTag) {
            StringTag id = (StringTag)entityTag.get("id");
            if (ENTTIY_NAME_TO_ID.containsKey(id.getValue()))
              data = ((Integer)ENTTIY_NAME_TO_ID.get(id.getValue())).intValue(); 
          } 
          tag.remove("EntityTag");
        } 
        item.setTag(tag);
        item.setData((short)data);
      } 
      if (item.getIdentifier() == 373) {
        CompoundTag tag = item.getTag();
        int data = 0;
        if (tag != null && tag.get("Potion") instanceof StringTag) {
          StringTag potion = (StringTag)tag.get("Potion");
          String potionName = potion.getValue().replace("minecraft:", "");
          if (POTION_NAME_TO_ID.containsKey(potionName))
            data = ((Integer)POTION_NAME_TO_ID.get(potionName)).intValue(); 
          tag.remove("Potion");
        } 
        item.setTag(tag);
        item.setData((short)data);
      } 
      if (item.getIdentifier() == 438) {
        CompoundTag tag = item.getTag();
        int data = 0;
        item.setIdentifier(373);
        if (tag != null && tag.get("Potion") instanceof StringTag) {
          StringTag potion = (StringTag)tag.get("Potion");
          String potionName = potion.getValue().replace("minecraft:", "");
          if (POTION_NAME_TO_ID.containsKey(potionName))
            data = ((Integer)POTION_NAME_TO_ID.get(potionName)).intValue() + 8192; 
          tag.remove("Potion");
        } 
        item.setTag(tag);
        item.setData((short)data);
      } 
    } 
  }
  
  public static void rewriteBookToServer(Item item) {
    int id = item.getIdentifier();
    if (id != 387)
      return; 
    CompoundTag tag = item.getTag();
    ListTag pages = (ListTag)tag.get("pages");
    if (pages == null)
      return; 
    for (int i = 0; i < pages.size(); i++) {
      Tag pageTag = pages.get(i);
      if (pageTag instanceof StringTag) {
        StringTag stag = (StringTag)pageTag;
        String value = stag.getValue();
        if (value.replaceAll(" ", "").isEmpty()) {
          value = "\"" + fixBookSpaceChars(value) + "\"";
        } else {
          value = fixBookSpaceChars(value);
        } 
        stag.setValue(value);
      } 
    } 
  }
  
  private static String fixBookSpaceChars(String str) {
    if (!str.startsWith(" "))
      return str; 
    str = "§r" + str;
    return str;
  }
  
  public static void toClient(Item item) {
    if (item != null) {
      if (item.getIdentifier() == 383 && item.getData() != 0) {
        CompoundTag tag = item.getTag();
        if (tag == null)
          tag = new CompoundTag("tag"); 
        CompoundTag entityTag = new CompoundTag("EntityTag");
        String entityName = ENTTIY_ID_TO_NAME.get(Integer.valueOf(item.getData()));
        if (entityName != null) {
          StringTag id = new StringTag("id", entityName);
          entityTag.put((Tag)id);
          tag.put((Tag)entityTag);
        } 
        item.setTag(tag);
        item.setData((short)0);
      } 
      if (item.getIdentifier() == 373) {
        CompoundTag tag = item.getTag();
        if (tag == null)
          tag = new CompoundTag("tag"); 
        if (item.getData() >= 16384) {
          item.setIdentifier(438);
          item.setData((short)(item.getData() - 8192));
        } 
        String name = potionNameFromDamage(item.getData());
        StringTag potion = new StringTag("Potion", "minecraft:" + name);
        tag.put((Tag)potion);
        item.setTag(tag);
        item.setData((short)0);
      } 
      if (item.getIdentifier() == 387) {
        CompoundTag tag = item.getTag();
        if (tag == null)
          tag = new CompoundTag("tag"); 
        ListTag pages = (ListTag)tag.get("pages");
        if (pages == null) {
          pages = new ListTag("pages", Collections.singletonList(new StringTag(Protocol1_9To1_8.fixJson("").toString())));
          tag.put((Tag)pages);
          item.setTag(tag);
          return;
        } 
        for (int i = 0; i < pages.size(); i++) {
          if (pages.get(i) instanceof StringTag) {
            StringTag page = (StringTag)pages.get(i);
            page.setValue(Protocol1_9To1_8.fixJson(page.getValue()).toString());
          } 
        } 
        item.setTag(tag);
      } 
    } 
  }
  
  public static String potionNameFromDamage(short damage) {
    String id, cached = POTION_ID_TO_NAME.get(Integer.valueOf(damage));
    if (cached != null)
      return cached; 
    if (damage == 0)
      return "water"; 
    int effect = damage & 0xF;
    int name = damage & 0x3F;
    boolean enhanced = ((damage & 0x20) > 0);
    boolean extended = ((damage & 0x40) > 0);
    boolean canEnhance = true;
    boolean canExtend = true;
    switch (effect) {
      case 1:
        id = "regeneration";
        break;
      case 2:
        id = "swiftness";
        break;
      case 3:
        id = "fire_resistance";
        canEnhance = false;
        break;
      case 4:
        id = "poison";
        break;
      case 5:
        id = "healing";
        canExtend = false;
        break;
      case 6:
        id = "night_vision";
        canEnhance = false;
        break;
      case 8:
        id = "weakness";
        canEnhance = false;
        break;
      case 9:
        id = "strength";
        break;
      case 10:
        id = "slowness";
        canEnhance = false;
        break;
      case 11:
        id = "leaping";
        break;
      case 12:
        id = "harming";
        canExtend = false;
        break;
      case 13:
        id = "water_breathing";
        canEnhance = false;
        break;
      case 14:
        id = "invisibility";
        canEnhance = false;
        break;
      default:
        canEnhance = false;
        canExtend = false;
        switch (name) {
          case 0:
            id = "mundane";
            break;
          case 16:
            id = "awkward";
            break;
          case 32:
            id = "thick";
            break;
        } 
        id = "empty";
        break;
    } 
    if (effect > 0)
      if (canEnhance && enhanced) {
        id = "strong_" + id;
      } else if (canExtend && extended) {
        id = "long_" + id;
      }  
    return id;
  }
  
  public static int getNewEffectID(int oldID) {
    if (oldID >= 16384)
      oldID -= 8192; 
    int index = POTION_INDEX.get(oldID);
    if (index != -1)
      return index; 
    oldID = ((Integer)POTION_NAME_TO_ID.get(potionNameFromDamage((short)oldID))).intValue();
    return 
      ((index = POTION_INDEX.get(oldID)) != -1) ? index : 0;
  }
  
  private static void registerEntity(int id, String name) {
    ENTTIY_ID_TO_NAME.put(Integer.valueOf(id), name);
    ENTTIY_NAME_TO_ID.put(name, Integer.valueOf(id));
  }
  
  private static void registerPotion(int id, String name) {
    POTION_INDEX.put(id, POTION_ID_TO_NAME.size());
    POTION_ID_TO_NAME.put(Integer.valueOf(id), name);
    POTION_NAME_TO_ID.put(name, Integer.valueOf(id));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\ItemRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */