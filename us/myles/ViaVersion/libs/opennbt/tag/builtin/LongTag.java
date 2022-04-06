package us.myles.viaversion.libs.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends Tag {
  private long value;
  
  public LongTag(String name) {
    this(name, 0L);
  }
  
  public LongTag(String name, long value) {
    super(name);
    this.value = value;
  }
  
  public Long getValue() {
    return Long.valueOf(this.value);
  }
  
  public void setValue(long value) {
    this.value = value;
  }
  
  public void read(DataInput in) throws IOException {
    this.value = in.readLong();
  }
  
  public void write(DataOutput out) throws IOException {
    out.writeLong(this.value);
  }
  
  public LongTag clone() {
    return new LongTag(getName(), getValue().longValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\tag\builtin\LongTag.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */