package us.myles.viaversion.libs.javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class SyntheticAttribute extends AttributeInfo {
  public static final String tag = "Synthetic";
  
  SyntheticAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }
  
  public SyntheticAttribute(ConstPool cp) {
    super(cp, "Synthetic", new byte[0]);
  }
  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    return new SyntheticAttribute(newCp);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\SyntheticAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */