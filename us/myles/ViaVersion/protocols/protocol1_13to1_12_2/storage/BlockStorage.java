package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.viaversion.libs.fastutil.ints.IntOpenHashSet;
import us.myles.viaversion.libs.fastutil.ints.IntSet;

public class BlockStorage extends StoredObject {
  private static final IntSet WHITELIST = (IntSet)new IntOpenHashSet(46, 1.0F);
  
  private final Map<Position, ReplacementData> blocks = new ConcurrentHashMap<>();
  
  static {
    WHITELIST.add(5266);
    int i;
    for (i = 0; i < 16; i++)
      WHITELIST.add(972 + i); 
    for (i = 0; i < 20; i++)
      WHITELIST.add(6854 + i); 
    for (i = 0; i < 4; i++)
      WHITELIST.add(7110 + i); 
    for (i = 0; i < 5; i++)
      WHITELIST.add(5447 + i); 
  }
  
  public BlockStorage(UserConnection user) {
    super(user);
  }
  
  public void store(Position position, int block) {
    store(position, block, -1);
  }
  
  public void store(Position position, int block, int replacementId) {
    if (!WHITELIST.contains(block))
      return; 
    this.blocks.put(position, new ReplacementData(block, replacementId));
  }
  
  public boolean isWelcome(int block) {
    return WHITELIST.contains(block);
  }
  
  public boolean contains(Position position) {
    return this.blocks.containsKey(position);
  }
  
  public ReplacementData get(Position position) {
    return this.blocks.get(position);
  }
  
  public ReplacementData remove(Position position) {
    return this.blocks.remove(position);
  }
  
  public static class ReplacementData {
    private int original;
    
    private int replacement;
    
    public ReplacementData(int original, int replacement) {
      this.original = original;
      this.replacement = replacement;
    }
    
    public int getOriginal() {
      return this.original;
    }
    
    public void setOriginal(int original) {
      this.original = original;
    }
    
    public int getReplacement() {
      return this.replacement;
    }
    
    public void setReplacement(int replacement) {
      this.replacement = replacement;
    }
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\storage\BlockStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */