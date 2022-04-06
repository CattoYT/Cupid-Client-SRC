package us.myles.viaversion.libs.javassist.expr;

import us.myles.viaversion.libs.javassist.CtClass;
import us.myles.viaversion.libs.javassist.CtConstructor;
import us.myles.viaversion.libs.javassist.CtMethod;
import us.myles.viaversion.libs.javassist.NotFoundException;
import us.myles.viaversion.libs.javassist.bytecode.CodeIterator;
import us.myles.viaversion.libs.javassist.bytecode.MethodInfo;

public class ConstructorCall extends MethodCall {
  protected ConstructorCall(int pos, CodeIterator i, CtClass decl, MethodInfo m) {
    super(pos, i, decl, m);
  }
  
  public String getMethodName() {
    return isSuper() ? "super" : "this";
  }
  
  public CtMethod getMethod() throws NotFoundException {
    throw new NotFoundException("this is a constructor call.  Call getConstructor().");
  }
  
  public CtConstructor getConstructor() throws NotFoundException {
    return getCtClass().getConstructor(getSignature());
  }
  
  public boolean isSuper() {
    return super.isSuper();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\expr\ConstructorCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */