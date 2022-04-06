package us.myles.viaversion.libs.javassist.convert;

import us.myles.viaversion.libs.javassist.CannotCompileException;
import us.myles.viaversion.libs.javassist.CtClass;
import us.myles.viaversion.libs.javassist.bytecode.CodeAttribute;
import us.myles.viaversion.libs.javassist.bytecode.CodeIterator;
import us.myles.viaversion.libs.javassist.bytecode.ConstPool;
import us.myles.viaversion.libs.javassist.bytecode.Descriptor;
import us.myles.viaversion.libs.javassist.bytecode.StackMap;
import us.myles.viaversion.libs.javassist.bytecode.StackMapTable;

public final class TransformNew extends Transformer {
  private int nested;
  
  private String classname;
  
  private String trapClass;
  
  private String trapMethod;
  
  public TransformNew(Transformer next, String classname, String trapClass, String trapMethod) {
    super(next);
    this.classname = classname;
    this.trapClass = trapClass;
    this.trapMethod = trapMethod;
  }
  
  public void initialize(ConstPool cp, CodeAttribute attr) {
    this.nested = 0;
  }
  
  public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp) throws CannotCompileException {
    int c = iterator.byteAt(pos);
    if (c == 187) {
      int index = iterator.u16bitAt(pos + 1);
      if (cp.getClassInfo(index).equals(this.classname)) {
        if (iterator.byteAt(pos + 3) != 89)
          throw new CannotCompileException("NEW followed by no DUP was found"); 
        iterator.writeByte(0, pos);
        iterator.writeByte(0, pos + 1);
        iterator.writeByte(0, pos + 2);
        iterator.writeByte(0, pos + 3);
        this.nested++;
        StackMapTable smt = (StackMapTable)iterator.get().getAttribute("StackMapTable");
        if (smt != null)
          smt.removeNew(pos); 
        StackMap sm = (StackMap)iterator.get().getAttribute("StackMap");
        if (sm != null)
          sm.removeNew(pos); 
      } 
    } else if (c == 183) {
      int index = iterator.u16bitAt(pos + 1);
      int typedesc = cp.isConstructor(this.classname, index);
      if (typedesc != 0 && this.nested > 0) {
        int methodref = computeMethodref(typedesc, cp);
        iterator.writeByte(184, pos);
        iterator.write16bit(methodref, pos + 1);
        this.nested--;
      } 
    } 
    return pos;
  }
  
  private int computeMethodref(int typedesc, ConstPool cp) {
    int classIndex = cp.addClassInfo(this.trapClass);
    int mnameIndex = cp.addUtf8Info(this.trapMethod);
    typedesc = cp.addUtf8Info(
        Descriptor.changeReturnType(this.classname, cp
          .getUtf8Info(typedesc)));
    return cp.addMethodrefInfo(classIndex, cp
        .addNameAndTypeInfo(mnameIndex, typedesc));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\convert\TransformNew.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */