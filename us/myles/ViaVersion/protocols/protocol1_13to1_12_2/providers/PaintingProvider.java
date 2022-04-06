package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.providers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import us.myles.ViaVersion.api.platform.providers.Provider;

public class PaintingProvider implements Provider {
  private final Map<String, Integer> paintings = new HashMap<>();
  
  public PaintingProvider() {
    add("kebab");
    add("aztec");
    add("alban");
    add("aztec2");
    add("bomb");
    add("plant");
    add("wasteland");
    add("pool");
    add("courbet");
    add("sea");
    add("sunset");
    add("creebet");
    add("wanderer");
    add("graham");
    add("match");
    add("bust");
    add("stage");
    add("void");
    add("skullandroses");
    add("wither");
    add("fighters");
    add("pointer");
    add("pigscene");
    add("burningskull");
    add("skeleton");
    add("donkeykong");
  }
  
  private void add(String motive) {
    this.paintings.put("minecraft:" + motive, Integer.valueOf(this.paintings.size()));
  }
  
  public Optional<Integer> getIntByIdentifier(String motive) {
    if (!motive.startsWith("minecraft:"))
      motive = "minecraft:" + motive.toLowerCase(Locale.ROOT); 
    return Optional.ofNullable(this.paintings.get(motive));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\providers\PaintingProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */