package us.myles.viaversion.libs.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntTag extends Tag {
  private int value;
  
  public IntTag(String name) {
    this(name, 0);
  }
  
  public IntTag(String name, int value) {
    super(name);
    this.value = value;
  }
  
  public Integer getValue() {
    return Integer.valueOf(this.value);
  }
  
  public void setValue(int value) {
    this.value = value;
  }
  
  public void read(DataInput in) throws IOException {
    this.value = in.readInt();
  }
  
  public void write(DataOutput out) throws IOException {
    out.writeInt(this.value);
  }
  
  public IntTag clone() {
    return new IntTag(getName(), getValue().intValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\tag\builtin\IntTag.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */