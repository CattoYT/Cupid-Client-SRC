package us.myles.viaversion.libs.javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import us.myles.viaversion.libs.javassist.ClassPool;
import us.myles.viaversion.libs.javassist.bytecode.ConstPool;

public class StringMemberValue extends MemberValue {
  int valueIndex;
  
  public StringMemberValue(int index, ConstPool cp) {
    super('s', cp);
    this.valueIndex = index;
  }
  
  public StringMemberValue(String str, ConstPool cp) {
    super('s', cp);
    setValue(str);
  }
  
  public StringMemberValue(ConstPool cp) {
    super('s', cp);
    setValue("");
  }
  
  Object getValue(ClassLoader cl, ClassPool cp, Method m) {
    return getValue();
  }
  
  Class<?> getType(ClassLoader cl) {
    return String.class;
  }
  
  public String getValue() {
    return this.cp.getUtf8Info(this.valueIndex);
  }
  
  public void setValue(String newValue) {
    this.valueIndex = this.cp.addUtf8Info(newValue);
  }
  
  public String toString() {
    return "\"" + getValue() + "\"";
  }
  
  public void write(AnnotationsWriter writer) throws IOException {
    writer.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor visitor) {
    visitor.visitStringMemberValue(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\annotation\StringMemberValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */