package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class ArrayInit extends ASTList {
  private static final long serialVersionUID = 1L;
  
  public ArrayInit(ASTree firstElement) {
    super(firstElement);
  }
  
  public int size() {
    int s = length();
    if (s == 1 && head() == null)
      return 0; 
    return s;
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atArrayInit(this);
  }
  
  public String getTag() {
    return "array";
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\ArrayInit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */