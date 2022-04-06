package us.myles.viaversion.libs.javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

abstract class ConstInfo {
  int index;
  
  public ConstInfo(int i) {
    this.index = i;
  }
  
  public abstract int getTag();
  
  public String getClassName(ConstPool cp) {
    return null;
  }
  
  public void renameClass(ConstPool cp, String oldName, String newName, Map<ConstInfo, ConstInfo> cache) {}
  
  public void renameClass(ConstPool cp, Map<String, String> classnames, Map<ConstInfo, ConstInfo> cache) {}
  
  public abstract int copy(ConstPool paramConstPool1, ConstPool paramConstPool2, Map<String, String> paramMap);
  
  public abstract void write(DataOutputStream paramDataOutputStream) throws IOException;
  
  public abstract void print(PrintWriter paramPrintWriter);
  
  public String toString() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    PrintWriter out = new PrintWriter(bout);
    print(out);
    return bout.toString();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\ConstInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */