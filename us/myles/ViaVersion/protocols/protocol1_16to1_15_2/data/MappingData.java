package us.myles.ViaVersion.protocols.protocol1_16to1_15_2.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import us.myles.ViaVersion.api.data.MappingData;
import us.myles.viaversion.libs.gson.JsonObject;

public class MappingData extends MappingData {
  private final BiMap<String, String> attributeMappings = (BiMap<String, String>)HashBiMap.create();
  
  public MappingData() {
    super("1.15", "1.16", true);
  }
  
  protected void loadExtras(JsonObject oldMappings, JsonObject newMappings, JsonObject diffMappings) {
    this.attributeMappings.put("generic.maxHealth", "minecraft:generic.max_health");
    this.attributeMappings.put("zombie.spawnReinforcements", "minecraft:zombie.spawn_reinforcements");
    this.attributeMappings.put("horse.jumpStrength", "minecraft:horse.jump_strength");
    this.attributeMappings.put("generic.followRange", "minecraft:generic.follow_range");
    this.attributeMappings.put("generic.knockbackResistance", "minecraft:generic.knockback_resistance");
    this.attributeMappings.put("generic.movementSpeed", "minecraft:generic.movement_speed");
    this.attributeMappings.put("generic.flyingSpeed", "minecraft:generic.flying_speed");
    this.attributeMappings.put("generic.attackDamage", "minecraft:generic.attack_damage");
    this.attributeMappings.put("generic.attackKnockback", "minecraft:generic.attack_knockback");
    this.attributeMappings.put("generic.attackSpeed", "minecraft:generic.attack_speed");
    this.attributeMappings.put("generic.armorToughness", "minecraft:generic.armor_toughness");
  }
  
  public BiMap<String, String> getAttributeMappings() {
    return this.attributeMappings;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16to1_15_2\data\MappingData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */