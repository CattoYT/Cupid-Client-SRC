package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data;

import com.google.common.collect.ObjectArrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.fastutil.ints.Int2ObjectMap;
import us.myles.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import us.myles.viaversion.libs.gson.reflect.TypeToken;

public class BlockIdData {
  public static final String[] PREVIOUS = new String[0];
  
  public static Map<String, String[]> blockIdMapping;
  
  public static Map<String, String[]> fallbackReverseMapping;
  
  public static Int2ObjectMap<String> numberIdToString;
  
  public static void init() {
    InputStream stream = MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/blockIds1.12to1.13.json");
    try (InputStreamReader reader = new InputStreamReader(stream)) {
      Map<String, String[]> map = (Map<String, String[]>)GsonUtil.getGson().fromJson(reader, (new TypeToken<Map<String, String[]>>() {
          
          }).getType());
      blockIdMapping = (Map)new HashMap<>((Map)map);
      fallbackReverseMapping = (Map)new HashMap<>();
      for (Map.Entry<String, String[]> entry : blockIdMapping.entrySet()) {
        for (String val : (String[])entry.getValue()) {
          String[] previous = fallbackReverseMapping.get(val);
          if (previous == null)
            previous = PREVIOUS; 
          fallbackReverseMapping.put(val, ObjectArrays.concat((Object[])previous, entry.getKey()));
        } 
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } 
    InputStream blockS = MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/blockNumberToString1.12.json");
    try (InputStreamReader blockR = new InputStreamReader(blockS)) {
      Map<Integer, String> map = (Map<Integer, String>)GsonUtil.getGson().fromJson(blockR, (new TypeToken<Map<Integer, String>>() {
          
          }).getType());
      numberIdToString = (Int2ObjectMap<String>)new Int2ObjectOpenHashMap(map);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\data\BlockIdData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */