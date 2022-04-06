package us.myles.ViaVersion.protocols.protocol1_9to1_8.sounds;

import us.myles.viaversion.libs.fastutil.ints.Int2IntMap;
import us.myles.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public class Effect {
  private static final Int2IntMap EFFECTS = (Int2IntMap)new Int2IntOpenHashMap(19, 1.0F);
  
  static {
    addRewrite(1005, 1010);
    addRewrite(1003, 1005);
    addRewrite(1006, 1011);
    addRewrite(1004, 1009);
    addRewrite(1007, 1015);
    addRewrite(1008, 1016);
    addRewrite(1009, 1016);
    addRewrite(1010, 1019);
    addRewrite(1011, 1020);
    addRewrite(1012, 1021);
    addRewrite(1014, 1024);
    addRewrite(1015, 1025);
    addRewrite(1016, 1026);
    addRewrite(1017, 1027);
    addRewrite(1020, 1029);
    addRewrite(1021, 1030);
    addRewrite(1022, 1031);
    addRewrite(1013, 1023);
    addRewrite(1018, 1028);
  }
  
  public static int getNewId(int id) {
    return EFFECTS.getOrDefault(id, id);
  }
  
  public static boolean contains(int oldId) {
    return EFFECTS.containsKey(oldId);
  }
  
  private static void addRewrite(int oldId, int newId) {
    EFFECTS.put(oldId, newId);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\sounds\Effect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */