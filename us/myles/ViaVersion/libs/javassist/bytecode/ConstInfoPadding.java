package us.myles.viaversion.libs.javassist.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class ConstInfoPadding extends ConstInfo {
  public ConstInfoPadding(int i) {
    super(i);
  }
  
  public int getTag() {
    return 0;
  }
  
  public int copy(ConstPool src, ConstPool dest, Map<String, String> map) {
    return dest.addConstInfoPadding();
  }
  
  public void write(DataOutputStream out) throws IOException {}
  
  public void print(PrintWriter out) {
    out.println("padding");
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\ConstInfoPadding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */