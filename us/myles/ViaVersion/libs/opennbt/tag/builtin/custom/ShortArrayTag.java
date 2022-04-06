package us.myles.viaversion.libs.opennbt.tag.builtin.custom;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class ShortArrayTag extends Tag {
  private short[] value;
  
  public ShortArrayTag(String name) {
    this(name, new short[0]);
  }
  
  public ShortArrayTag(String name, short[] value) {
    super(name);
    this.value = value;
  }
  
  public short[] getValue() {
    return (short[])this.value.clone();
  }
  
  public void setValue(short[] value) {
    if (value == null)
      return; 
    this.value = (short[])value.clone();
  }
  
  public short getValue(int index) {
    return this.value[index];
  }
  
  public void setValue(int index, short value) {
    this.value[index] = value;
  }
  
  public int length() {
    return this.value.length;
  }
  
  public void read(DataInput in) throws IOException {
    this.value = new short[in.readInt()];
    for (int index = 0; index < this.value.length; index++)
      this.value[index] = in.readShort(); 
  }
  
  public void write(DataOutput out) throws IOException {
    out.writeInt(this.value.length);
    for (int index = 0; index < this.value.length; index++)
      out.writeShort(this.value[index]); 
  }
  
  public ShortArrayTag clone() {
    return new ShortArrayTag(getName(), getValue());
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\opennbt\tag\builtin\custom\ShortArrayTag.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */