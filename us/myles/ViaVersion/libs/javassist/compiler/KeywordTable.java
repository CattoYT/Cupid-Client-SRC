package us.myles.viaversion.libs.javassist.compiler;

import java.util.HashMap;

public final class KeywordTable extends HashMap<String, Integer> {
  private static final long serialVersionUID = 1L;
  
  public int lookup(String name) {
    return containsKey(name) ? get(name).intValue() : -1;
  }
  
  public void append(String name, int t) {
    put(name, Integer.valueOf(t));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\KeywordTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */