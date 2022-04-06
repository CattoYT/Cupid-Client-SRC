package us.myles.viaversion.libs.javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import us.myles.viaversion.libs.javassist.ClassPool;
import us.myles.viaversion.libs.javassist.bytecode.ConstPool;

public class AnnotationMemberValue extends MemberValue {
  Annotation value;
  
  public AnnotationMemberValue(ConstPool cp) {
    this(null, cp);
  }
  
  public AnnotationMemberValue(Annotation a, ConstPool cp) {
    super('@', cp);
    this.value = a;
  }
  
  Object getValue(ClassLoader cl, ClassPool cp, Method m) throws ClassNotFoundException {
    return AnnotationImpl.make(cl, getType(cl), cp, this.value);
  }
  
  Class<?> getType(ClassLoader cl) throws ClassNotFoundException {
    if (this.value == null)
      throw new ClassNotFoundException("no type specified"); 
    return loadClass(cl, this.value.getTypeName());
  }
  
  public Annotation getValue() {
    return this.value;
  }
  
  public void setValue(Annotation newValue) {
    this.value = newValue;
  }
  
  public String toString() {
    return this.value.toString();
  }
  
  public void write(AnnotationsWriter writer) throws IOException {
    writer.annotationValue();
    this.value.write(writer);
  }
  
  public void accept(MemberValueVisitor visitor) {
    visitor.visitAnnotationMemberValue(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\annotation\AnnotationMemberValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */