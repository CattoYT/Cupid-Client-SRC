package us.myles.viaversion.libs.javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class DeprecatedAttribute extends AttributeInfo {
  public static final String tag = "Deprecated";
  
  DeprecatedAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }
  
  public DeprecatedAttribute(ConstPool cp) {
    super(cp, "Deprecated", new byte[0]);
  }
  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    return new DeprecatedAttribute(newCp);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\DeprecatedAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */