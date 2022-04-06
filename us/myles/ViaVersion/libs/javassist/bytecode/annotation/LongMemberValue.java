package us.myles.viaversion.libs.javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import us.myles.viaversion.libs.javassist.ClassPool;
import us.myles.viaversion.libs.javassist.bytecode.ConstPool;

public class LongMemberValue extends MemberValue {
  int valueIndex;
  
  public LongMemberValue(int index, ConstPool cp) {
    super('J', cp);
    this.valueIndex = index;
  }
  
  public LongMemberValue(long j, ConstPool cp) {
    super('J', cp);
    setValue(j);
  }
  
  public LongMemberValue(ConstPool cp) {
    super('J', cp);
    setValue(0L);
  }
  
  Object getValue(ClassLoader cl, ClassPool cp, Method m) {
    return Long.valueOf(getValue());
  }
  
  Class<?> getType(ClassLoader cl) {
    return long.class;
  }
  
  public long getValue() {
    return this.cp.getLongInfo(this.valueIndex);
  }
  
  public void setValue(long newValue) {
    this.valueIndex = this.cp.addLongInfo(newValue);
  }
  
  public String toString() {
    return Long.toString(getValue());
  }
  
  public void write(AnnotationsWriter writer) throws IOException {
    writer.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor visitor) {
    visitor.visitLongMemberValue(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\annotation\LongMemberValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */