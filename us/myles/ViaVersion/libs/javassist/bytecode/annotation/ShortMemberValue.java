package us.myles.viaversion.libs.javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import us.myles.viaversion.libs.javassist.ClassPool;
import us.myles.viaversion.libs.javassist.bytecode.ConstPool;

public class ShortMemberValue extends MemberValue {
  int valueIndex;
  
  public ShortMemberValue(int index, ConstPool cp) {
    super('S', cp);
    this.valueIndex = index;
  }
  
  public ShortMemberValue(short s, ConstPool cp) {
    super('S', cp);
    setValue(s);
  }
  
  public ShortMemberValue(ConstPool cp) {
    super('S', cp);
    setValue((short)0);
  }
  
  Object getValue(ClassLoader cl, ClassPool cp, Method m) {
    return Short.valueOf(getValue());
  }
  
  Class<?> getType(ClassLoader cl) {
    return short.class;
  }
  
  public short getValue() {
    return (short)this.cp.getIntegerInfo(this.valueIndex);
  }
  
  public void setValue(short newValue) {
    this.valueIndex = this.cp.addIntegerInfo(newValue);
  }
  
  public String toString() {
    return Short.toString(getValue());
  }
  
  public void write(AnnotationsWriter writer) throws IOException {
    writer.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor visitor) {
    visitor.visitShortMemberValue(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\annotation\ShortMemberValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */