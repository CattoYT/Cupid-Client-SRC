package us.myles.viaversion.libs.javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import us.myles.viaversion.libs.javassist.ClassPool;
import us.myles.viaversion.libs.javassist.bytecode.ConstPool;

public class CharMemberValue extends MemberValue {
  int valueIndex;
  
  public CharMemberValue(int index, ConstPool cp) {
    super('C', cp);
    this.valueIndex = index;
  }
  
  public CharMemberValue(char c, ConstPool cp) {
    super('C', cp);
    setValue(c);
  }
  
  public CharMemberValue(ConstPool cp) {
    super('C', cp);
    setValue(false);
  }
  
  Object getValue(ClassLoader cl, ClassPool cp, Method m) {
    return Character.valueOf(getValue());
  }
  
  Class<?> getType(ClassLoader cl) {
    return char.class;
  }
  
  public char getValue() {
    return (char)this.cp.getIntegerInfo(this.valueIndex);
  }
  
  public void setValue(char newValue) {
    this.valueIndex = this.cp.addIntegerInfo(newValue);
  }
  
  public String toString() {
    return Character.toString(getValue());
  }
  
  public void write(AnnotationsWriter writer) throws IOException {
    writer.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor visitor) {
    visitor.visitCharMemberValue(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\annotation\CharMemberValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */