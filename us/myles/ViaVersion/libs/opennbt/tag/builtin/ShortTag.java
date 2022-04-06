package us.myles.viaversion.libs.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends Tag {
  private short value;
  
  public ShortTag(String name) {
    this(name, (short)0);
  }
  
  public ShortTag(String name, short value) {
    super(name);
    this.value = value;
  }
  
  public Short getValue() {
    return Short.valueOf(this.value);
  }
  
  public void setValue(short value) {
    this.value = value;
  }
  
  public void read(DataInput in) throws IOException {
    this.value = in.readShort();
  }
  
  public void write(DataOutput out) throws IOException {
    out.writeShort(this.value);
  }
  
  public ShortTag clone() {
    return new ShortTag(getName(), getValue().shortValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\tag\builtin\ShortTag.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */